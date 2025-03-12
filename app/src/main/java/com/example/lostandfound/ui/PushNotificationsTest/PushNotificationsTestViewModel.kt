package com.example.lostandfound.ui.PushNotificationsTest

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.PushNotificationManagers.PushNotificationCallback
import com.example.lostandfound.PushNotificationManagers.PushNotificationManager

class PushNotificationsTestViewModel : ViewModel() {
    val fcmToken: MutableState<String> = mutableStateOf("")
    val title: MutableState<String> = mutableStateOf("")
    val content: MutableState<String> = mutableStateOf("")
}