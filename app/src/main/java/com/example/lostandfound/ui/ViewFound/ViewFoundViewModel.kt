package com.example.lostandfound.ui.ViewFound

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.UserManager

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewFoundViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isContactUserDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // item data are stored here
    var itemData = FoundItem()

    // username used to display the user
    var foundUser = User()

    // function to get user name given found item data
    fun getUser(callback: Callback<Boolean>){
        UserManager.getUserFromId(itemData.userID, object: UserManager.UserCallback{
            override fun onComplete(user: User?) {
                if (user == null){
                    callback.onComplete(false)

                } else {
                    foundUser = user
                    callback.onComplete(true)
                }
            }
        })
    }
}