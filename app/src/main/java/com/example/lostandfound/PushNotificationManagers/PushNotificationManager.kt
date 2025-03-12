package com.example.lostandfound.PushNotificationManagers

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object PushNotificationManager {
    val fcmApi: FCMApi = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(FCMApi::class.java)


    fun getAccessToken(context: Context): String {
        val credentials = GoogleCredentials
            .fromStream(
                context.assets.open("service-account.json")
            )
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }
}