package com.example.lostandfound.FirebaseManagers

import android.util.Log
import com.example.lostandfound.Data.FirebaseNames
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object FCMTokenManager {

    interface FCMTokenUpdateCallback{
        fun onComplete(success: Boolean)
    }
    interface FCMTokenGetCallback{
        fun onComplete(token: String?)   // return token if successful or null
    }
    interface FCMTokenDeleteCallback{
        fun onComplete(success: Boolean)
    }

    // method to save the FCM token of the user when given a user id
    fun updateFCMToken(userID: String, callback: FCMTokenUpdateCallback){
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

    // method to get fcm token given a user id
    fun getFCMTokenFromUser(userID: String, callback: FCMTokenGetCallback){
        FirebaseFirestore.getInstance().collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val token = document.getString(FirebaseNames.USERS_FCM_TOKEN)
                if (token == null){
                    callback.onComplete(null)
                    return@addOnSuccessListener
                }
                callback.onComplete(token)
            }
            .addOnFailureListener { e ->
                Log.e("FCM token get error", "Failed to get FCM token", e)
                callback.onComplete(null)
            }
    }

    // method to clear FCM token for a user
    fun removeFCMTokenFromUser(userID: String, callback: FCMTokenDeleteCallback){
        FirebaseFirestore.getInstance().collection("users")
            .document(userID)
            .update(FirebaseNames.USERS_FCM_TOKEN, FieldValue.delete())     // delete the attribute
            .addOnSuccessListener {
                callback.onComplete(true)
            }
            .addOnFailureListener{ e ->
                Log.e("Firebase user delete error", e.message.toString())
                callback.onComplete(false)
            }
    }
}