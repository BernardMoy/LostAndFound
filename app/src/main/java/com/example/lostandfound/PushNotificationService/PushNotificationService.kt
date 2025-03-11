package com.example.lostandfound.PushNotificationService

import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService : FirebaseMessagingService(){

    override fun onNewToken(token: String){
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // send notifications
        showNotification("Testtitle", "testbody")
    }

    // updates the token in firebase firestore if it updates
    private fun sendTokenToFirebase(token: String) {
        val userID = FirebaseUtility.getUserID()
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
            .document(userID)
            .update(FirebaseNames.USERS_FCM_TOKEN, token)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Log.e("Update FCM token error", "Failed to update token")
            }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(this, "FCM_CHANNEL")
            .setContentTitle(title)
            .setContentText(message)  // notifs only have title and message
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}