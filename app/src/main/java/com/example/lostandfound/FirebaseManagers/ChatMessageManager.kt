package com.example.lostandfound.FirebaseManagers

import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback

interface ChatMessageCallback {
    fun onComplete(result: ChatMessage?)  // return chat message or null if failed
}

interface UpdateMessageCallback {
    fun onComplete(result: Boolean)
}


object ChatMessageManager {
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
