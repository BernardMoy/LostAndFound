package com.example.lostandfound.ui.Chat

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.ChatInboxPreview
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.ChatInboxManager
import com.example.lostandfound.FirebaseManagers.ChatMessageCallback
import com.example.lostandfound.FirebaseManagers.ChatMessageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.UserManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

interface ChatInboxPreviewCallback {
    fun onComplete(result: Boolean)
}

class ChatFragmentViewModel : ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // a list of chat inbox previews to be displayed
    val chatInboxPreviewList: MutableList<ChatInboxPreview> = mutableStateListOf()

    // keep track of the listener registration
    private var listenerRegistration: ListenerRegistration? = null


    fun loadData(callback: ChatInboxPreviewCallback) {
        val db = FirebaseFirestore.getInstance()

        // clear the list
        chatInboxPreviewList.clear()

        // remove previous listener
        listenerRegistration?.remove()

        // assign new listener
        listenerRegistration = db.collection(FirebaseNames.COLLECTION_CHAT_INBOXES)
            .whereArrayContains(FirebaseNames.CHAT_INBOX_PARTICIPANTS, FirebaseUtility.getUserID())
            .orderBy(FirebaseNames.CHAT_INBOX_UPDATED_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->       // listen for real time updates
                if (error != null) {
                    Log.d("Snapshot error", error.message.toString())
                    callback.onComplete(false)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val totalSize = snapshot.documentChanges.size
                    var fetchedItems = 0

                    if (snapshot.documentChanges.isEmpty()) {
                        callback.onComplete(true)
                        return@addSnapshotListener
                    }

                    // clear the list before reloading
                    chatInboxPreviewList.clear()

                    // initialise the itemData array with dummy variables of chat inbox previews
                    chatInboxPreviewList.addAll(List(totalSize) { ChatInboxPreview() })

                    snapshot.documentChanges.forEachIndexed{ index, documentChange ->
                        // listen for new messages
                        if (documentChange.type == DocumentChange.Type.ADDED
                            || documentChange.type == DocumentChange.Type.MODIFIED) {

                            // the recipient of the chat inbox is the participant in the participants array
                            // that does not equal to the current user, given that a user cannot chat with themselves.
                            val participants =
                                documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANTS] as List<*>
                            val inboxRecipientUserID =
                                (if (participants[0] != FirebaseUtility.getUserID()) participants[0] else participants[1]).toString()

                            // get the user object from id
                            UserManager.getUserFromId(
                                inboxRecipientUserID,
                                object : UserManager.UserCallback {
                                    override fun onComplete(user: User?) {
                                        if (user == null) {
                                            callback.onComplete(false)
                                            return
                                        }

                                        // get the chatMessage object from id
                                        val lastMessageID =
                                            documentChange.document[FirebaseNames.CHAT_INBOX_LAST_MESSAGE_ID].toString()
                                        ChatMessageManager.getChatMessageFromMessageId(
                                            lastMessageID,
                                            object : ChatMessageCallback {
                                                override fun onComplete(result: ChatMessage?) {
                                                    if (result == null) {
                                                        callback.onComplete(false)
                                                        return
                                                    }

                                                    // create new chat message preview object
                                                    val newChatInboxPreview = ChatInboxPreview(
                                                        recipientUser = user,
                                                        lastMessage = result
                                                    )

                                                    // add to list
                                                    chatInboxPreviewList[index] = newChatInboxPreview

                                                    // if all fetched, sort and return
                                                    fetchedItems++
                                                    if (fetchedItems == totalSize) {
                                                        callback.onComplete(true)
                                                    }
                                                }
                                            })
                                    }
                                })
                        }
                    }

                }
            }
    }

    // clear the previous listener when the view model is destroyed
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}