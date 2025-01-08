package com.example.lostandfound.ui.ViewClaim

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.ui.ViewComparison.Callback

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewClaimViewModel : ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // default claim placeholder data
    var claimData = Claim()

    // default lost item placeholder data
    var lostItemData = LostItem()

    // default found item placeholder data
    var foundItemData = FoundItem()

    // username used to display the found user, only that is needed
    var lostUserName = "Unknown"
    var foundUserName = "Unknown"


    // function to load the lost and found item data when given the claim item id.
    fun loadDataWithClaim(callback: ErrorCallback) {
        val firestoreManager = FirestoreManager()

        // get the lost and found item ids given the claim id
        firestoreManager.get(
            FirebaseNames.COLLECTION_CLAIMED_ITEMS,
            claimData.claimID,
            object : FirestoreManager.Callback<Map<String, Any>> {
                override fun onComplete(result: Map<String, Any>?) {
                    if (result == null) {
                        callback.onComplete("Claim data not found")
                        return
                    }

                    val lostItemId = result[FirebaseNames.CLAIM_LOST_ITEM_ID].toString()
                    val foundItemId = result[FirebaseNames.CLAIM_FOUND_ITEM_ID].toString()

                    ItemManager.getLostItemFromId(
                        lostItemId,
                        object : ItemManager.LostItemCallback {
                            override fun onComplete(lostItem: LostItem?) {
                                if (lostItem == null) {
                                    callback.onComplete("Error fetching lost item")
                                    return
                                }

                                lostItemData = lostItem

                                // get the lost user name
                                UserManager.getUsernameFromId(
                                    lostItemData.userID,
                                    object : UserManager.UsernameCallback {
                                        override fun onComplete(username: String) {
                                            if (username.isEmpty()) {
                                                callback.onComplete("Error fetching user data")
                                                return
                                            }

                                            lostUserName = username

                                            // load found data
                                            ItemManager.getFoundItemFromId(
                                                foundItemId,
                                                object : ItemManager.FoundItemCallback {
                                                    override fun onComplete(foundItem: FoundItem?) {
                                                        if (foundItem == null) {
                                                            callback.onComplete("Error fetching found item")
                                                            return
                                                        }

                                                        foundItemData = foundItem

                                                        // get the found user name
                                                        UserManager.getUsernameFromId(
                                                            foundItemData.userID,
                                                            object : UserManager.UsernameCallback {
                                                                override fun onComplete(username: String) {
                                                                    if (username.isEmpty()) {
                                                                        callback.onComplete("Error fetching user data")
                                                                        return
                                                                    }

                                                                    foundUserName = username

                                                                    // callback with no errors
                                                                    callback.onComplete("")
                                                                }
                                                            })
                                                    }
                                                })
                                        }
                                    })
                            }
                        })
                }
            })
    }

    // function to get item data
    fun getUserName(callback: Callback<Boolean>) {
        // manager
        val firestoreManager = FirestoreManager()

        // get name of the user from firebase firestore
        firestoreManager.get(
            FirebaseNames.COLLECTION_USERS,
            foundItemData.userID,
            object : FirestoreManager.Callback<Map<String, Any>> {
                override fun onComplete(result: Map<String, Any>?) {
                    if (result == null) {
                        callback.onComplete(false)

                    } else {
                        val name =
                            result[FirebaseNames.USERS_FIRSTNAME].toString() + " " + result[FirebaseNames.USERS_LASTNAME].toString()

                        // set username
                        foundUserName = name

                        // return true
                        callback.onComplete(true)
                    }
                }
            })
    }
}