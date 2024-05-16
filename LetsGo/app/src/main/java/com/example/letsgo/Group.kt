package com.example.letsgo

import android.net.Uri
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class Group(
    val id: String,
    var title: String,
    var description: String,
    var created_user: String,
    var uri : Uri
) {

}
val sdf1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
val dateFromString1 = sdf1.parse("2021-12-01") ?: Date()
//val group1 = Group(1,"Group1","Descrition of Group 1",Date(),"Personal")
//val group2 = Group(2,"Group2","Descrition of Group 2", dateFromString,"Personal")
//val group3 = Group(3,"Group3","Descrition of Group 3",dateFromString,"Personal")
//val group4 = Group(4,"Group4","Descrition of Group 4",dateFromString,"Personal")
//val group5 = Group(5,"Group5","Descrition of Group 5",dateFromString,"Personal")
//val group6 = Group(6,"Group6","Descrition of Group 6",dateFromString,"Personal")
//val group7 = Group(7,"Group7","Descrition of Group 7",dateFromString,"Personal")
//val group8 = Group(8,"Group8","Descrition of Group 8",dateFromString,"Personal")
//val group9 = Group(9,"Group9","Descrition of Group 9",dateFromString,"Personal")
//val group10 = Group(10,"Group10","Descrition of Group 10",dateFromString,"Personal")
//val groupList1 = mutableListOf<Group>(group1, group2, group3, group4, group5, group6, group7, group8, group9, group10)


