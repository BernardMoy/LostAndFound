package com.example.lostandfound.Data

data class ChatInboxPreview(
    val recipientUser: User = User(),  // the sender is always the current user - to be passed as intent

    // display data of the last message
    val lastMessageContent: String = "",
    val lastMessageIsRead: Boolean = false,
    val lastMessageTimestamp: Long = 0L // epoch time
)