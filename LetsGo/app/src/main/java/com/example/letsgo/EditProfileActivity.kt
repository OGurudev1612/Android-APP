package com.example.letsgo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import java.util.UUID
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var imageView: ImageView
    private var currentImageUri: Uri? = Uri.parse("")
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference

//    private val saveProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            val updatedName = data?.getStringExtra("updatedName")
//            val updatedDesc = data?.getStringExtra("updatedDesc")
//            val updatedMobile = data?.getStringExtra("updatedMobile")
//
//            val resultIntent = Intent()
//            resultIntent.putExtra("updatedName", updatedName)
//            resultIntent.putExtra("updatedDesc", updatedDesc)
//            resultIntent.putExtra("updatedMobile", updatedMobile)
//            setResult(Activity.RESULT_OK, resultIntent)
//            finish()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emailform)
        //val textView = findViewById<TextView>(R.id.emailinfo) // Replace with your TextView ID

        val email = "aklnkl079@gmail.com"
        val formattedText = "<b><big>email</big></b>: $email"

        //textView.text = Html.fromHtml(formattedText)

        val saveButton: Button = findViewById(R.id.savebutton)
        val nameEditText: EditText = findViewById(R.id.enternameuser)
        val descEditText: EditText = findViewById(R.id.descInput)
        val mobileEditText: EditText = findViewById(R.id.enterphonenum)
        val buttonGallery = findViewById<Button>(R.id.cfgb)
        imageView = findViewById<ImageView>(R.id.imageView6)
        buttonGallery.setOnClickListener { openGallery() }

        saveButton.setOnClickListener {
            val updatedName = nameEditText.text.toString()
            val updatedDesc = descEditText.text.toString()
            val updatedMobile = mobileEditText.text.toString()

            val imageName = UUID.randomUUID().toString()
            val storageReference: StorageReference = storage.reference.child("images/$imageName")
            val user = FirebaseAuth.getInstance().currentUser

            currentImageUri?.let {
                storageReference.putFile(it)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            var user1 = hashMapOf(
                                "username" to updatedName,
                                "description" to updatedDesc,
                                "phonenumber" to updatedMobile,
                                "uri" to uri
                            )
                            if (user != null) {
                                db.collection("users").document(user.uid)
                                    .set(user1, SetOptions.merge())
                                    .addOnSuccessListener {
                                        val intent = Intent(this, ProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {  }
                            }
                        }

                    }
                    .addOnFailureListener { e -> }
            }

        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GroupFormActivity.GALLERY_REQUEST_CODE)
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
        private const val GALLERY_REQUEST_CODE = 101
    }
}
