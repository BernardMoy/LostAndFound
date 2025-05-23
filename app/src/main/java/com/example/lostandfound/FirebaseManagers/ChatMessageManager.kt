package com.example.lostandfound.FirebaseManagers

import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback



/*
Firebase methods to change the state of a chat message
 */
object ChatMessageManager {

    interface UpdateMessageCallback {
        fun onComplete(result: Boolean)
    }


    // given a message id, mark it as read
    fun markChatAsRead(messageID: String, callback: UpdateMessageCallback) {
        val firestoreManager = FirestoreManager()
        firestoreManager.update(
            FirebaseNames.COLLECTION_CHATS,
            messageID,
            FirebaseNames.CHAT_IS_READ_BY_RECIPIENT,
            true,
            object : Callback<Boolean> {
                override fun onComplete(result: Boolean) {
                    callback.onComplete(result)
                }
            }
        )
    }
}
