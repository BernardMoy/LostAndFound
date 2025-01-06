package com.example.lostandfound.ui.ViewComparison

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.ErrorCallback

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewComparisonViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // default lost item placeholder data
    var lostItemData = LostItem(
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
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image
    )

    // default found item placeholder data
    var foundItemData = FoundItem(
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
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
        securityQuestion = "",
        securityQuestionAns = ""
    )

    // username used to display the found user, only that is needed
    var foundUserName = "Unknown"

    // function to get item data
    fun getUserName(callback: Callback<Boolean>){

        // manager
        val firestoreManager = FirestoreManager()

        // get name of the user from firebase firestore
        firestoreManager.get(FirebaseNames.COLLECTION_USERS, foundItemData.userID, object : FirestoreManager.Callback<Map<String, Any>>{
            override fun onComplete(result: Map<String, Any>?) {
                if (result == null) {
                    callback.onComplete(false)

                } else {
                    val name = result[FirebaseNames.USERS_FIRSTNAME].toString() + " " + result[FirebaseNames.USERS_LASTNAME].toString()

                    // set username
                    foundUserName = name

                    // return true
                    callback.onComplete(true)
                }
            }
        })
    }


    // function to update item status
    fun updateItemStatus(lostItemStatus: Int, foundItemStatus: Int, callback: ErrorCallback){
        // update the current lost and found item status
        val firestoreManager = FirestoreManager()

        // update the lost item
        firestoreManager.update(FirebaseNames.COLLECTION_LOST_ITEMS, lostItemData.itemID, FirebaseNames.LOSTFOUND_STATUS, lostItemStatus, object : FirestoreManager.Callback<Boolean>{
            override fun onComplete(result: Boolean) {
                if (!result){
                    callback.onComplete("Error updating lost data")

                } else {
                    // also update the found item
                    firestoreManager.update(FirebaseNames.COLLECTION_FOUND_ITEMS, foundItemData.itemID, FirebaseNames.LOSTFOUND_STATUS, foundItemStatus, object : FirestoreManager.Callback<Boolean>{
                        override fun onComplete(result: Boolean) {
                            if (!result){
                                callback.onComplete("Error updating found data")

                            } else {
                                callback.onComplete("")
                            }
                        }
                    })
                }
            }
        })
    }
}