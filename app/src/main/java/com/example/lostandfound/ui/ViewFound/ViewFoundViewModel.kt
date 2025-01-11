package com.example.lostandfound.ui.ViewFound

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.UserManager

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewFoundViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // item data are stored here
    var itemData = FoundItem()

    // username used to display the user
    var userName = "Unknown"

    // function to get user name given found item data
    fun getUserName(callback: Callback<Boolean>){
        UserManager.getUserFromId(itemData.userID, object: UserManager.UserCallback{
            override fun onComplete(user: User?) {
                if (user == null){
                    callback.onComplete(false)

                } else {
                    userName = user.firstName + ' ' + user.lastName
                    callback.onComplete(true)
                }
            }
        })
    }
}