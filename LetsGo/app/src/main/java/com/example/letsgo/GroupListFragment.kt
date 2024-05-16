package com.example.letsgo

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.logging.Logger

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GroupListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupListFragment : Fragment(), GroupAdapter.GroupAdapterItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var myParent: GroupListFragment.Parent? = null
    lateinit var myAdapter: GroupAdapter
    val Log = Logger.getLogger(GroupListFragment::class.java.name)
    val db = Firebase.firestore

    interface Parent {
        fun load(fragmentGroup: GroupFragment)
        fun addGroupFabclicked()
    }
    fun setParent(parent : GroupListFragment.Parent) {
        this.myParent = parent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview =  inflater.inflate(R.layout.fragment_group_list, container, false)
        val rview = rootview.findViewById<RecyclerView>(R.id.groupRView)
        var groupListC : MutableList<Group> = mutableListOf()
        var user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    var grplist = document.get("groups") as? ArrayList<String>
                    for( grp in grplist!!) {

                        db.collection("groups").document(grp)
                            .get()
                            .addOnSuccessListener { document ->
                                    //Log.d(TAG, "${document.id} => ${document.data}")
                                    Log.warning(document.data.toString())
                                var created_user = ""
                                db.collection("users").document(document.data!!.get("created_user").toString())
                                    .get().addOnSuccessListener { doc ->
                                        created_user = doc.getString("email").toString()
                                        var group = Group(
                                            document.id.toString(),
                                            document.data!!.getValue("title").toString(),
                                            document.data!!.get("description").toString(),
                                            created_user,
                                            Uri.parse(document.getString("imageuri"))
                                        )
                                        groupListC.add(group)

                                        Log.warning(groupListC.size.toString())

                                        myAdapter = GroupAdapter(groupListC)
                                        myAdapter.setMyItemClickListener(this)
                                        rview.layoutManager = LinearLayoutManager(this.context)
                                        rview.adapter = myAdapter
                                    }
                                    .addOnFailureListener { e -> }
//                                    var group = Group(
//                                        document.id.toString(),
//                                        document.data!!.getValue("title").toString(),
//                                        document.data!!.get("description").toString(),
//                                        created_user,
//                                        Uri.parse(document.getString("imageuri"))
//                                    )
//                                    groupListC.add(group)
//
//                                Log.warning(groupListC.size.toString())
//
//                                myAdapter = GroupAdapter(groupListC)
//                                myAdapter.setMyItemClickListener(this)
//                                rview.layoutManager = LinearLayoutManager(this.context)
//                                rview.adapter = myAdapter
                            }
                            .addOnFailureListener { exception ->
                                Log.warning("Error getting documents." + exception.toString())
                            }
                    }
                }


        }
        val fab: FloatingActionButton = rootview.findViewById(R.id.addGroupFab)
        fab.setOnClickListener {
            // Handle the FAB click action
            myParent?.addGroupFabclicked()
        }

        return rootview
    }
    override fun onItemClickedFromAdapter(group : Group) {
        val args = Bundle()
        args.putString("title", group.title)
        args.putString("description", group.description)
        args.putString("created_user",group.created_user)
        args.putString("id",group.id)
        args.putString("uri", group.uri.toString())
        val fragmentGroup = GroupFragment()
        fragmentGroup.arguments = args
        myParent?.load(fragmentGroup)


    }

    override fun exitClickedFromAdapter(group: Group) {
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GroupListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}