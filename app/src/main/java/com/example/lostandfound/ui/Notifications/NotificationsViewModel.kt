package com.example.lostandfound.ui.Notifications

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.FirebaseManagers.NotificationManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

interface LoadNotificationsCallback {
    fun onComplete(result: Boolean)
}

class NotificationsViewModel : ViewModel() {
    val isItemsUnread: MutableState<Boolean> = mutableStateOf(false)
    val isMessagesUnread: MutableState<Boolean> = mutableStateOf(true)

    val isItemsLoading: MutableState<Boolean> = mutableStateOf(true)

    // keep track of the listener registration
    private var listenerRegistration: ListenerRegistration? = null

    // store a list of notifications in maps (Instead of classes), since notifs can have different formats
    var itemsNotificationList: MutableList<Map<String, Any>> =
        mutableStateListOf()  // make this listenable


    /*
    fun loadItemsNotifications(callback: LoadNotificationsCallback){
        val db = FirebaseFirestore.getInstance()

        // clear chat message preview
        itemsNotificationList.clear()

        // fetch all notifications with user_id = current user id
        listenerRegistration = db.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
            .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, FirebaseUtility.getUserID())
            .orderBy(FirebaseNames.NOTIFICATION_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->       // listen for real time updates
                // clear notifications preview
                itemsNotificationList.clear()

                if (error != null) {
                    Log.d("Snapshot error", error.message.toString())
                    callback.onComplete(false)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (documentChange in snapshot.documentChanges) {
                        // listen for added entries and modified (Whether is read)
                        if (documentChange.type == DocumentChange.Type.ADDED
                            || documentChange.type == DocumentChange.Type.MODIFIED) {
                            // add the map to the items notification list
                            val thisNotification = documentChange.document.data as MutableMap<String, Any>

                            // add the snapshot id to the notification map
                            thisNotification[FirebaseNames.NOTIFICATION_ID] = documentChange.document.id

                            itemsNotificationList.add(thisNotification)
                        }
                    }
                }

                // return true
                callback.onComplete(true)
            }
    }

     */

    // a regular load method
    fun loadItemsNotifications(callback: LoadNotificationsCallback) {
        val db = FirebaseFirestore.getInstance()
        itemsNotificationList.clear()

        // get all notifications of the current user from the db
        db.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
            .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, UserManager.getUserID())
            .orderBy(FirebaseNames.NOTIFICATION_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // the user does not have any notifications
                    callback.onComplete(true)
                    return@addOnSuccessListener
                }

                for (document in snapshot.documents) {
                    val thisNotification = document.data?.toMutableMap() ?: mutableMapOf()
                    // add the notification id to the map
                    thisNotification[FirebaseNames.NOTIFICATION_ID] = document.id
                    itemsNotificationList.add(thisNotification)
                }

                // return true after all elements are added
                callback.onComplete(true)

            }.addOnFailureListener { error ->
                Log.d("Notifications fetching error", error.message.toString())
                callback.onComplete(false)

            }
    }

    // method to mark all notifications as read
    // return true if successful false otherwise
    fun markAllAsRead(callback: LoadNotificationsCallback) {
        NotificationManager.markAllNotificationsAsRead(
            UserManager.getUserID(),
            object : NotificationManager.NotificationUpdateCallback {
                override fun onComplete(result: Boolean) {
                    if (!result) {
                        callback.onComplete(false)
                        return
                    }

                    callback.onComplete(true)
                }
            }
        )
    }

    // clear the previous listener when the view model is destroyed
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}