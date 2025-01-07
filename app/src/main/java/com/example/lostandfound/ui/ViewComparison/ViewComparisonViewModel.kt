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
        timePosted = 0L,
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
        status = 0
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
        timePosted = 0L,
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
        securityQuestion = "",
        securityQuestionAns = "",
        status = 0
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
    /*
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
                                callback.onComplete("Error updating the data")

                            } else {
                                callback.onComplete("")
                            }
                        }
                    })
                }
            }
        })
    }

     */

    // function to add the items to the claimed collection, and also update the item status
    fun putClaimedItemsAndUpdate(callback: ErrorCallback){
        val firestoreManager = FirestoreManager()

        val data: Map<String, Any> = mutableMapOf(
            FirebaseNames.CLAIM_IS_APPROVED to false,  // default false
            FirebaseNames.CLAIM_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),
            FirebaseNames.CLAIM_LOST_ITEM_ID to lostItemData.itemID,
            FirebaseNames.CLAIM_FOUND_ITEM_ID to foundItemData.itemID
        )

        firestoreManager.putWithUniqueId(FirebaseNames.COLLECTION_CLAIMED_ITEMS, data, object: FirestoreManager.Callback<String>{
            override fun onComplete(result: String) {
                // it returns the generated id
                if (result.isEmpty()){
                    callback.onComplete("Error claiming items")
                    return
                }

                // return with no errors
                callback.onComplete("")
            }
        })
    }
}