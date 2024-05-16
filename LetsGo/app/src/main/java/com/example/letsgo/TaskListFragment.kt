package com.example.letsgo

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.logging.Logger
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [TaskListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskListFragment : Fragment(), TaskAdapter.MyItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var myParent: Parent? = null
    lateinit var myAdapter: TaskAdapter
    val Log = Logger.getLogger(TaskListFragment::class.java.name)
    val db = Firebase.firestore
    lateinit var finalgrp : String
    val user = FirebaseAuth.getInstance().currentUser


    interface Parent {
        fun load(fragmentTask: TaskFragment)
        fun addTaskFabclicked(grpid : String, grptitle : String)
    }
    fun setParent(parent : Parent) {
        this.myParent = parent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var grpid = arguments?.getString("grpid")
        var grptitle = arguments?.getString("grptitle")
        var taskListC : MutableList<Task> = mutableListOf()
        // Inflate the layout for this fragment
        val rootview =  inflater.inflate(R.layout.fragment_task_list, container, false)
        val head = rootview.findViewById<TextView>(R.id.taskheadtext)
        val rview = rootview.findViewById<RecyclerView>(R.id.taskRView)

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // We don't want to support move in this case
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Remove the item from the adapter
                myAdapter.removeItem(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rview)


        Log.warning("here hre here here hre")
        Log.warning(grpid.toString())
        if ( grpid == "null" ) {
            head.text = " All tasks "
            Log.warning("enter if")
            db.collection("users").document(user?.uid.toString())
                .get()
                .addOnSuccessListener { docs ->
                    var groups = docs.get("groups") as ArrayList<String>


                    db.collection("tasks")
                        .whereIn("group",groups)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                //Log.d(TAG, "${document.id} => ${document.data}")
                                var grpname = document.getString("group").toString()
                                db.collection("groups").document(grpname)
                                    .get().addOnSuccessListener { doc ->
                                        grpname = doc.getString("title").toString()
                                        finalgrp = grpname
                                        Log.warning(document.data.toString())
                                        var task = Task(
                                            document.id.toString(),
                                            document.data.getValue("title").toString(),
                                            document.data.get("description").toString(),
                                            document.getTimestamp("date")!!.toDate(),
                                            document.getTimestamp("time")!!.toDate(),
                                            grpname,
                                            Uri.parse(document.getString("imageuri"))
                                        )
                                        taskListC.add(task)
                                        taskListC.sortBy { it.date }
                                        myAdapter = TaskAdapter(taskListC)
                                        myAdapter.setMyItemClickListener(this)
                                        rview.layoutManager = LinearLayoutManager(this.context)
                                        rview.adapter = myAdapter
                                    }.addOnFailureListener { e ->

                                    }

                            }
                            Log.warning(taskListC.size.toString())

                        }
                        .addOnFailureListener { exception ->
                            Log.warning("Error getting documents." + exception.toString())
                        }
                }
        }else{
            head.text = grptitle
            Log.warning("enter else")
            db.collection("tasks")
                .whereEqualTo("group", grpid)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        //Log.d(TAG, "${document.id} => ${document.data}")
                        Log.warning(document.data.toString())
                        finalgrp = grptitle.toString()
                        var task = Task(
                            document.id.toString(),
                            document.data.getValue("title").toString(),
                            document.data.get("description").toString(),
                            document.getTimestamp("date")!!.toDate(),
                            document.getTimestamp("time")!!.toDate(),
                            grptitle.toString(),
                            Uri.parse(document.getString("imageuri"))
                        )
                        taskListC.add(task)
                    }
                    Log.warning(taskListC.size.toString())
                    taskListC.sortBy { it.date }
                    myAdapter = TaskAdapter(taskListC)
                    myAdapter.setMyItemClickListener(this)
                    rview.layoutManager = LinearLayoutManager(this.context)
                    rview.adapter = myAdapter
                }
                .addOnFailureListener { exception ->
                    Log.warning("Error getting documents." + exception.toString())
                }
        }
        val fab: FloatingActionButton = rootview.findViewById(R.id.addTaskFab)
        fab.setOnClickListener {
            // Handle the FAB click action
            if (grpid != null) {
                if (grptitle != null) {
                    myParent?.addTaskFabclicked(grpid,grptitle)
                }
            }
        }

        return rootview
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.menutasklist, menu)

        val search = menu.findItem(R.id.taskSearchIcon)!!.actionView as SearchView
        if ( search != null ){
            search.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    myAdapter.search(query)
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }

        // Define the listener
        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when action item collapses
                Log.warning("collasped")
                myAdapter.backToOriginal()
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do something when expanded
                return true // Return true to expand action view
            }
        }
        // Get the MenuItem for the action item
        val actionMenuItem = menu?.findItem(R.id.taskSearchIcon)
        // Assign the listener to that action item
        actionMenuItem?.setOnActionExpandListener(expandListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.taskSort1 -> {
                myAdapter.sortByDate()
            }
            R.id.taskSort2 -> {
                myAdapter.sortByDateDesc()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClickedFromAdapter(task : Task) {
        val sdfDayDate = SimpleDateFormat("EEE MMM dd, YYYY", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val args = Bundle()
        args.putString("title", task.title)
        args.putString("description", task.description)
        args.putString("date", sdfDayDate.format(task.date))
        args.putString("time", sdfTime.format(task.time))
        args.putString("group",task.group)
        args.putString("id",task.id)
        args.putString("uri",task.imageuri.toString())
        val fragmentTask = TaskFragment()
        fragmentTask.arguments = args
        myParent?.load(fragmentTask)


    }

    override fun onItemLongClickedFromAdapter(position: Int) {
        TODO("Not yet implemented")
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaskListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}