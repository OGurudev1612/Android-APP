package com.example.letsgo

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

//import com.example.letsgo.taskList

class TaskActivity : AppCompatActivity(), TaskListFragment.Parent,NavigationView.OnNavigationItemSelectedListener {

    lateinit var fragmentTaskList : TaskListFragment
    lateinit var appBar: ActionBar
    lateinit var mainAct: DrawerLayout
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mainAct = findViewById<DrawerLayout>(R.id.mainAct)
        val navView = findViewById<NavigationView>(R.id.navView)
        setSupportActionBar(findViewById(R.id.toolbar))
        appBar = supportActionBar!!
        var grpid = intent.getStringExtra("grpid").toString()
        var grptitle = intent.getStringExtra("grptitle").toString()
        if (savedInstanceState == null){
            val args = Bundle()
            args.putString("grpid", grpid)
            args.putString("grptitle", grptitle)
            fragmentTaskList = TaskListFragment()
            fragmentTaskList.setParent(this)
            fragmentTaskList.arguments = args
            supportFragmentManager.beginTransaction().replace(R.id.taskFragmentContainer, fragmentTaskList).commit()
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
                        Glide.with(this@TaskActivity)
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

    override fun load(fragmentTask: TaskFragment) {
        //TODO("Not yet implemented")
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.flip_enter,
            R.anim.flip_out,
        )
        fragmentTransaction
            .replace(
                R.id.taskFragmentContainer,
                fragmentTask
            )
            .addToBackStack(null)
            .commit()
    }

    override fun addTaskFabclicked( grpid : String, grptitle : String){
        val intent = Intent(this, TaskFormActivity::class.java).apply {
            putExtra("grpid", grpid)
            putExtra("grptitle", grptitle)
        }
        startActivity(intent)
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