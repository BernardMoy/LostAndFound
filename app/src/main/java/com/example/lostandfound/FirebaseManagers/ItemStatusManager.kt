package com.example.lostandfound.FirebaseManagers

import android.util.Log
import com.example.lostandfound.Data.FirebaseNames
import com.google.firebase.firestore.FirebaseFirestore

object ItemStatusManager {
    interface StatusCallback{
        fun onComplete(status: Int) // return the status, which must be 0, 1 or 2 (0 if error)
    }

    fun getLostItemStatus(lostItemID: String, callback: StatusCallback){
        val db = FirebaseFirestore.getInstance()

        // check if the item exists
        db.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
            .whereEqualTo(FirebaseNames.CLAIM_LOST_ITEM_ID, lostItemID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty){
                    // No claims found for the lost item -> status 0
                    callback.onComplete(0)
                    return@addOnSuccessListener
                }

                // A claim exist, then check if it is approved
                // Only one claim can exist at a time for lost items
                for (claim in querySnapshot){
                    val isApproved = claim.getBoolean(FirebaseNames.CLAIM_IS_APPROVED) ?: false
                    if (isApproved){
                        // claim is approved -> status 2
                        callback.onComplete(2)
                        return@addOnSuccessListener
                    }
                }

                // A claim exist but is not approved -> status 1
                callback.onComplete(1)

            }
            .addOnFailureListener { exception ->
                Log.d("Status exception", exception.message?:"")
                callback.onComplete(0)
            }
    }

    fun getFoundItemStatus(foundItemID: String, callback: StatusCallback) {
        val db = FirebaseFirestore.getInstance()

        // check if the item exists
        db.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
            .whereEqualTo(FirebaseNames.CLAIM_FOUND_ITEM_ID, foundItemID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No claims found for the found item -> status 0
                    callback.onComplete(0)
                    return@addOnSuccessListener
                }

                // check if the item exists in an approved claim
                db.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
                    .whereEqualTo(FirebaseNames.CLAIM_FOUND_ITEM_ID, foundItemID)
                    .whereEqualTo(FirebaseNames.CLAIM_IS_APPROVED, true)
                    .get()
                    .addOnSuccessListener { querySnapshot2 ->
                        if (!querySnapshot2.isEmpty){
                            // any approved claims -> status 2
                            callback.onComplete(2)
                            return@addOnSuccessListener
                        }

                        // There exists some claims but none are approved -> status 1
                        callback.onComplete(1)
                    }

            }
            .addOnFailureListener { exception ->
                Log.d("Status exception", exception.message ?: "")
                callback.onComplete(0)
            }
    }
}