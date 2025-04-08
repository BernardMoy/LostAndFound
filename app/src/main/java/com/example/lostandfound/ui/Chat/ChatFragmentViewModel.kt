package com.example.lostandfound.ui.Chat

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.ChatInboxPreview
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Filter
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
        val currentUserID = FirebaseUtility.getUserID()

        // clear the list
        chatInboxPreviewList.clear()

        // remove previous listener
        listenerRegistration?.remove()

        // assign new listener to retrieve all chat inbox entries
        listenerRegistration = db.collection(FirebaseNames.COLLECTION_CHAT_INBOXES)
            .where(
                Filter.or(
                    Filter.equalTo(FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_ID, currentUserID),
                    Filter.equalTo(FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_ID, currentUserID)
                )
            )
            .orderBy(FirebaseNames.CHAT_INBOX_LAST_MESSAGE_TIMESTAMP, Query.Direction.DESCENDING)
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

                    snapshot.documentChanges.forEachIndexed { index, documentChange ->
                        // listen for new messages
                        if (documentChange.type == DocumentChange.Type.ADDED
                            || documentChange.type == DocumentChange.Type.MODIFIED
                        ) {

                            // get the user information of the user that is communicating with
                            // the recipient of the chat inbox is the participant in the participants array
                            // that does not equal to the current user, given that a user cannot chat with themselves.
                            val userID1 =
                                documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_ID].toString()
                            val userID2 =
                                documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_ID].toString()
                            val inboxRecipientUser = if (userID1 != currentUserID) User(
                                userID = userID1,
                                avatar = documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_AVATAR].toString(),
                                firstName = documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_FIRST_NAME].toString(),
                                lastName = documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT1_USER_LAST_NAME].toString(),
                            ) else User(
                                userID = userID2,
                                avatar = documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_AVATAR].toString(),
                                firstName = documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_FIRST_NAME].toString(),
                                lastName = documentChange.document[FirebaseNames.CHAT_INBOX_PARTICIPANT2_USER_LAST_NAME].toString(),
                            )


                            // get the chatMessage preview
                            val newChatInboxPreview = ChatInboxPreview(
                                recipientUser = inboxRecipientUser,
                                lastMessageContent = documentChange.document[FirebaseNames.CHAT_INBOX_LAST_MESSAGE_CONTENT].toString(),
                                lastMessageTimestamp = documentChange.document[FirebaseNames.CHAT_INBOX_LAST_MESSAGE_TIMESTAMP] as Long,
                                lastMessageIsRead = documentChange.document[FirebaseNames.CHAT_INBOX_LAST_MESSAGE_IS_READ] as Boolean
                            )

                            // add to list
                            chatInboxPreviewList[index] =
                                newChatInboxPreview

                            // if all fetched, sort and return
                            fetchedItems++
                            if (fetchedItems == totalSize) {
                                callback.onComplete(true)
                            }


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