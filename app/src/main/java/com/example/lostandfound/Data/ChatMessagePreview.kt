package com.example.lostandfound.Data

data class ChatMessagePreview (
    val chatMessage: ChatMessage = ChatMessage(),
    val senderUserName: String = ""
)