package com.example.lostandfound.Data

data class ChatMessage (
    val messageID: String,
    val senderUserID: String,
    val recipientUserID: String,
    val text: String,
    val timestamp: Long // epoch time
)