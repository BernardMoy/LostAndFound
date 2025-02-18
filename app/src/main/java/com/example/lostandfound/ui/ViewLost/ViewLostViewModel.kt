package com.example.lostandfound.ui.ViewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.google.android.gms.maps.model.LatLng

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewLostViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isTrackUpdateLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isContactUserDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isDeleteDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // default lost item placeholder data
    // will be replaced by method below
    var itemData = LostItem()
    val isItemTracking: MutableState<Boolean> = mutableStateOf(false)  // to be reflected in the ui after pressing button

    // username used to display the user
    var lostUser = User()

    // function to get user name and load it into the var
    fun getUser(callback: Callback<Boolean>){
        UserManager.getUserFromId(itemData.userID, object: UserManager.UserCallback{
            override fun onComplete(user: User?) {
                if (user == null){
                    callback.onComplete(false)
                    return
                }

                lostUser = user
                callback.onComplete(true)
            }

        })
    }

    // function to update the lost item to be tracking
    fun updateIsTracking(
        isTracking: Boolean,
        callback: Callback<Boolean>
    ){
        ItemManager.updateIsTracking(
            itemData.itemID,
            isTracking,
            object: ItemManager.UpdateLostCallback{
                override fun onComplete(result: Boolean) {
                    if (!result){
                        callback.onComplete(false)
                        return
                    }

                    // update the data in the LostItem object
                    itemData.isTracking = isTracking

                    // update the mutable state to be displayed in the ui
                    isItemTracking.value = isTracking


                    // add activity log item
                    val firestoreManager = FirestoreManager()
                    firestoreManager.putWithUniqueId(
                        FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS,
                        mapOf(
                            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to if (isTracking) 2 else 3,
                            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to itemData.itemName + " (#" + itemData.itemID + ")",
                            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to FirebaseUtility.getUserID(),
                            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
                        ),
                        object: FirestoreManager.Callback<String>{
                            override fun onComplete(result: String?) {
                                // not necessary to throw an error if failed here
                                callback.onComplete(true)
                            }
                        }
                    )
                }
            }
        )
    }

    // function to delete item
    // return true if successful, false if failed
    fun deleteItem(callback: Callback<Boolean>){
        val firestoreManager = FirestoreManager()
        firestoreManager.delete(
            FirebaseNames.COLLECTION_LOST_ITEMS,
            itemData.itemID,
            object: FirestoreManager.Callback<Boolean>{
                override fun onComplete(result: Boolean?) {
                    if (result == null || !result){
                        callback.onComplete(false)
                        return
                    }

                    // add activity log item
                    firestoreManager.putWithUniqueId(
                        FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS,
                        mapOf(
                            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 6,
                            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to itemData.itemName + " (#" + itemData.itemID + ")",
                            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to FirebaseUtility.getUserID(),
                            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
                        ),
                        object: FirestoreManager.Callback<String>{
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