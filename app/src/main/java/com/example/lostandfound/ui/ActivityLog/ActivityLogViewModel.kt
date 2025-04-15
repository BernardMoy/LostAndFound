package com.example.lostandfound.ui.ActivityLog

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.ActivityLogItem
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

interface FetchActivityLogCallback {
    fun onComplete(result: Boolean)
}

class ActivityLogViewModel : ViewModel() {

    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    // store the user to display the activity log items, or null if not exist (For current user)
    val userID : MutableState<String?> = mutableStateOf(null)

    // store a list of activity log items
    val itemData: MutableList<ActivityLogItem> = mutableListOf()

    fun fetchActivityLogItems(
        callback: FetchActivityLogCallback
    ) {
        itemData.clear()

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS)
            .whereEqualTo(FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID, userID.value ?: UserManager.getUserID())
            .orderBy(FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // construct activity log item data
                    val activityLogItem: ActivityLogItem = ActivityLogItem(
                        id = document.id,
                        type = (document[FirebaseNames.ACTIVITY_LOG_ITEM_TYPE] as Long).toInt(),
                        content = document[FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT].toString(),
                        userID = document[FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID].toString(),
                        timestamp = document[FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP] as Long
                    )

                    // add to list
                    itemData.add(activityLogItem)
                }

                // return true
                callback.onComplete(true)

            }
            .addOnFailureListener { error ->
                Log.e("FIRESTORE ERROR", error.message.toString())
                callback.onComplete(false)
            }
    }
}