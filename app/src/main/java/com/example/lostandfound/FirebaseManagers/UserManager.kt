package com.example.lostandfound.FirebaseManagers

import com.example.lostandfound.Data.FirebaseNames

object UserManager {
    interface UsernameCallback{
        fun onComplete(username: String) // return the username, or empty string if fails
    }

    // return the displayed name (first name + last name) when given a user id
    // return empty string if failed (Username cannot be empty)
    fun getUsernameFromId(userID: String, callback: UsernameCallback){
        val firestoreManager = FirestoreManager()

        // get name of the user from firebase firestore
        firestoreManager.get(FirebaseNames.COLLECTION_USERS, userID, object : FirestoreManager.Callback<Map<String, Any>>{
            override fun onComplete(result: Map<String, Any>?) {
                if (result == null) {
                    callback.onComplete("")

                } else {
                    val name = result[FirebaseNames.USERS_FIRSTNAME].toString() + " " + result[FirebaseNames.USERS_LASTNAME].toString()
                    callback.onComplete(name)
                }
            }
        })
    }
}