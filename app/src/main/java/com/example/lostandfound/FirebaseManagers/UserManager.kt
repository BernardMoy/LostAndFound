package com.example.lostandfound.FirebaseManagers

import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User

object UserManager {

    interface UserCallback {
        fun onComplete(user: User?)  // return the user or null if fails
    }

    // method to get user object from id
    fun getUserFromId(userID: String, callback: UserCallback) {
        val firestoreManager = FirestoreManager()

        // get name of the user from firebase firestore
        firestoreManager.get(
            FirebaseNames.COLLECTION_USERS,
            userID,
            object : FirestoreManager.Callback<Map<String, Any>> {
                override fun onComplete(result: Map<String, Any>?) {
                    if (result == null) {
                        callback.onComplete(null)

                    } else {
                        val userData = User(
                            userID = userID,
                            avatar = result[FirebaseNames.USERS_AVATAR].toString(),
                            firstName = result[FirebaseNames.USERS_FIRSTNAME].toString(),
                            lastName = result[FirebaseNames.USERS_LASTNAME].toString()
                        )

                        callback.onComplete(userData)
                    }
                }
            })
    }
}