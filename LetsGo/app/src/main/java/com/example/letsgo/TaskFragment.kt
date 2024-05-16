package com.example.letsgo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Logger

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val Log = Logger.getLogger("hi")
    val db = Firebase.firestore

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
        val rootview =  inflater.inflate(R.layout.fragment_task, container, false)
        val sdfDayDate = SimpleDateFormat("EEE MMM dd, YYYY", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val title = arguments?.getString("title")
        val desc = arguments?.getString("description")
        val date = arguments?.getString("date")
        val time = arguments?.getString("time")
        val group = arguments?.getString("group")
        val id = arguments?.getString("id")
        val uri = arguments?.getString("uri")
        val titletext = rootview.findViewById<TextView>(R.id.taskhead)
        val taskDate = rootview.findViewById<TextView>(R.id.taskdate)
        val taskTime = rootview.findViewById<TextView>(R.id.tasktime)
        val taskDesc = rootview.findViewById<TextView>(R.id.taskdesc)
        val imageView = rootview.findViewById<ImageView>(R.id.imageView8)
        val taskgrp = rootview.findViewById<TextView>(R.id.taskgrp)
        titletext.text = title
        taskDesc.text = desc
        taskDate.text = date
        taskTime.text = time
        taskgrp.text = group
        Glide.with(this)
            .load(uri)
            .into(imageView);


        return rootview
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaskFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}