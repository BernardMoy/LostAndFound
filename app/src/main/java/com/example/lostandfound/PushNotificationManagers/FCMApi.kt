package com.example.lostandfound.PushNotificationManagers

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMApi {
    @Headers("Content-Type: application/json")
    @POST("v1/projects/lostandfound-d4afc/messages:send")
    suspend fun sendMessage(
        @Header("Authorization") authHeader: String,
        @Body body: SendMessageDto
    )
}