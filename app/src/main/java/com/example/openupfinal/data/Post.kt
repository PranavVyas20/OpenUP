package com.example.openupfinal.data

import com.google.firebase.firestore.PropertyName

 class Post(
     val id:String = "",
     val creationTime:Long = 0,
     val postDescription:String = "",
     val postedByUser:String = "",
     val postedByUserUID: String = ""
)
