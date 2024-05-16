package com.example.letsgo


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.letsgo.taskList

class UserActivity : AppCompatActivity(), UserListFragment.Parent {

    lateinit var fragmentUserList : UserListFragment
    lateinit var appBar: ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))
        appBar = supportActionBar!!
        if (savedInstanceState == null){
            fragmentUserList = UserListFragment()
            fragmentUserList.setParent(this)
            supportFragmentManager.beginTransaction().replace(R.id.taskFragmentContainer, fragmentUserList).commit()
        }
    }




}