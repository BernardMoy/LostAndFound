package com.example.lostandfound.Data

data class ReportedUser (
   val fromUid: String,
   val fromFirstName: String,
   val fromLastName: String,
   val fromEmail: String,
   val toUid: String,
   val toFirstName: String,
   val toLastName: String,
   val toEmail: String,
   val description: String
)