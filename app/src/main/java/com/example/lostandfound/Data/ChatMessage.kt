package com.example.lostandfound.Data

data class ChatMessage (
    val messageID: String = "",
    val senderUserID: String = "",
    val recipientUserID: String = "",
    val text: String = "",
    val isReadByRecipient: Boolean = false,
    val timestamp: Long = 0L // epoch time
)