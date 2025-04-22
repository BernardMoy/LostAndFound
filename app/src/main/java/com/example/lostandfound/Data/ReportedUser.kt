package com.example.lostandfound.Data

// Stores a reported user class that are rendered in viewing users (Admin view only)
data class ReportedUser(
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