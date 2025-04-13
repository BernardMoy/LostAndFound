package com.example.lostandfound.Data

data class ReportedUser (
   val userFrom: User,
   val userTo: User,
   val description: String
)