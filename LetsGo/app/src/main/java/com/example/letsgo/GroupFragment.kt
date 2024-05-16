package com.example.letsgo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.logging.Logger

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupFragment : Fragment(), RequestsAdapter.RequestAdapterItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var myAdapter: RequestsAdapter
    var myParent: GroupFragment.Parent? = null

    val Log = Logger.getLogger("he")
    val db = Firebase.firestore

    interface Parent {
//        fun load(fragmentTask: TaskFragment)
//        fun addTaskFabclicked()
        fun openTasks(grpId : String, title : String)
    }
    fun setParent(parent : Parent) {
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
        val rootview = inflater.inflate(R.layout.fragment_group, container, false)
        val title = arguments?.getString("title") ?: ""
        val description = arguments?.getString("description") ?: ""
        val created_user = arguments?.getString("created_user") ?: ""
        val id = arguments?.getString("id") ?: ""
        val uri = arguments?.getString("uri") ?: ""
        val titletext = rootview.findViewById<TextView>(R.id.groupNameTitle)
        val desc = rootview.findViewById<TextView>(R.id.grpdesctext)
        val cuser = rootview.findViewById<TextView>(R.id.createdusertext)
        val img = rootview.findViewById<ImageView>(R.id.groupImage)
        val grptasksb = rootview.findViewById<Button>(R.id.grptasksbutton)
        grptasksb.setOnClickListener{ myParent?.openTasks(id,title) }
        var usrListC : MutableList<User> = mutableListOf()
        db.collection("groups").document(id)
            .get()
            .addOnSuccessListener { document ->
                var usrlist = document.get("members") as? ArrayList<String>
                for( usr in usrlist!!) {
                    db.collection("users").document(usr)
                        .get()
                        .addOnSuccessListener { doc ->
                            var user4 = User(
                                doc.id,
                                doc.getString("username"),
                                doc.getString("description"),
                                doc.getString("email")!!,
                                doc.getString("phonenumber"),
                                false,
                                arrayListOf(),
                                doc.getString("uri")!!
                            )
                            usrListC.add(user4)
//                            Toast.makeText(
//                                requireContext(),
//                                "user added",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            val rview = rootview.findViewById<RecyclerView>(R.id.memberList)
                            myAdapter = RequestsAdapter(usrListC)
                            myAdapter.setMyItemClickListener(this)
                            rview.layoutManager = LinearLayoutManager(this.context)
                            rview.adapter = myAdapter
                        }
                        .addOnFailureListener { e ->

                        }
                }
//                val rview = rootview.findViewById<RecyclerView>(R.id.memberList)
//                myAdapter = RequestsAdapter(usrListC)
//                myAdapter.setMyItemClickListener(this)
//                rview.layoutManager = LinearLayoutManager(this.context)
//                rview.adapter = myAdapter
                titletext.text = title
                desc.text = description
                cuser.text = created_user
                if(uri != "") {
                    Glide.with(this)
                        .load(uri)
                        .into(img);
                }else{
                    img.setImageResource(R.drawable.man_sits_desk_with_pile_papers_pile_books_900370_26990)
                }
            }.addOnFailureListener { ex ->

            }


//        titletext.text = title
//        desc.text = description
//        cuser.text = created_user
//        Glide.with(this)
//            .load(uri)
//            .into(img);
        return rootview
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}