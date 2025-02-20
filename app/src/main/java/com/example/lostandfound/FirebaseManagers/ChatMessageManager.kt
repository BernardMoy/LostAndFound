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
    // given a chat message id, return a chat message object, or null if fails
    fun getChatMessageFromMessageId(messageID: String, callback: ChatMessageCallback) {
        val firestoreManager = FirestoreManager()
        firestoreManager.get(
            FirebaseNames.COLLECTION_CHATS,
            messageID,
            object : Callback<Map<String, Any>> {
                override fun onComplete(result: Map<String, Any>?) {
                    if (result.isNullOrEmpty()) {
                        callback.onComplete(null)
                        return
                    }

                    val chatMessage = ChatMessage(
                        messageID = messageID,
                        senderUserID = result[FirebaseNames.CHAT_SENDER_USER_ID] as String,
                        recipientUserID = result[FirebaseNames.CHAT_RECIPIENT_USER_ID] as String,
                        text = result[FirebaseNames.CHAT_CONTENT] as String,
                        isReadByRecipient = result[FirebaseNames.CHAT_IS_READ_BY_RECIPIENT] as Boolean,
                        timestamp = result[FirebaseNames.CHAT_TIMESTAMP] as Long
                    )

                    callback.onComplete(chatMessage)
                }

            }
        )
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
