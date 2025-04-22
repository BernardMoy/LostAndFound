package com.example.lostandfound.Data

// stores a general chat message
data class ChatMessage(
    val messageID: String = "",
    val senderUser: User = User(),
    val recipientUser: User = User(),
    val text: String = "",
    val isReadByRecipient: Boolean = false,
    val timestamp: Long = 0L // epoch time
)