package com.example.lostandfound.ui.ViewFound

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.ui.ViewLost.Callback

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewFoundViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // item data are stored here
    var itemData = FoundItem(
        itemID = "Unknown",
        userID = "Unknown",
        itemName = "Unknown",
        category = "Unknown",
        subCategory = "Unknown",
        color = "Unknown",
        brand = "",
        dateTime = 0L,
        location = Pair(52.37930763817003,-1.5614912710215834),
        description = "",
        status = 0,
        timePosted = 0L,
        image = "",
        securityQuestion = "",
        securityQuestionAns = ""
    )

    // username used to display the user
    var userName = "Unknown"

    // function to get item data
    fun getUserName(callback: com.example.lostandfound.ui.ViewFound.Callback<Boolean>){

        // managers
        val firestoreManager = FirestoreManager()

        // get name of the user from firebase firestore
        firestoreManager.get(FirebaseNames.COLLECTION_USERS, itemData.userID, object : FirestoreManager.Callback<Map<String, Any>>{
            override fun onComplete(result: Map<String, Any>?) {
                if (result == null) {
                    callback.onComplete(false)

                } else {
                    val name = result[FirebaseNames.USERS_FIRSTNAME].toString() + " " + result[FirebaseNames.USERS_LASTNAME].toString()

                    // set username
                    userName = name

                    // return true
                    callback.onComplete(true)
                }
            }
        })
    }
}