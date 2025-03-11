package com.example.lostandfound.FirebaseManagers

data class SendMessageDto(
    val message: MessageBody
)

data class MessageBody(
    val token: String,
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)
