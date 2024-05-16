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
 * Use the [UserSelectListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserSelectListFragment : Fragment(), RequestsSelectAdapter.RequestsSelectAdapterItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var myParent: UserSelectListFragment.Parent? = null
    lateinit var myAdapter: RequestsSelectAdapter
    val Log = Logger.getLogger("select user")
    val db = Firebase.firestore

    interface Parent {
//        fun load(fragmentTask: TaskFragment)
        fun doneFabclicked(selectedUsers : ArrayList<String>)
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
        var userListC : MutableList<User> = mutableListOf()
        val rootview =  inflater.inflate(R.layout.fragment_user_select_list, container, false)
        val rview = rootview.findViewById<RecyclerView>(R.id.userRView)
        var user = FirebaseAuth.getInstance().currentUser
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //Log.d(TAG, "${document.id} => ${document.data}")
                    if (user != null) {
                        if (user.uid != document.id) {
                            Log.warning(document.data.toString())
                            var user2 = User(
                                document.id,
                                document.getString("Username"),
                                document.getString("description"),
                                document.getString("email")!!,
                                document.getString("phonenumber"),
                                false,
                                arrayListOf(),
                                document.getString("uri")!!
                            )
                            userListC.add(user2)
                        }
                    }
                }
                Log.warning(userListC.size.toString())
                myAdapter = RequestsSelectAdapter(userListC)
                myAdapter.setMyItemClickListener(this)
                rview.layoutManager = LinearLayoutManager(this.context)
                rview.adapter = myAdapter
            }
            .addOnFailureListener { exception ->
                Log.warning( "Error getting documents." + exception.toString())
            }



        val fab: FloatingActionButton = rootview.findViewById(R.id.doneFab)
        fab.setOnClickListener {
            // Handle the FAB click action
            myParent?.doneFabclicked( myAdapter.getSelectedUsers())
        }

        return rootview
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserSelectListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserSelectListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}