package com.example.letsgo

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.letsgo.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        val accountExists = findViewById<TextView>(R.id.accountExists)
        accountExists.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmPassword = binding.confirmPassEt.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if(it.isSuccessful){
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                val userId = user.uid
                                val grp1 = hashMapOf(
                                    "title" to "Personal",
                                    "description" to "My Tasks",
                                    "created_user" to userId,
                                    "imageuri" to "",
                                    "members" to arrayListOf<String>(userId)
                                )
                                db.collection("groups")
                                    .add(grp1)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(
                                            ContentValues.TAG,
                                            "DocumentSnapshot added with ID: ${documentReference.id}"
                                        )
                                        var user1 = hashMapOf(
                                            "username" to "",
                                            "description" to "",
                                            "phonenumber" to "",
                                            "email" to user.email,
                                            "groups" to arrayListOf<String>(documentReference.id),
                                            "uri" to ""
                                        )
                                        db.collection("users").document(userId).set(user1)
                                            .addOnSuccessListener {
                                        // Document added successfully
                                            }
                                            .addOnFailureListener { e ->
                                        // Handle failure
                                            }

                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(ContentValues.TAG, "Error adding document", e)
                                    }


                            }
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Password is not Matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}