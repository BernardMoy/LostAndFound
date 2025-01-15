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
import com.example.lostandfound.FirebaseManagers.UserManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

interface ChatInboxPreviewCallback {
    fun onComplete(result: Boolean)
}

class ChatFragmentViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // a list of chat inbox previews to be displayed
    val chatInboxPreviewList: MutableList<ChatInboxPreview> = mutableStateListOf()

    // keep track of the listener registration
    private var listenerRegistration: ListenerRegistration? = null


    fun loadData(callback: ChatInboxPreviewCallback){
        val db = FirebaseFirestore.getInstance()

        // clear the list
        chatInboxPreviewList.clear()

        // remove previous listener
        listenerRegistration?.remove()

        // assign new listener
        listenerRegistration = db.collection(FirebaseNames.COLLECTION_CHATS)
            .whereArrayContains(FirebaseNames.CHAT_FROM_TO, FirebaseUtility.getUserID())
            .orderBy(FirebaseNames.CHAT_TIMESTAMP, Query.Direction.DESCENDING)
            .limit(1)  // get the latest ChatMessage object
            .addSnapshotListener { snapshot, error ->       // listen for real time updates
                if (error != null) {
                    Log.d("Snapshot error", error.message.toString())
                    callback.onComplete(false)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val totalSize = snapshot.documentChanges.size
                    var fetchedItems = 0

                    if (snapshot.documentChanges.isEmpty()){
                        callback.onComplete(true)
                        return@addSnapshotListener
                    }

                    for (documentChange in snapshot.documentChanges) {
                        // listen for new messages
                        if (documentChange.type == DocumentChange.Type.ADDED) {

                            // clear the list
                            chatInboxPreviewList.clear()

                            // Log.d("NEW MSG", documentChange.document[FirebaseNames.CHAT_CONTENT].toString())


                            // the recipient user id is either the sender or recipient OF THE MESSAGE,
                            // and equal to the one that isnt the current user.
                            val messageSenderUserID = documentChange.document[FirebaseNames.CHAT_SENDER_USER_ID].toString()
                            val messageRecipientUserID = documentChange.document[FirebaseNames.CHAT_RECIPIENT_USER_ID].toString()
                            val newRecipientUserID = if (messageSenderUserID != FirebaseUtility.getUserID()) messageSenderUserID
                                                    else messageRecipientUserID

                            // get the user object from id
                            /*
                            UserManager.getUserFromId(newRecipientUserID, object: UserManager.UserCallback{
                                override fun onComplete(user: User?) {

                                    if (user == null){
                                        callback.onComplete(false)
                                        return
                                    }

                                    // get the last message and last message timestamp
                                    val newLastMessage = if (messageSenderUserID == FirebaseUtility.getUserID()) "You: " + documentChange.document[FirebaseNames.CHAT_CONTENT].toString()
                                                        else documentChange.document[FirebaseNames.CHAT_CONTENT].toString()
                                    val newLastMessageTimestamp = documentChange.document[FirebaseNames.CHAT_TIMESTAMP] as Long



                                    // create new chat message preview object
                                    val newChatInboxPreview = ChatInboxPreview(
                                        recipientUser = user,
                                        lastMessage = newLastMessage,
                                        lastMessageTimestamp = newLastMessageTimestamp
                                    )

                                    // add to list
                                    chatInboxPreviewList.add(newChatInboxPreview)

                                    // if all fetched, sort and return
                                    fetchedItems ++
                                    if (fetchedItems == totalSize){
                                        chatInboxPreviewList.sortByDescending { key ->
                                            key.lastMessageTimestamp
                                        }
                                        callback.onComplete(true)
                                    }
                                }
                            })

                             */
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