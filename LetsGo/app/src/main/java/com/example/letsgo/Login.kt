package com.example.letsgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import com.example.letsgo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.logging.Logger

class Login : AppCompatActivity(), ForgotPasswordFragment.Parent {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        val notRegistered = findViewById<TextView>(R.id.notRegistered)
        val forgotPassword = findViewById<TextView>(R.id.forgotPass)
        val loginForm = findViewById<LinearLayout>(R.id.loginForm)
        val forgotPassFragment = findViewById<FragmentContainerView>(R.id.forgotPassFragment)

        forgotPassword.setOnClickListener{
            val t =Logger.getLogger("test Forgot passcode")
            t.warning("****************************")
            loginForm.visibility = View.GONE
            forgotPassFragment.visibility = View.VISIBLE
            val fpFragment = ForgotPasswordFragment()
            fpFragment.setParent(this)
            supportFragmentManager.beginTransaction().replace(R.id.forgotPassFragment, fpFragment).commit()

        }
        notRegistered.setOnClickListener{
            val t =Logger.getLogger("REgister")
            t.warning("########################")
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser !=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}