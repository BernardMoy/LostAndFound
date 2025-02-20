package com.example.lostandfound.FirebaseManagers

import android.util.Log
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Utility.DateTimeManager
import com.google.firebase.firestore.FirebaseFirestore

interface ChatInboxUpdateCallback {
    fun onComplete(result: Boolean)
}

object ChatInboxManager {

    // given a chat message, CREATE OR UPDATE the chat inbox data
    fun updateChatInbox(
        user1ID: String,
        user2ID: String,
        lastMessageID: String,  // the last message is the id of this message, because users cannot send message in the past of the current latest message
        callback: ChatInboxUpdateCallback
    ) {
        // get the participants of the message in sorted order
        val sortedParticipants =
            arrayOf(user1ID, user2ID).sorted()


        // check if the chat inbox with the same participants already exist
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseNames.COLLECTION_CHAT_INBOXES)
            .whereEqualTo(FirebaseNames.CHAT_INBOX_PARTICIPANTS, sortedParticipants)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // if the chat box does not exist, then create it using put
                    // create data
                    val data = mapOf(
                        FirebaseNames.CHAT_INBOX_PARTICIPANTS to sortedParticipants,
                        FirebaseNames.CHAT_INBOX_LAST_MESSAGE_ID to lastMessageID,
                        FirebaseNames.CHAT_INBOX_UPDATED_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
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
                    // else, update the last message of the chatbox
                    // there should only be one document
                    for (document in result) {
                        document.reference.update(
                            FirebaseNames.CHAT_INBOX_LAST_MESSAGE_ID,
                            lastMessageID  // only update the last msg id
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