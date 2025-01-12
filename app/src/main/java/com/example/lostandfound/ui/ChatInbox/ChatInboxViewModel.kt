package com.example.lostandfound.ui.ChatInbox

import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.User

class ChatInboxViewModel: ViewModel() {
    // only store the user that the current user is chatting with, because the second user must be the current user
    // loaded on create
    var chatUser: User = User(
        firstName = "E"
    )
}