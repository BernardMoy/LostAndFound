package com.example.lostandfound.ui.ChatInbox

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.Utility.DateTimeManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

interface SendMessageCallback {
    fun onComplete(result: Boolean)
}

interface FetchMessageCallback {
    fun onComplete(result: Boolean)
}

class ChatInboxViewModel : ViewModel() {
    // only store the user that the current user is chatting with, because the second user must be the current user
    // loaded on create
    var chatUser: User = User()

    // store the current typed message content
    val typedText: MutableState<String> = mutableStateOf("")

    // store a list of chat previews
    var chatMessageList: MutableList<ChatMessage> = mutableStateListOf()  // make this listenable

    // whether the past chats are loading
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    // trigger to enable scrolling by button
    val triggerScrollToBottom: MutableState<Boolean> = mutableStateOf(false)

    // trigger to scroll to bottom immediately, initially
    val triggerScrollToBottomInstantly: MutableState<Boolean> = mutableStateOf(false)

    // whether the new messages button is shown
    val isNewMessageButtonShown: MutableState<Boolean> = mutableStateOf(false)

    // whether is initial load. If yes, it will be forced to scroll to bottom and button wont show
    val isInitialLoad: MutableState<Boolean> = mutableStateOf(true)


    // validate if the message to send is valid
    fun validateMessage(): Boolean {
        return typedText.value.trim().isNotEmpty()
    }

    // method to send message
    fun sendMessage(callback: SendMessageCallback) {
        val db = FirestoreManager()

        // create chat data
        val data = mapOf(
            FirebaseNames.CHAT_SENDER_USER_ID to FirebaseUtility.getUserID(),
            FirebaseNames.CHAT_RECIPIENT_USER_ID to chatUser.userID,
            FirebaseNames.CHAT_FROM_TO to listOf(
                FirebaseUtility.getUserID(),
                chatUser.userID
            ),  // [from user, to user]
            FirebaseNames.CHAT_CONTENT to typedText.value,
            FirebaseNames.CHAT_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
        )

        // add the message content from typedText
        db.putWithUniqueId(
            FirebaseNames.COLLECTION_CHATS,
            data,
            object : Callback<String> {
                override fun onComplete(result: String) {
                    if (result.isEmpty()) {
                        callback.onComplete(false)
                        return
                    }

                    // success, then reset the typed text
                    typedText.value = ""
                    callback.onComplete(true)
                }
            }
        )
    }

    // method to fetch all messages and load it into chat preview
    fun fetchMessagePreviews(callback: FetchMessageCallback) {
        val db = FirebaseFirestore.getInstance()

        // clear chat message preview
        chatMessageList.clear()

        // fetch all messages where the user is either the sender or recipient
        db.collection(FirebaseNames.COLLECTION_CHATS)
            .whereArrayContains(FirebaseNames.CHAT_FROM_TO, FirebaseUtility.getUserID())
            .orderBy(FirebaseNames.CHAT_TIMESTAMP)
            .addSnapshotListener { snapshot, error ->       // listen for real time updates
                if (error != null) {
                    callback.onComplete(false)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (documentChange in snapshot.documentChanges) {
                        // listen for added entries only
                        if (documentChange.type == DocumentChange.Type.ADDED) {
                            // create new chat message object
                            val newChatMessage = ChatMessage(
                                messageID = documentChange.document.id,
                                senderUserID = documentChange.document[FirebaseNames.CHAT_SENDER_USER_ID].toString(),
                                recipientUserID = documentChange.document[FirebaseNames.CHAT_RECIPIENT_USER_ID].toString(),
                                text = documentChange.document[FirebaseNames.CHAT_CONTENT].toString(),
                                timestamp = documentChange.document[FirebaseNames.CHAT_TIMESTAMP] as Long
                            )

                            // add the chat message to list
                            chatMessageList.add(newChatMessage)
                        }
                    }
                }

                // return true
                callback.onComplete(true)
            }
    }
}