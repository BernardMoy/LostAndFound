package com.example.lostandfound.FirebaseManagers

import android.util.Log
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.google.firebase.firestore.FirebaseFirestore

// Firebase methods to change the chat inbox object when a new chat is sent in that inbox
object ChatInboxManager {

    interface ChatInboxUpdateCallback {
        fun onComplete(result: Boolean)
    }


    // given a chat message, CREATE OR UPDATE the chat inbox data
    fun updateChatInbox(
        senderUser: User,
        recipientUser: User,
        lastMessageID: String,  // the last message is the id of this message, because users cannot send message in the past of the current latest message
        lastMessageContent: String,
        lastMessageIsRead: Boolean,
        lastMessageTimestamp: Long,
        lastMessageSenderUserID: String,
        callback: ChatInboxUpdateCallback
    ) {

        // participant 1 and 2 are not random, the first participant must have a smaller order when sorting
        // to allow easy comparison
        val (participant1, participant2) = listOf(senderUser, recipientUser).sortedBy { it ->
            it.userID  // sort by their user ids
        }

        // check if the chat inbox with the same participants already exist
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseNames.COLLECTION_CHAT_INBOXES)
            .whereEqualTo(FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_ID, participant1.userID)
            .whereEqualTo(FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_ID, participant2.userID)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // if the chat box does not exist, then create it using put
                    // create data
                    val data = mapOf(
                        FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_ID to participant1.userID,
                        FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_AVATAR to participant1.avatar,
                        FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_FIRST_NAME to participant1.firstName,
                        FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_LAST_NAME to participant1.lastName,
                        FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_ID to participant2.userID,
                        FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_AVATAR to participant2.avatar,
                        FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_FIRST_NAME to participant2.firstName,
                        FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_LAST_NAME to participant2.lastName,
                        FirebaseNames.CHAT_INBOX_LAST_MESSAGE_ID to lastMessageID,
                        FirebaseNames.CHAT_INBOX_LAST_MESSAGE_CONTENT to lastMessageContent,
                        FirebaseNames.CHAT_INBOX_LAST_MESSAGE_IS_READ to lastMessageIsRead,
                        FirebaseNames.CHAT_INBOX_LAST_MESSAGE_TIMESTAMP to lastMessageTimestamp,
                        FirebaseNames.CHAT_INBOX_LAST_MESSAGE_SENDER_USER_ID to lastMessageSenderUserID
                    )

                    db.collection(FirebaseNames.COLLECTION_CHAT_INBOXES)
                        .add(data)
                        .addOnSuccessListener {
                            callback.onComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Log.d("Chat inbox update error", e.message ?: "")
                            callback.onComplete(false)
                        }

                } else {
                    // else, update the last message id, content, isread, timestamp of the chatbox
                    // there should only be one document
                    for (document in result) {
                        document.reference.update(
                            mapOf(
                                FirebaseNames.CHAT_INBOX_LAST_MESSAGE_ID to lastMessageID,
                                FirebaseNames.CHAT_INBOX_LAST_MESSAGE_CONTENT to lastMessageContent,
                                FirebaseNames.CHAT_INBOX_LAST_MESSAGE_IS_READ to lastMessageIsRead,
                                FirebaseNames.CHAT_INBOX_LAST_MESSAGE_TIMESTAMP to lastMessageTimestamp,
                                FirebaseNames.CHAT_INBOX_LAST_MESSAGE_SENDER_USER_ID to lastMessageSenderUserID
                            )
                        )
                            .addOnSuccessListener {
                                callback.onComplete(true)
                            }
                            .addOnFailureListener { e ->
                                Log.d("Chat inbox update error", e.message ?: "")
                                callback.onComplete(false)
                            }
                    }

                }
            }
            .addOnFailureListener { e ->
                Log.d("Chat inbox update error", e.message ?: "")
                callback.onComplete(false)
            }
    }
}