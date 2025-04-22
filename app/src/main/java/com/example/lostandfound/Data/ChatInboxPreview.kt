package com.example.lostandfound.Data

// stores a chat inbox preview that is rendered in chat fragment (Main activity)
data class ChatInboxPreview(
    val recipientUser: User = User(),  // the sender is always the current user - to be passed as intent

    // display data of the last message
    val lastMessageContent: String = "",
    val lastMessageIsRead: Boolean = false,
    val lastMessageSenderUserID: String = "",
    val lastMessageTimestamp: Long = 0L // epoch time
)