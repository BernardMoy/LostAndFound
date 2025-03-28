package com.example.lostandfound.ui.ChatInbox

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.ChatInboxManager
import com.example.lostandfound.FirebaseManagers.ChatInboxUpdateCallback
import com.example.lostandfound.FirebaseManagers.ChatMessageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.FirebaseManagers.UpdateMessageCallback
import com.example.lostandfound.Utility.DateTimeManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

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

    // keep track of the listener registration
    private var listenerRegistration: ListenerRegistration? = null


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
            FirebaseNames.CHAT_FROM_TO to listOf(  // [from user, to user]
                FirebaseUtility.getUserID(),
                chatUser.userID
            ),
            FirebaseNames.CHAT_CONTENT to typedText.value,
            FirebaseNames.CHAT_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),
            FirebaseNames.CHAT_IS_READ_BY_RECIPIENT to false, // default false
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

                    // update the chat inbox data
                    ChatInboxManager.updateChatInbox(
                        FirebaseUtility.getUserID(),
                        chatUser.userID,
                        result,
                        object : ChatInboxUpdateCallback {
                            override fun onComplete(result: Boolean) {
                                callback.onComplete(result)
                            }
                        }
                    )
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
        listenerRegistration = db.collection(FirebaseNames.COLLECTION_CHATS)
            .whereArrayContains(FirebaseNames.CHAT_FROM_TO, FirebaseUtility.getUserID())
            .orderBy(FirebaseNames.CHAT_TIMESTAMP)
            .addSnapshotListener { snapshot, error ->       // listen for real time updates
                if (error != null) {
                    Log.d("Snapshot error", error.message.toString())
                    callback.onComplete(false)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (documentChange in snapshot.documentChanges) {
                        // listen for added entries only
                        if (documentChange.type == DocumentChange.Type.ADDED) {
                            // create new chat message object
                            val messageID = documentChange.document.id
                            val isReadByRecipient =
                                documentChange.document[FirebaseNames.CHAT_IS_READ_BY_RECIPIENT] as Boolean
                            val messageSenderID =
                                documentChange.document[FirebaseNames.CHAT_SENDER_USER_ID].toString()

                            val newChatMessage = ChatMessage(
                                messageID = messageID,
                                senderUserID = messageSenderID,
                                recipientUserID = documentChange.document[FirebaseNames.CHAT_RECIPIENT_USER_ID].toString(),
                                text = documentChange.document[FirebaseNames.CHAT_CONTENT].toString(),
                                timestamp = documentChange.document[FirebaseNames.CHAT_TIMESTAMP] as Long,
                                isReadByRecipient = isReadByRecipient
                            )

                            // add the chat message to list
                            chatMessageList.add(newChatMessage)

                            // if the message NOT sent by the current user and is not read, mark it as read
                            if (messageSenderID != FirebaseUtility.getUserID() && !isReadByRecipient) {
                                ChatMessageManager.markChatAsRead(
                                    messageID,
                                    object : UpdateMessageCallback {
                                        override fun onComplete(result: Boolean) {
                                        }
                                    })
                            }
                        }
                    }
                }

                // return true
                callback.onComplete(true)
            }
    }

    // clear the previous listener when the view model is destroyed
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}