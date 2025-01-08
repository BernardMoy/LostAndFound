package com.example.lostandfound.ui.ViewClaim

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.ui.ViewComparison.Callback

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewClaimViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    var claimId: String = ""

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
    var lostUserName = "Unknown"
    var foundUserName = "Unknown"


    // function to load the lost and found item data when given the claim item id.
    fun loadDataWithClaimId(callback: ErrorCallback){
        val firestoreManager = FirestoreManager()

        // get the lost and found item ids given the claim id
        firestoreManager.get(FirebaseNames.COLLECTION_CLAIMED_ITEMS, claimId, object : FirestoreManager.Callback<Map<String, Any>>{
            override fun onComplete(result: Map<String, Any>?) {
                if (result == null){
                    callback.onComplete("Claim data not found")
                    return
                }

                val lostItemId = result[FirebaseNames.CLAIM_LOST_ITEM_ID].toString()
                val foundItemId = result[FirebaseNames.CLAIM_FOUND_ITEM_ID].toString()

                // load the lost items and found items


            }
        })
    }

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
}