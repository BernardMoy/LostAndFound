package com.example.lostandfound.Data

// Stores an activity log item that is rendered in view activity log
data class ActivityLogItem(
    val id: String = "",
    val type: Int = 0,  // 0 to 7
    val content: String = "",
    val userID: String = "",
    val timestamp: Long = 0L,
)