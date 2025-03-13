package com.example.lostandfound.PushNotificationManagers

import android.content.Context
import com.example.lostandfound.FirebaseManagers.FCMTokenManager
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

    // method of sending a notification to a user given a user id,
    // or do nothing when the user does not have a valid fcm token
    // return true only when the notif is sent, false otherwise
    fun sendPushNotificationToUserID(
        context: Context,
        userID: String,
        title: String,
        content: String,
        callback: PushNotificationCallback
    ) {
        // get the fcm token of the user
        FCMTokenManager.getFCMTokenFromUser(userID, object : FCMTokenManager.FCMTokenGetCallback {
            override fun onComplete(token: String?) {
                // if no fcm token, return
                if (token == null) {
                    callback.onComplete(false)
                    return
                }

                // send notification to the token here
                sendPushNotification(
                    context = context,
                    fcmToken = token,
                    title = title,
                    content = content,
                    object : PushNotificationCallback {
                        override fun onComplete(success: Boolean) {
                            // return the result
                            callback.onComplete(success)
                        }
                    }
                )
            }

        })
    }
}