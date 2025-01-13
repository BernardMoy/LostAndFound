package com.example.lostandfound.Data

data class ChatInboxPreview(
    val recipientUser: User = User(),  // the sender is always the current user
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L
)