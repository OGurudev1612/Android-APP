package com.example.letsgo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.logging.Logger

class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var nameTextView: TextView
    private lateinit var descTextView: TextView
    private lateinit var mobileTextView: TextView
    private lateinit var mailTextView: TextView
    val db = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser
    lateinit var mainAct: DrawerLayout

//    private val editProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            val updatedName = data?.getStringExtra("updatedName")
//            val updatedDesc = data?.getStringExtra("updatedDesc")
//            val updatedMobile = data?.getStringExtra("updatedMobile")
//
//            nameTextView.text = updatedName
//            descTextView.text = updatedDesc
//            mobileTextView.text = updatedMobile
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))
        mainAct = findViewById(R.id.mainAct)
        val navView = findViewById<NavigationView>(R.id.navView)
        nameTextView = findViewById(R.id.profilename)
        mailTextView = findViewById<TextView>(R.id.profileemail)
        descTextView = findViewById(R.id.profiledesc)
        mobileTextView = findViewById(R.id.profilenumber)
        var profileimage = findViewById<ImageView>(R.id.profile)
        var doc = db.collection("users").document(user?.uid.toString())
        doc.get().addOnSuccessListener { document ->
            nameTextView.text = document.getString("username")
            descTextView.text = document.getString("description")
            mobileTextView.text = document.getString("phonenumber")
            mailTextView.text = document.getString("email")
            Logger.getLogger("jk").warning(document.getString("uri").toString())
            if( document.getString("uri").toString() != "null") {
                Glide.with(this)
                    .load(document.getString("uri").toString())
                    .into(profileimage);
            }else{
                profileimage.setImageResource(R.drawable.epp)
            }
        }

        val editProfileButton: Button = findViewById(R.id.profilebutton)
        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(this, mainAct, toolbar, R.string.nav_open, R.string.nav_close)
        mainAct.addDrawerListener(toggle)
        mainAct.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerOpened(drawerView: View) {
                val headerView = navView.getHeaderView(0)
                val logintext = findViewById<TextView>(R.id.loginuser)
                val imageviewuser = findViewById<ImageView>(R.id.imageView5)

                // Set the values for the header views

                db.collection("users").document(user?.uid.toString())
                    .get()
                    .addOnSuccessListener { doc ->
                        var username = doc.getString("username")
                        logintext.text = "Hi " + username +" !"
                        var uri = doc.getString("uri")
                        Glide.with(this@ProfileActivity)
                            .load(uri)
                            .into(imageviewuser);
                    }
            }

            override fun onDrawerClosed(drawerView: View) { /* ... */ }
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) { /* ... */ }
            override fun onDrawerStateChanged(newState: Int) { /* ... */ }
        })
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_item_1 -> {
                val intent = Intent(this, GroupActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_item_2 -> {
                val intent = Intent(this, TaskActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_item_3 -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_item_4 -> {
                val auth = FirebaseAuth.getInstance()

                // Sign out
                auth.signOut()

                // Update UI or navigate to another activity
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        mainAct.closeDrawer(GravityCompat.START)
        return true
    }
}

