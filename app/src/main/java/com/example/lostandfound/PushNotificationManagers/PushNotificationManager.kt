package com.example.lostandfound.PushNotificationManagers

import android.content.Context
import android.util.Log
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FCMTokenManager
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.firestore.FirebaseFirestore
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

/*
Contains the methods to send push notifications of different types
called in notification manager
 */
object PushNotificationManager {
    val fcmApi: FCMApi = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(FCMApi::class.java)


    private fun getAccessToken(context: Context): String {
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
    private fun sendPushNotificationToUserID(
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

    // method to send a push notification of a new message
    fun sendPushNotificationNewMessage(
        context: Context,
        userID: String,
        userName: String,
        message: String,   // the new message
        callback: PushNotificationCallback
    ) {
        // first use the user id to see if the user has message notif enabled
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { doc ->
                val enabled =
                    (doc[FirebaseNames.USERS_MESSAGE_NOTIFICATION_ENABLED] as? Boolean) ?: true
                if (!enabled) {
                    callback.onComplete(true)
                    return@addOnSuccessListener
                }

                // if enabled, send push notif
                sendPushNotificationToUserID(
                    context = context,
                    userID = userID,
                    title = "New message",
                    content = "$userName: $message",
                    object : PushNotificationCallback {
                        override fun onComplete(success: Boolean) {
                            callback.onComplete(success)
                        }
                    }
                )
            }
            .addOnFailureListener { e ->
                Log.e("Firebase firestore error", e.message.toString())
                callback.onComplete(false)
            }
    }

    // method to send a push notification about a notif of type 0
    fun sendPushNotificationNewMatchingItem(
        context: Context,
        userID: String,
        lostItemName: String,
        callback: PushNotificationCallback
    ) {
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { doc ->
                val enabled =
                    (doc[FirebaseNames.USERS_ITEM_NOTIFICATION_ENABLED] as? Boolean) ?: true
                if (!enabled) {
                    callback.onComplete(true)
                    return@addOnSuccessListener
                }

                // if enabled, send push notif
                sendPushNotificationToUserID(
                    context = context,
                    userID = userID,
                    title = "New matching item!",
                    content = "A new matching item has been found for your item $lostItemName.",
                    object : PushNotificationCallback {
                        override fun onComplete(success: Boolean) {
                            callback.onComplete(success)
                        }
                    }
                )
            }
            .addOnFailureListener { e ->
                Log.e("Firebase firestore error", e.message.toString())
                callback.onComplete(false)
            }
    }

    // method to send push notif type 1
    fun sendPushNotificationClaimApproved(
        context: Context,
        userID: String,
        callback: PushNotificationCallback
    ) {
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { doc ->
                val enabled =
                    (doc[FirebaseNames.USERS_ITEM_NOTIFICATION_ENABLED] as? Boolean) ?: true
                if (!enabled) {
                    callback.onComplete(true)
                    return@addOnSuccessListener
                }

                // if enabled, send push notif
                sendPushNotificationToUserID(
                    context = context,
                    userID = userID,
                    title = "Claim approved!",
                    content = "Your claim has been approved!",
                    object : PushNotificationCallback {
                        override fun onComplete(success: Boolean) {
                            callback.onComplete(success)
                        }
                    }
                )
            }
            .addOnFailureListener { e ->
                Log.e("Firebase firestore error", e.message.toString())
                callback.onComplete(false)
            }
    }


    // method to send push notif type 2
    fun sendPushNotificationClaimRejected(
        context: Context,
        userID: String,
        callback: PushNotificationCallback
    ) {
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { doc ->
                val enabled =
                    (doc[FirebaseNames.USERS_ITEM_NOTIFICATION_ENABLED] as? Boolean) ?: true
                if (!enabled) {
                    callback.onComplete(true)
                    return@addOnSuccessListener
                }

                // if enabled, send push notif
                sendPushNotificationToUserID(
                    context = context,
                    userID = userID,
                    title = "Claim rejected",
                    content = "Contact the user for more details.",
                    object : PushNotificationCallback {
                        override fun onComplete(success: Boolean) {
                            callback.onComplete(success)
                        }
                    }
                )
            }
            .addOnFailureListener { e ->
                Log.e("Firebase firestore error", e.message.toString())
                callback.onComplete(false)
            }
    }


    // method to send push notif type 3
    fun sendPushNotificationUserClaimedYourItem(
        context: Context,
        userID: String,
        foundItemName: String,
        callback: PushNotificationCallback
    ) {
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { doc ->
                val enabled =
                    (doc[FirebaseNames.USERS_ITEM_NOTIFICATION_ENABLED] as? Boolean) ?: true
                if (!enabled) {
                    callback.onComplete(true)
                    return@addOnSuccessListener
                }

                // if enabled, send push notif
                sendPushNotificationToUserID(
                    context = context,
                    userID = userID,
                    title = "New claim to your found item",
                    content = "A user has claimed your item $foundItemName.",
                    object : PushNotificationCallback {
                        override fun onComplete(success: Boolean) {
                            callback.onComplete(success)
                        }
                    }
                )
            }
            .addOnFailureListener { e ->
                Log.e("Firebase firestore error", e.message.toString())
                callback.onComplete(false)
            }
    }
}