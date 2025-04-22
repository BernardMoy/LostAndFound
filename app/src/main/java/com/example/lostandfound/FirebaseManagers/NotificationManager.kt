package com.example.lostandfound.FirebaseManagers

import android.content.Context
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.PushNotificationManagers.PushNotificationCallback
import com.example.lostandfound.PushNotificationManagers.PushNotificationManager
import com.example.lostandfound.Utility.DateTimeManager
import com.google.firebase.firestore.FirebaseFirestore
/*
Firebase methods for sending notifications, consisting of adding the notification to the firstore database
and sending a push notification to the corresponding user
 */
object NotificationManager {

    interface NotificationSendCallback {
        fun onComplete(result: Boolean)  // true if successful, false if fails
    }

    interface NotificationUpdateCallback {
        fun onComplete(result: Boolean)
    }

    // method to send type 0 notification to the target user
    fun sendNewMatchingItemNotification(
        context: Context,    // for push notif
        targetUserId: String,
        lostItemID: String,
        foundItemID: String,
        lostItemName: String,
        scoreData: ScoreData,
        callback: NotificationSendCallback
    ) {
        val firestoreManager = FirestoreManager()

        // construct notification data
        val data: MutableMap<String, Any> = mutableMapOf(
            FirebaseNames.NOTIFICATION_USER_ID to targetUserId,
            FirebaseNames.NOTIFICATION_TYPE to 0,
            FirebaseNames.NOTIFICATION_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),  // current time
            FirebaseNames.NOTIFICATION_IS_READ to false,
            FirebaseNames.NOTIFICATION_CATEGORY_SCORE to scoreData.categoryScore,
            FirebaseNames.NOTIFICATION_COLOR_SCORE to scoreData.colorScore,
            FirebaseNames.NOTIFICATION_OVERALL_SCORE to scoreData.overallScore,

            // also pass the lost and found item id
            FirebaseNames.NOTIFICATION_LOST_ITEM_ID to lostItemID,
            FirebaseNames.NOTIFICATION_FOUND_ITEM_ID to foundItemID
        )

        // add optional nullable values
        scoreData.imageScore?.let {
            data[FirebaseNames.NOTIFICATION_IMAGE_SCORE] = it
        }
        scoreData.brandScore?.let {
            data[FirebaseNames.NOTIFICATION_BRAND_SCORE] = it
        }
        scoreData.locationScore?.let {
            data[FirebaseNames.NOTIFICATION_LOCATION_SCORE] = it
        }

        firestoreManager.putWithUniqueId(
            FirebaseNames.COLLECTION_NOTIFICATIONS,
            data,
            object : Callback<String> {
                override fun onComplete(result: String?) {
                    if (result.isNullOrEmpty()) {
                        callback.onComplete(false)
                        return
                    }

                    // notification added to db successfully
                    // send push notification
                    PushNotificationManager.sendPushNotificationNewMatchingItem(
                        context = context,
                        userID = targetUserId,
                        lostItemName = lostItemName,
                        object : PushNotificationCallback {
                            override fun onComplete(success: Boolean) {
                                // regardless of the result, return true
                                callback.onComplete(true)
                            }
                        }
                    )
                }
            }
        )
    }

    // method to send type 1 notification to the target user
    // currently the data passed for type 1 2 3 are the same (Except the type)
    fun sendClaimApprovedNotification(
        context: Context,    // for push notif
        targetUserId: String,
        claimID: String,
        callback: NotificationSendCallback
    ) {
        val firestoreManager = FirestoreManager()

        // construct notification data
        val data: Map<String, Any> = mapOf(
            FirebaseNames.NOTIFICATION_USER_ID to targetUserId,
            FirebaseNames.NOTIFICATION_TYPE to 1,
            FirebaseNames.NOTIFICATION_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),  // current time
            FirebaseNames.NOTIFICATION_IS_READ to false,

            // also pass the claim id
            FirebaseNames.NOTIFICATION_CLAIM_ID to claimID
        )

        firestoreManager.putWithUniqueId(
            FirebaseNames.COLLECTION_NOTIFICATIONS,
            data,
            object : Callback<String> {
                override fun onComplete(result: String?) {
                    if (result.isNullOrEmpty()) {
                        callback.onComplete(false)
                        return
                    }
                    // notification added to db successfully
                    // send push notification
                    PushNotificationManager.sendPushNotificationClaimApproved(
                        context = context,
                        userID = targetUserId,
                        object : PushNotificationCallback {
                            override fun onComplete(success: Boolean) {
                                // regardless of the result, return true
                                callback.onComplete(true)
                            }
                        }
                    )
                }
            }
        )
    }

    // method to send type 2 notification to the target user
    fun sendClaimRejectedNotification(
        context: Context,    // for push notif
        targetUserId: String,
        claimID: String,
        callback: NotificationSendCallback
    ) {
        val firestoreManager = FirestoreManager()

        // construct notification data
        val data: Map<String, Any> = mapOf(
            FirebaseNames.NOTIFICATION_USER_ID to targetUserId,
            FirebaseNames.NOTIFICATION_TYPE to 2,
            FirebaseNames.NOTIFICATION_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),  // current time
            FirebaseNames.NOTIFICATION_IS_READ to false,

            // also pass the claim id
            FirebaseNames.NOTIFICATION_CLAIM_ID to claimID
        )

        firestoreManager.putWithUniqueId(
            FirebaseNames.COLLECTION_NOTIFICATIONS,
            data,
            object : Callback<String> {
                override fun onComplete(result: String?) {
                    if (result.isNullOrEmpty()) {
                        callback.onComplete(false)
                        return
                    }
                    // notification added to db successfully
                    // send push notification
                    PushNotificationManager.sendPushNotificationClaimRejected(
                        context = context,
                        userID = targetUserId,
                        object : PushNotificationCallback {
                            override fun onComplete(success: Boolean) {
                                // regardless of the result, return true
                                callback.onComplete(true)
                            }
                        }
                    )
                }
            }
        )
    }

    // method to send type 3 notification to the target user
    fun sendUserClaimedYourItemNotification(
        context: Context,    // for push notif
        targetUserId: String,
        claimID: String,
        foundItemName: String,
        callback: NotificationSendCallback
    ) {
        val firestoreManager = FirestoreManager()

        // construct notification data
        val data: Map<String, Any> = mapOf(
            FirebaseNames.NOTIFICATION_USER_ID to targetUserId,
            FirebaseNames.NOTIFICATION_TYPE to 3,
            FirebaseNames.NOTIFICATION_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),  // current time
            FirebaseNames.NOTIFICATION_IS_READ to false,

            // also pass the claim id
            FirebaseNames.NOTIFICATION_CLAIM_ID to claimID
        )

        firestoreManager.putWithUniqueId(
            FirebaseNames.COLLECTION_NOTIFICATIONS,
            data,
            object : Callback<String> {
                override fun onComplete(result: String?) {
                    if (result.isNullOrEmpty()) {
                        callback.onComplete(false)
                        return
                    }
                    // notification added to db successfully
                    // send push notification
                    PushNotificationManager.sendPushNotificationUserClaimedYourItem(
                        context = context,
                        userID = targetUserId,
                        foundItemName = foundItemName,
                        object : PushNotificationCallback {
                            override fun onComplete(success: Boolean) {
                                // regardless of the result, return true
                                callback.onComplete(true)
                            }
                        }
                    )
                }
            }
        )
    }

    // function to mark all notifications as read (isRead = true)
    // return true if the operation is successful (Even when all are read), false otherwise
    fun markAllNotificationsAsRead(userID: String, callback: NotificationUpdateCallback) {
        val db = FirebaseFirestore.getInstance()
        // get all notifications of the current user
        db.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
            .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, UserManager.getUserID())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // mark each reference of the notification to be read
                    val batch = db.batch()
                    for ((_, item) in task.result.withIndex()) {
                        batch.update(item.reference, FirebaseNames.NOTIFICATION_IS_READ, true)
                    }

                    // commit the batch
                    batch.commit().addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            callback.onComplete(true)
                        } else {
                            callback.onComplete(false)
                        }
                    }
                } else {
                    callback.onComplete(false)
                }
            }
    }
}