package com.example.letsgo


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.letsgo.taskList
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
//import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.util.logging.Logger

class GroupFormActivity : AppCompatActivity() , RequestsAdapter.RequestAdapterItemClickListener{

    private lateinit var imageView: ImageView
    val db = Firebase.firestore
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    private var currentImageUri: Uri? = Uri.parse("")
    var Logg = Logger.getLogger("taskform")
    lateinit var uriValue :String
    lateinit var nameValue :String
    lateinit var descValue :String
    lateinit var selectedUsers : ArrayList<String>
    lateinit var myAdapter: RequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_form)
        selectedUsers = intent.getStringArrayListExtra("selectedUsers") ?: arrayListOf()
        uriValue = intent.getStringExtra("uri") ?: ""
        nameValue = intent.getStringExtra("grpName")?: ""
        descValue = intent.getStringExtra("grpDesc")?: ""
        val titleText = findViewById<EditText>(R.id.editTextGroupName)
        val descriptionText = findViewById<EditText>(R.id.editTextGroupDescription)
        val imageSource = findViewById<ImageView>(R.id.imageView4)
        val rview = findViewById<RecyclerView>(R.id.addedMembers)
        var usrListC : MutableList<User> = mutableListOf()
        if (selectedUsers.size > 0){
            for( usr in selectedUsers){
                db.collection("users").document(usr)
                    .get()
                    .addOnSuccessListener { doc ->
                        var user5 = User(
                            doc.id,
                            doc.getString("Username"),
                            doc.getString("description"),
                            doc.getString("email")!!,
                            doc.getString("phonenumber"),
                            false,
                            arrayListOf(),
                            doc.getString("uri")!!
                        )
                        usrListC.add(user5)
//                        Toast.makeText(
//                            this,
//                            "user added",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        myAdapter = RequestsAdapter(usrListC)
                        myAdapter.setMyItemClickListener(this)
                        rview.layoutManager = LinearLayoutManager(this)
                        rview.adapter = myAdapter
                    }
                    .addOnFailureListener { e ->

                    }
            }
        }
//        myAdapter = RequestsAdapter(usrListC)
//        myAdapter.setMyItemClickListener(this)
//        rview.layoutManager = LinearLayoutManager(this)
//        rview.adapter = myAdapter
        if ( nameValue != "") {
            titleText.setText(nameValue)
        }
        if( descValue != "") {
            descriptionText.setText(descValue)
        }
        if( uriValue != "") {
            imageSource.setImageURI(Uri.parse(uriValue))
        }


        imageView = findViewById<ImageView>(R.id.imageView4)


        val buttonGallery = findViewById<Button>(R.id.chooseGrpPhoto)
        buttonGallery.setOnClickListener { openGallery() }

        val submitButton = findViewById<Button>(R.id.submitNewGroup)
        submitButton.setOnClickListener { addGroup() }

        val addMembersButton = findViewById<Button>(R.id.addMemberButton)
        addMembersButton.setOnClickListener{ addmembers() }

    }
    private fun addmembers() {
        val titleText = findViewById<EditText>(R.id.editTextGroupName)
        val descriptionText = findViewById<EditText>(R.id.editTextGroupDescription)
        val intent = Intent(this, UserSelectActivity::class.java).apply {
            putExtra("uri", currentImageUri.toString() )
            putExtra("grpName", titleText.text.toString() )
            putExtra("grpDesc", descriptionText.text.toString())
        }
        startActivity(intent)
    }




    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
                }

                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data.data
                    currentImageUri = selectedImageUri
                    imageView.setImageURI(selectedImageUri)
                }
            }
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        internal const val GALLERY_REQUEST_CODE = 101
    }

    private fun addGroup() {
        val titleText = findViewById<EditText>(R.id.editTextGroupName)
        val descriptionText = findViewById<EditText>(R.id.editTextGroupDescription)
        val imageSource = findViewById<ImageView>(R.id.imageView4)


        val imageName = UUID.randomUUID().toString()
        val storageReference: StorageReference = storage.reference.child("images/$imageName")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentImageUri?.let {
                storageReference.putFile(it)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            //Logg.warning(dateText.text.toString())
//                            val task = Task( titleText.text.toString(), descriptionText.text.toString(),
//                                sdfDayDate.parse(dateText.text.toString()), "personel", uri
//                            )
                            selectedUsers.add(user.uid)
                            val grp1 = hashMapOf(
                                "title" to titleText.text.toString(),
                                "description" to descriptionText.text.toString(),
                                "created_user" to user.uid,
                                "imageuri" to uri,
                                "members" to selectedUsers
                            )
                           // Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
                            db.collection("groups")
                                .add(grp1)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot added with ID: ${documentReference.id}"

                                    )
                                    var arr = arrayListOf<String>()
                                    for(user3 in selectedUsers){
                                        //if(user3 != user.uid){
                                        Logger.getLogger("hi").warning(user3)
                                            var doc = db.collection("users").document(user3)
                                            doc.update("groups", FieldValue.arrayUnion(documentReference.id))
                                                .addOnSuccessListener {
                                                    // Element added successfully
                                                   // Toast.makeText(this, "hello grp added to user", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    // Handle the error
                                                }

                                        //}
                                    }
//                                    Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT)
//                                        .show()
                                    val intent = Intent(this, GroupActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }

                            //taskList.add(task)
                            //adapter.notifyDataSetChanged()

//                            Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT)
//                                .show()
//                            val intent = Intent(this, GroupActivity::class.java)
//                            startActivity(intent)
                        }
                    }.addOnFailureListener { e ->
//                        Toast.makeText(
//                            this,
//                            "Image upload failed: ${e.message}",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        Log.e("FirebaseUpload", "Error uploading image", e)
                    }
            }
        }


    }
}