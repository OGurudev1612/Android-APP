package com.example.letsgo

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class User(
    val id: String,
    var UserName: String?,
    var description: String?,
    var UserMail: String,
    var PhoneNumber: String?,
    var isChecked : Boolean,
    var groups : ArrayList<String>,
    var uri : String

) {

}
var p = arrayListOf<String>("p")
val user1 = User("1","user1","Descrition of user 1", "user1@gmail.com","12345679894",false,p,"")
val user2 = User("2","user2","Descrition of user 2", "user1@gmail.com","12345679894",false,p,"")
val user3 = User("3","user3","Descrition of user 3","user1@gmail.com","12345679894",false,p,"")
val user4 = User("4","user4","Descrition of user 4","user1@gmail.com","12345679894",false,p,"")
val user5 = User("5","user5","Descrition of user 5","user1@gmail.com","12345679894",false,p,"")
val user6 = User("6","user6","Descrition of user 6","user1@gmail.com","12345679894",false,p,"")
val user7 = User("7","user7","Descrition of user 7","user1@gmail.com","12345679894",false,p,"")
val user8 = User("8","user8","Descrition of user 8","user1@gmail.com","12345679894",false,p,"")
val user9 = User("9","user9","Descrition of user 9","user1@gmail.com","12345679894",false,p,"")
val user10 = User("10","user10","Descrition of user 10","user1@gmail.com","12345679894",false,p,"")
val userList = mutableListOf<User>(user1, user2,user3, user4,user5, user6,user7, user8,user9, user10)
