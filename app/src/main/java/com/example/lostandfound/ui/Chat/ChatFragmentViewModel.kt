package com.example.lostandfound.ui.Chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.ChatInboxPreview
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.UserManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

interface ChatInboxPreviewCallback {
    fun onComplete(result: Boolean)
}

class ChatFragmentViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // a list of chat inbox previews to be displayed
    val chatInboxPreviewList: MutableList<ChatInboxPreview> = mutableListOf()


    fun loadData(callback: ChatInboxPreviewCallback){
        val db = FirebaseFirestore.getInstance()

        // clear the list
        chatInboxPreviewList.clear()

        db.collection(FirebaseNames.COLLECTION_CHATS)
            .whereArrayContains(FirebaseNames.CHAT_FROM_TO, FirebaseUtility.getUserID())
            .orderBy(FirebaseNames.CHAT_TIMESTAMP)
            .limit(1)  // get the latest ChatMessage object
            .addSnapshotListener { snapshot, error ->       // listen for real time updates
                if (error != null) {
                    callback.onComplete(false)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val totalSize = snapshot.documentChanges.size
                    var fetchedItems = 0

                    if (totalSize == 0){
                        callback.onComplete(true)
                        return@addSnapshotListener
                    }

                    for (documentChange in snapshot.documentChanges) {
                        // listen for new messages
                        if (documentChange.type == DocumentChange.Type.MODIFIED) {
                            // get the last message and last message timestamp
                            val newLastMessage = documentChange.document[FirebaseNames.CHAT_CONTENT].toString()
                            val newLastMessageTimestamp = documentChange.document[FirebaseNames.CHAT_TIMESTAMP] as Long
                            val newRecipientUserID = documentChange.document[FirebaseNames.CHAT_RECIPIENT_USER_ID].toString()

                            // get the user object from id
                            UserManager.getUserFromId(newRecipientUserID, object: UserManager.UserCallback{
                                override fun onComplete(user: User?) {
                                    if (user == null){
                                        callback.onComplete(false)
                                        return
                                    }

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
                        }
                    }
                }
            }
    }
}