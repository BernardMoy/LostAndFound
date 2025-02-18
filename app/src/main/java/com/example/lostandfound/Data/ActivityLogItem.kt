package com.example.lostandfound.Data

data class ActivityLogItem (
    val type: Int = 0,  // 0 to 7
    val content: String = "",
    val userID: String = "",
    val timestamp: Long = 0L,
    )