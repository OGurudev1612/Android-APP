package com.example.letsgo

import android.net.Uri
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class Task(
    var id : String,
    var title: String,
    var description: String,
    var date: Date,
    var time: Date,
    var group: String,
    var imageuri : Uri
) {

}

val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
val dateFromString = sdf.parse("2021-12-01") ?: Date()
//val task1 = Task(1,"Task1","Descrition of Task 1",Date(),"Personal", "d")
//val task2 = Task(2,"Task2","Descrition of Task 2", dateFromString,"Personal", "d")
//val task3 = Task(3,"Task3","Descrition of Task 3",dateFromString,"Personal", "d")
//val task4 = Task(4,"Task4","Descrition of Task 4",dateFromString,"Personal", "d")
//val task5 = Task(5,"Task5","Descrition of Task 5",dateFromString,"Personal", "d")
//val task6 = Task(6,"Task6","Descrition of Task 6",dateFromString,"Personal", "d")
//val task7 = Task(7,"Task7","Descrition of Task 7",dateFromString,"Personal", "d")
//val task8 = Task(8,"Task8","Descrition of Task 8",dateFromString,"Personal", "d")
//val task9 = Task(9,"Task9","Descrition of Task 9",dateFromString,"Personal", "d")
//val task10 = Task(10,"Task20","Descrition of Task 10",dateFromString,"Personal", "d")
//val taskList = mutableListOf<Task>(task1, task2,task3, task4,task5, task6,task7, task8,task9, task10)
