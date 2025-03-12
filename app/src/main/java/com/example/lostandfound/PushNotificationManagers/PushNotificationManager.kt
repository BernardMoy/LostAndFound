package com.example.lostandfound.PushNotificationManagers

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

interface PushNotificationCallback {
    fun onComplete(success: Boolean)
}

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

    // main method to send a notification
    fun sendPushNotification(
        context: Context,
        fcmToken: String,
        title: String,
        content: String,
        callback: PushNotificationCallback
    ) {
        // launch the suspend send function in a different thread (IO Thread)
        CoroutineScope(Dispatchers.IO).launch {
            val adminToken = getAccessToken(context)

            // create the message dto object for the contents of the push notifications
            val messageDto = SendMessageDto(
                message = MessageBody(
                    token = fcmToken,
                    notification = NotificationBody(
                        title = title,
                        body = content
                    )
                )
            )

            try {
                fcmApi.sendMessage("Bearer $adminToken", messageDto)

                // return the result in the main thread
                withContext(Dispatchers.Main) { // Switch back to Main thread for UI updates
                    callback.onComplete(true)
                }

            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback.onComplete(false)
                }

            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback.onComplete(false)
                }
            }
        }
    }
}