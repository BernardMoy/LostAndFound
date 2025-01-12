package com.example.lostandfound.ui.ChatInbox

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.Utility.DateTimeManager

interface SendMessageCallback{
    fun onComplete(result: Boolean)
}

class ChatInboxViewModel: ViewModel() {
    // only store the user that the current user is chatting with, because the second user must be the current user
    // loaded on create
    var chatUser: User = User()

    // store the current typed message content
    val typedText: MutableState<String> = mutableStateOf("")

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
}