package com.example.lostandfound.ui.ViewClaim

import android.content.Context
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
import com.example.lostandfound.Utility.DateTimeManager
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
                                lostUser = lostItemData.user

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
                                            foundUser = foundItem.user

                                            // callback with no errors
                                            callback.onComplete("")
                                        }
                                    })
                            }
                        })
                }
            })


    }

    // function to mark the claim as accepted
    // once a claim is approved, it cannot be un-approved
    fun approveClaim(
        context: Context,
        callback: ErrorCallback
    ) {
        val firestoreManager = FirestoreManager()
        firestoreManager.update(FirebaseNames.COLLECTION_CLAIMED_ITEMS,
            claimData.claimID,
            FirebaseNames.CLAIM_IS_APPROVED,
            true,  // set is approved to true
            object : FirestoreManager.Callback<Boolean> {
                override fun onComplete(result: Boolean) {
                    if (!result) {
                        callback.onComplete("Failed approving claim")
                        return
                    }

                    // send notification type 1 (To the target lost user) and 2 (To other lost users)
                    sendApprovalNotification(context, object : Callback<Boolean> {
                        override fun onComplete(result: Boolean) {
                            // continue regardless of result

                            sendRejectionNotifications(context, object : Callback<Boolean> {
                                override fun onComplete(result: Boolean) {
                                    // continue regardless of result

                                    // add to activity log
                                    firestoreManager.putWithUniqueId(
                                        FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS,
                                        mapOf(
                                            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 5,
                                            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to
                                                    "Approved " + lostItemData.itemName + " (#" + lostItemData.itemID + ") to "
                                                    + foundItemData.itemName + " (#" + foundItemData.itemID + ")",
                                            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to UserManager.getUserID(),
                                            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
                                        ),
                                        object : FirestoreManager.Callback<String> {
                                            override fun onComplete(result: String?) {
                                                // not necessary to throw an error if failed here
                                                // open done activity
                                                callback.onComplete("")
                                            }
                                        }
                                    )
                                }
                            })
                        }
                    })
                }
            }
        )
    }

    // function to send notification type 1 to the approved user
    fun sendApprovalNotification(
        context: Context,
        callback: Callback<Boolean>
    ) {
        NotificationManager.sendClaimApprovedNotification(
            context,
            lostUser.userID,
            claimData.claimID,  // current claim id
            object : NotificationManager.NotificationSendCallback {
                override fun onComplete(result: Boolean) {
                    if (!result) {
                        callback.onComplete(false)
                        return
                    }

                    callback.onComplete(true)
                }
            }
        )
    }

    // function to send notification type 2 to the rejected users
    fun sendRejectionNotifications(
        context: Context,
        callback: Callback<Boolean>
    ) {
        // get all claims from the found item
        ItemManager.getClaimsFromFoundId(
            foundItemData.itemID,
            object : ItemManager.FoundClaimCallback {
                override fun onComplete(claimList: MutableList<Claim>) {
                    // For every claim, get the user id of the lost item
                    val claimListSize = claimList.size
                    var sentItems = 0
                    if (claimListSize == 0) {
                        callback.onComplete(true)
                        return
                    }

                    for (claim in claimList) {
                        ItemManager.getLostItemFromId(
                            claim.lostItemID,
                            object : ItemManager.LostItemCallback {
                                override fun onComplete(lostItem: LostItem?) {
                                    if (lostItem == null) {
                                        callback.onComplete(false)
                                        return
                                    }

                                    // send notif type 2 to every user that isnt the user of the current claim
                                    if (lostItem.user.userID != lostUser.userID) {
                                        NotificationManager.sendClaimRejectedNotification(
                                            context,
                                            lostItem.user.userID,
                                            claim.claimID,  // pass the current claim data
                                            object : NotificationManager.NotificationSendCallback {
                                                override fun onComplete(result: Boolean) {
                                                    // continue regardless of result
                                                    // return true after all has been sent
                                                    sentItems++
                                                    if (sentItems == claimListSize) {
                                                        callback.onComplete(true)
                                                        return
                                                    }
                                                }
                                            }
                                        )
                                    } else {
                                        // return true after all has been sent
                                        sentItems++
                                        if (sentItems == claimListSize) {
                                            callback.onComplete(true)
                                            return
                                        }
                                    }
                                }
                            })
                    }
                }
            })
    }
}