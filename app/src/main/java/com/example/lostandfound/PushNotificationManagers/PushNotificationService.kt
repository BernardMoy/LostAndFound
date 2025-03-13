package com.example.lostandfound.PushNotificationManagers

import com.google.firebase.messaging.FirebaseMessagingService

/*
This class is put in the manifest and cannot be omitted
 */
class PushNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // additional functions here
    }
}