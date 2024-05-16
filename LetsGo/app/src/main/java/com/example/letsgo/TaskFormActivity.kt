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
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
//import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.util.logging.Logger

class TaskFormActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    val db = Firebase.firestore
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    private var currentImageUri: Uri? = null
    var Logg = Logger.getLogger("taskform")
    lateinit var grpid : String
    lateinit var grptitle : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_form)

        grpid = intent.getStringExtra("grpid").toString()
        grptitle = intent.getStringExtra("grptitle").toString()
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        editTextDate.setOnClickListener { showDatePickerDialog(editTextDate) }

        val editTextTime = findViewById<EditText>(R.id.editTextTime)
        editTextTime.setOnClickListener { showTimePickerDialog(editTextTime) }

        imageView = findViewById<ImageView>(R.id.imageView)

        val buttonCamera = findViewById<Button>(R.id.buttonCamera)
        buttonCamera.setOnClickListener { openCamera() }

        val buttonGallery = findViewById<Button>(R.id.buttonGallery)
        buttonGallery.setOnClickListener { openGallery() }

        val submitButton = findViewById<Button>(R.id.submit_button)
        submitButton.setOnClickListener { addTask() }

    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "${selectedMonth + 1}/${selectedDayOfMonth}/$selectedYear"
                editText.setText(selectedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                // Convert 24-hour time to 12-hour format
                val hourFormatted =
                    if (selectedHour == 0 || selectedHour == 12) 12 else selectedHour % 12
                val amPm = if (selectedHour < 12) "AM" else "PM"
                val selectedTime =
                    String.format("%02d:%02d %s", hourFormatted, selectedMinute, amPm)
                editText.setText(selectedTime)
            }, hour, minute, false
        ) // false for 12-hour format

        timePickerDialog.show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
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
        private const val GALLERY_REQUEST_CODE = 101
    }

    private fun addTask() {
        val titleText = findViewById<EditText>(R.id.titleInput)
        val descriptionText = findViewById<EditText>(R.id.taskDescInput)
        val dateText = findViewById<EditText>(R.id.editTextDate)
        val timeText = findViewById<EditText>(R.id.editTextTime)
        val imageSource = findViewById<ImageView>(R.id.imageView)
        val sdfDayDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val comdate = SimpleDateFormat("MM/dd/yyyy hh:mm a",Locale.getDefault())


        val imageName = UUID.randomUUID().toString()
        val storageReference: StorageReference = storage.reference.child("images/$imageName")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentImageUri?.let {
                storageReference.putFile(it)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            Logg.warning(dateText.text.toString())
                            var grpn = ""
                            db.collection("users").document(user.uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    var grps = document.get("groups") as? ArrayList<String>
                                    if (grpid == "null"){
                                        Logg.warning("jokeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                                        Logg.warning("enter if")
                                        grpn = grps?.get(0) ?: ""
                                    }else{
                                        Logg.warning("jokeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                                        Logg.warning("enter else")
                                        grpn = grpid
                                    }

                                    val task1 = hashMapOf(
                                        "title" to titleText.text.toString(),
                                        "description" to descriptionText.text.toString(),
                                        "date" to comdate.parse(dateText.text.toString()+ " "+timeText.text.toString()),
                                        "time" to sdfTime.parse(timeText.text.toString()),
                                        "group" to grpn,
                                        "imageuri" to uri
                                    )
                                   // Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
                                    db.collection("tasks")
                                        .add(task1)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d(
                                                TAG,
                                                "DocumentSnapshot added with ID: ${documentReference.id}"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding document", e)
                                        }


//                                    Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT)
//                                        .show()
                                    //val intent = Intent(this, TaskActivity::class.java)
                                    val intent = Intent(this, TaskActivity::class.java).apply {
                                        putExtra("grpid", grpid )
                                    putExtra("grptitle", grptitle)
                                    }
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e -> }
//                            val task1 = hashMapOf(
//                                "title" to titleText.text.toString(),
//                                "description" to descriptionText.text.toString(),
//                                "date" to sdfDayDate.parse(dateText.text.toString()),
//                                "time" to sdfTime.parse(timeText.text.toString()),
//                                "group" to grpn,
//                                "imageuri" to uri
//                            )
//                            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
//                            db.collection("tasks")
//                                .add(task1)
//                                .addOnSuccessListener { documentReference ->
//                                    Log.d(
//                                        TAG,
//                                        "DocumentSnapshot added with ID: ${documentReference.id}"
//                                    )
//                                }
//                                .addOnFailureListener { e ->
//                                    Log.w(TAG, "Error adding document", e)
//                                }
//
//
//                            Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT)
//                                .show()
//                            val intent = Intent(this, TaskActivity::class.java)
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