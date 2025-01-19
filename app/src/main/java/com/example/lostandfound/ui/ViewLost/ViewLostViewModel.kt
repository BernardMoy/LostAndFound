package com.example.lostandfound.ui.ViewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R
import com.google.android.gms.maps.model.LatLng

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewLostViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isTrackUpdateLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isContactUserDialogShown: MutableState<Boolean> = mutableStateOf(false)

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

    // function to update the tracking status of the lost item to the isTracking value
    // return true if successful, false otherwise
    fun updateIsTracking(
        isTracking: Boolean,
        callback: Callback<Boolean>
    ){
        val firestoreManager: FirestoreManager = FirestoreManager()
        firestoreManager.update(
            FirebaseNames.COLLECTION_LOST_ITEMS,
            itemData.itemID,
            FirebaseNames.LOST_IS_TRACKING,
            isTracking,
            object: FirestoreManager.Callback<Boolean>{
                override fun onComplete(result: Boolean) {
                    if (!result){
                        callback.onComplete(false)
                        return
                    }
                    // update the data in the LostItem object
                    itemData.isTracking = isTracking

                    // update the mutable state to be displayed in the ui
                    isItemTracking.value = isTracking

                    // return successful
                    callback.onComplete(true)
                }
            }
        )
    }
}