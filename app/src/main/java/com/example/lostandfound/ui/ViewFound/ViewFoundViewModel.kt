package com.example.lostandfound.ui.ViewFound

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.Utility.DateTimeManager

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewFoundViewModel : ViewModel() {
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isContactUserDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isDeleteDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // item data are stored here
    var itemData = FoundItem()

    // function to delete item
    // return true if successful, false if failed
    fun deleteItem(callback: Callback<Boolean>) {
        val firestoreManager = FirestoreManager()
        firestoreManager.delete(
            FirebaseNames.COLLECTION_FOUND_ITEMS,
            itemData.itemID,
            object : FirestoreManager.Callback<Boolean> {
                override fun onComplete(result: Boolean?) {
                    if (result == null || !result) {
                        callback.onComplete(false)
                        return
                    }

                    // add activity log item
                    firestoreManager.putWithUniqueId(
                        FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS,
                        mapOf(
                            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 7,
                            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to itemData.itemName + " (#" + itemData.itemID + ")",
                            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to UserManager.getUserID(),
                            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
                        ),
                        object : FirestoreManager.Callback<String> {
                            override fun onComplete(result: String?) {
                                // not necessary to throw an error if failed here
                                callback.onComplete(true)  //return true
                            }
                        }
                    )
                }
            }
        )
    }
}