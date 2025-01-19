package com.example.lostandfound.ui.ViewClaim

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseManagers.NotificationManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.Utility.ErrorCallback

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewClaimViewModel : ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)
    var isAcceptClaimDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isContactUserDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isApproveLoading: MutableState<Boolean> = mutableStateOf(false)

    // default claim placeholder data
    var claimData = Claim()

    // default lost item placeholder data
    var lostItemData = LostItem()

    // default found item placeholder data
    var foundItemData = FoundItem()

    // username used to display the found user, only that is needed
    var lostUser = User()
    var foundUser = User()


    // function to load the lost and found item data  (lostItemData, foundItemData)
    // and also load the lost and found users
    // when given the claim item (claimData)
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

                                // get the lost user
                                UserManager.getUserFromId(
                                    lostItemData.userID,
                                    object : UserManager.UserCallback {
                                        override fun onComplete(user: User?) {
                                            if (user == null){
                                                callback.onComplete("Error fetching user data")
                                                return
                                            }

                                            lostUser = user

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
                                                        UserManager.getUserFromId(
                                                            foundItemData.userID,
                                                            object : UserManager.UserCallback {
                                                                override fun onComplete(user: User?) {
                                                                    if (user == null) {
                                                                        callback.onComplete("Error fetching user data")
                                                                        return
                                                                    }

                                                                    foundUser = user

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

    // function to mark the claim as accepted
    // once a claim is approved, it cannot be un-approved
    fun approveClaim(callback: ErrorCallback){
        val db = FirestoreManager()
        db.update(FirebaseNames.COLLECTION_CLAIMED_ITEMS,
            claimData.claimID,
            FirebaseNames.CLAIM_IS_APPROVED,
            true,  // set is approved to true
            object: FirestoreManager.Callback<Boolean>{
                override fun onComplete(result: Boolean) {
                    if (!result){
                        callback.onComplete("Failed approving claim")
                        return
                    }

                    // send notification type 1 (To the target lost user) and 2 (To other lost users)
                    NotificationManager.sendClaimApprovedNotification(
                        lostUser.userID,
                        claimData.claimID,  // current claim id
                        object: NotificationManager.NotificationSendCallback{
                            override fun onComplete(result: Boolean) {
                                if (!result){
                                    callback.onComplete("Failed sending notification")
                                    return
                                }

                                // update successful, return true
                                callback.onComplete("")
                            }
                        }
                    )
                }
            }
        )
    }
}