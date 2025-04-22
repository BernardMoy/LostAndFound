package com.example.lostandfound.Data

// Stores a reported issue class that are rendered in viewing issues (Admin view only)
data class ReportedIssue(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val description: String
)