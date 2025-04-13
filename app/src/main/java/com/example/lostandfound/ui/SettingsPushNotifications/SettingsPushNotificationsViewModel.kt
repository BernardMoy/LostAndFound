package com.example.lostandfound.ui.SettingsPushNotifications

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.UserManager
import com.google.firebase.firestore.FirebaseFirestore

interface PushNotificationSettingsCallback {
    fun onComplete(success: Boolean)
}

class SettingsPushNotificationsViewModel : ViewModel() {
    val isItemNotificationChecked: MutableState<Boolean> = mutableStateOf(false)
    val isMessageNotificationChecked: MutableState<Boolean> = mutableStateOf(false)
    val firestore = FirebaseFirestore.getInstance()
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // when the done button is clicked, update the attributes from the db
    fun updateUserSettings(callback: PushNotificationSettingsCallback) {
        firestore.collection(FirebaseNames.COLLECTION_USERS)
            .document(UserManager.getUserID())
            .update(
                mapOf(
                    FirebaseNames.USERS_ITEM_NOTIFICATION_ENABLED to isItemNotificationChecked.value,
                    FirebaseNames.USERS_MESSAGE_NOTIFICATION_ENABLED to isMessageNotificationChecked.value
                )
            )
            .addOnSuccessListener { d ->
                callback.onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase error", e.message.toString())
                callback.onComplete(false)
            }
    }

    // load the saved user settings in the db initially
    fun loadUserSettings(callback: PushNotificationSettingsCallback) {
        firestore.collection(FirebaseNames.COLLECTION_USERS)
            .document(UserManager.getUserID())
            .get()
            .addOnSuccessListener { doc ->
                isItemNotificationChecked.value =
                    (doc[FirebaseNames.USERS_ITEM_NOTIFICATION_ENABLED] as? Boolean) ?: true
                isMessageNotificationChecked.value =
                    (doc[FirebaseNames.USERS_MESSAGE_NOTIFICATION_ENABLED] as? Boolean) ?: true
                callback.onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase error", e.message.toString())
                callback.onComplete(false)
            }
    }
}