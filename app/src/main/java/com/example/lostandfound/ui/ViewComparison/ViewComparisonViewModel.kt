package com.example.lostandfound.ui.ViewComparison

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseManagers.NotificationManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.FirebaseManagers.UserManager.UpdateTimeCallback
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.ErrorCallback

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewComparisonViewModel : ViewModel() {
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isSecurityQuestionDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val isContactUserDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // the security question ans the user answered
    var securityQuestionAnswerFromUser: MutableState<String> = mutableStateOf("")
    var securityQuestionInputError: MutableState<String> = mutableStateOf("")

    // default lost item placeholder data
    var lostItemData = LostItem()

    // default found item placeholder data
    var foundItemData = FoundItem()

    // default claim item for the lost item placeholder
    var claim =
        Claim()  // used to check whether the found item is the claimed item of the lost item

    // score data when navigated from search activity
    var scoreData = ScoreData()


    // function to add the items to the claimed collection, and also update the item status
    fun putClaimedItems(
        context: Context,
        callback: ErrorCallback
    ) {
        val firestoreManager = FirestoreManager()

        val data: Map<String, Any> = mutableMapOf(
            FirebaseNames.CLAIM_IS_APPROVED to false,  // default false
            FirebaseNames.CLAIM_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),
            FirebaseNames.CLAIM_LOST_ITEM_ID to lostItemData.itemID,
            FirebaseNames.CLAIM_FOUND_ITEM_ID to foundItemData.itemID,
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to securityQuestionAnswerFromUser.value  // empty only when there are no sec Q
        )

        firestoreManager.putWithUniqueId(
            FirebaseNames.COLLECTION_CLAIMED_ITEMS,
            data,
            object : FirestoreManager.Callback<String> {
                override fun onComplete(result: String) {
                    // it returns the generated id
                    if (result.isEmpty()) {
                        callback.onComplete("Error claiming items")
                        return
                    }

                    // send a notification to the found user, saying that a user has claimed their item
                    NotificationManager.sendUserClaimedYourItemNotification(
                        context = context,
                        targetUserId = foundItemData.user.userID,
                        claimID = result,  // the claim id
                        foundItemName = foundItemData.itemName,
                        callback = object : NotificationManager.NotificationSendCallback {
                            override fun onComplete(result: Boolean) {
                                if (!result) {
                                    callback.onComplete("Error sending notification to user")
                                    return
                                }

                                // force mark the lost item as no longer tracking
                                ItemManager.updateIsTracking(
                                    lostItemData.itemID,
                                    false,
                                    object : ItemManager.UpdateLostCallback {
                                        override fun onComplete(result: Boolean) {
                                            if (!result) {
                                                callback.onComplete("Error updating item data")
                                                return
                                            }

                                            // add activity log
                                            firestoreManager.putWithUniqueId(
                                                FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS,
                                                mapOf(
                                                    FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 4,
                                                    FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to lostItemData.itemName + " (#" + lostItemData.itemID + ") claimed " + foundItemData.itemName + " (#" + foundItemData.itemID + ")",
                                                    FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to UserManager.getUserID(),
                                                    FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
                                                ),
                                                object : FirestoreManager.Callback<String> {
                                                    override fun onComplete(result: String?) {
                                                        // not necessary to throw an error if failed here

                                                        // set user state
                                                        UserManager.updateClaimTimestamp(object :
                                                            UpdateTimeCallback {
                                                            override fun onComplete(success: Boolean) {
                                                                callback.onComplete("")
                                                            }
                                                        })
                                                    }
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            })
    }

    // function to verify if the security question input box is valid
    fun validateSecurityQuestionInput(): Boolean {
        // reset error
        securityQuestionInputError.value = ""

        if (securityQuestionAnswerFromUser.value.isEmpty()) {
            securityQuestionInputError.value = "This answer cannot be empty"
            return false
        }

        return true
    }
}