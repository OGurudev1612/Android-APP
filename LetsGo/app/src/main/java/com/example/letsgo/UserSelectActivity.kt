package com.example.letsgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.letsgo.taskList

class UserSelectActivity : AppCompatActivity(), UserSelectListFragment.Parent {

    lateinit var fragmentSelectUserList : UserSelectListFragment
    lateinit var appBar: ActionBar
    lateinit var uriValue :String
    lateinit var nameValue :String
    lateinit var descValue :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uriValue = intent.getStringExtra("uri").toString()
        nameValue = intent.getStringExtra("grpName").toString()
        descValue = intent.getStringExtra("grpDesc").toString()
        setContentView(R.layout.activity_recyclerview)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))
        appBar = supportActionBar!!
        if (savedInstanceState == null){
            fragmentSelectUserList = UserSelectListFragment()
            fragmentSelectUserList.setParent(this)
            supportFragmentManager.beginTransaction().replace(R.id.taskFragmentContainer, fragmentSelectUserList).commit()
        }
    }

    override fun doneFabclicked(selectedUsers : ArrayList<String>) {
        val intent = Intent(this, GroupFormActivity::class.java).apply {
            putExtra("uri", uriValue )
            putExtra("grpName", nameValue )
            putExtra("grpDesc", descValue)
        }
        intent.putStringArrayListExtra("selectedUsers", selectedUsers)
        startActivity(intent)
    }


}