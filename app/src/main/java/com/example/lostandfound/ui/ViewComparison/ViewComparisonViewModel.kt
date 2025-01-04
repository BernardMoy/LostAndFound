package com.example.lostandfound.ui.ViewComparison

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewComparisonViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    val isLostLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isFoundLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

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
        image = ""
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
        image = "",
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
}