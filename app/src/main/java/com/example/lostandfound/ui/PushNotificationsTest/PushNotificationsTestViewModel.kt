package com.example.lostandfound.ui.PermissionsTest

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PushNotificationsTestViewModel : ViewModel() {
    val fcmToken: MutableState<String> = mutableStateOf("")
    val title: MutableState<String> = mutableStateOf("")
    val content: MutableState<String> = mutableStateOf("")
}