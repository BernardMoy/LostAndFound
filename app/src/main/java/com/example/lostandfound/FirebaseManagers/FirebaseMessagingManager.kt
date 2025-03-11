package com.example.lostandfound.FirebaseManagers

import android.util.Log
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseMessagingManager {

    interface FCMTokenCallback{
        fun onComplete(success: Boolean)
    }

    // method to save the FCM token of the user when given a user id
    fun saveFCMTOken(userID: String, callback: FCMTokenCallback){
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
            if (!task.isSuccessful){
                Log.e("Firebase messaging error", task.exception!!.message.toString())
                callback.onComplete(false)
                return@addOnCompleteListener
            }

            // get the token
            val token = task.result

            // save the token in firestore users database
            FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
                .document(userID)
                .update(FirebaseNames.USERS_FCM_TOKEN, token)
                .addOnSuccessListener {
                    callback.onComplete(true)
                }
                .addOnFailureListener{ e ->
                    Log.e("Firebase user update error", e.message.toString())
                    callback.onComplete(false)
                }
        }
    }
}