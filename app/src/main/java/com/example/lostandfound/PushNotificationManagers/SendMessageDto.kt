package com.example.lostandfound.PushNotificationManagers

/*
Contains a dto object used for sending push notifications

These formats must be exactly followed
the names of variables also cannot be changed
otherwise it 400 bad request
 */

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
