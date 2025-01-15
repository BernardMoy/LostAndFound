package com.example.lostandfound.Data

data class ChatInboxPreview(
    val updatedTime: Long = 0L,
    val recipientUser: User = User(),  // the sender is always the current user - to be passed as intent
    val lastMessage: ChatMessage = ChatMessage()  // fetch the last message
)