package com.example.lostandfound.ui.ChatInbox

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.ChatMessagePreview
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.Utility.DateTimeManager

interface SendMessageCallback{
    fun onComplete(result: Boolean)
}

interface FetchMessageCallback{
    fun onComplete(result: Boolean)
}

class ChatInboxViewModel: ViewModel() {
    // only store the user that the current user is chatting with, because the second user must be the current user
    // loaded on create
    var chatUser: User = User()

    // store the current typed message content
    val typedText: MutableState<String> = mutableStateOf("")

    // store a list of chat previews
    var chatMessagePreviewList: MutableList<ChatMessagePreview> = mutableListOf()

    // whether the past chats are loading
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    // validate if the message to send is valid
    fun validateMessage(): Boolean{
        if (typedText.value.trim().isEmpty()){
            return false
        }
        return true
    }

    // method to send message
    fun sendMessage(callback: SendMessageCallback){
        val db = FirestoreManager()

        // create chat data
        val data = mapOf(
            FirebaseNames.CHAT_SENDER_USER_ID to FirebaseUtility.getUserID(),
            FirebaseNames.CHAT_RECIPIENT_USER_ID to chatUser.userID,
            FirebaseNames.CHAT_FROM_TO to listOf(FirebaseUtility.getUserID(),chatUser.userID),  // [from user, to user]
            FirebaseNames.CHAT_CONTENT to typedText.value,
            FirebaseNames.CHAT_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
        )

        // add the message content from typedText
        db.putWithUniqueId(
            FirebaseNames.COLLECTION_CHATS,
            data,
            object: Callback<String>{
                override fun onComplete(result: String) {
                    if (result.isEmpty()){
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
    fun fetchMessagePreviews(callback: FetchMessageCallback){
        val firestoreManager = FirestoreManager()

        // clear chat message preview
        chatMessagePreviewList.clear()

        firestoreManager.getIdsWhereArrayContains(
            FirebaseNames.COLLECTION_CHATS,
            FirebaseNames.CHAT_FROM_TO,
            FirebaseUtility.getUserID(),  // contains the current user id
            FirebaseNames.CHAT_TIMESTAMP,
            object: Callback<List<String>>{
                override fun onComplete(result: List<String>?) {
                    if (result == null){
                        callback.onComplete(false)
                        return
                    }

                    // for each chat id, get its chat preview and add to list
                    val totalSize = result.size
                    var fetchedItems = 0

                    result.forEach { chatMessageID ->
                        ItemManager.getChatMessagePreviewFromId(chatMessageID, object: ItemManager.ChatMessagePreviewCallback{
                            override fun onComplete(chatMessagePreview: ChatMessagePreview?) {
                                // if any is null, fail
                                if (chatMessagePreview == null){
                                    callback.onComplete(false)
                                    return
                                }

                                // add to list
                                chatMessagePreviewList.add(chatMessagePreview)

                                fetchedItems ++
                                if (fetchedItems == totalSize){
                                    callback.onComplete(true)
                                }
                            }
                        })
                    }
                }
            }
        )
    }
}