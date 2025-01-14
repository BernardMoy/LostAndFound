package com.example.lostandfound.ui.Notifications

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel(){
    val isMatchingItemsUnread: MutableState<Boolean> = mutableStateOf(false)
    val isMessagesUnread: MutableState<Boolean> = mutableStateOf(true)


}