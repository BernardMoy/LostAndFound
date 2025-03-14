package com.example.lostandfound.ui.SettingsPushNotifications

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsPushNotificationsViewModel : ViewModel() {
    val isItemNotificationChecked: MutableState<Boolean> = mutableStateOf(false)
    val isMessageNotificationChecked: MutableState<Boolean> = mutableStateOf(false)
}