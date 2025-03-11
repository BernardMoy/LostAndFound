package com.example.lostandfound.FirebaseManagers

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.lostandfound.Data.FirebaseNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

object FirebaseMessagingManager {

    interface FCMTokenCallback{
        fun onComplete(success: Boolean)
    }
    interface FCMTokenGetCallback{
        fun onComplete(token: String?)   // return token if successful or null
    }

    // method to save the FCM token of the user when given a user id
    fun updateFCMToken(userID: String, callback: FCMTokenCallback){
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

    // method to send notifications
    fun sendPushNotification(userID: String, title: String, content: String){
        // first get the fcm token of the user id
        getFCMTokenFromUser(userID, object: FCMTokenGetCallback{
            override fun onComplete(token: String?) {
                if (token == null){
                    return
                }
                val notification = JSONObject().apply {
                    put("title", title)
                    put("body", content)
                }

                val message = JSONObject().apply {
                    put("token", token)
                    put("notification", notification)
                }

                val payload = JSONObject().apply {
                    put("message", message)
                }

                // make a JSON Post request
                val request = object : JsonObjectRequest(
                    Method.POST, "https://fcm.googleapis.com/v1/projects/lostandfound-d4afc/messages:send",
                    payload,
                    { response -> Log.d("FCM", "Notification sent: $response") },
                    { error -> Log.e("FCM", "Error sending notification", error) }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        return mutableMapOf(
                            "Authorization" to "Bearer YOUR_OAUTH_ACCESS_TOKEN",
                            "Content-Type" to "application/json"
                        )
                    }
                }

                val requestQueue = Volley.newRequestQueue(FirebaseAuth.getInstance().app.applicationContext)
                requestQueue.add(request)

            }
        })
    }
}