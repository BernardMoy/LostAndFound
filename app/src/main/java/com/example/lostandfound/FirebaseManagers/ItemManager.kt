package com.example.lostandfound.FirebaseManagers

import android.net.Uri
import android.util.Log
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.R
import com.example.lostandfound.Utility.LocationManager
import com.google.firebase.firestore.FirebaseFirestore

object ItemManager {
    interface LostItemCallback{
        fun onComplete(lostItem: LostItem?)  // return the lost item generated, or null if failed
    }

    interface FoundItemCallback{
        fun onComplete(foundItem: FoundItem?)  // return the found item generated, or null if failed
    }

    interface StatusCallback{
        fun onComplete(status: Int) // return the status, which must be 0, 1 or 2 (0 if error)
    }

    // method to get the lostitem as a LostItem object when given a lost item id
    fun getLostItemFromId(lostItemID: String, callback: LostItemCallback){
        val firestoreManager = FirestoreManager()
        val firebaseStorageManager = FirebaseStorageManager()

        firestoreManager.get(
            FirebaseNames.COLLECTION_LOST_ITEMS,
            lostItemID,
            object : FirestoreManager.Callback<Map<String, Any>> {
                override fun onComplete(itemResult: Map<String, Any>?) {
                    // if itemResult is null, fetching data failed
                    if (itemResult == null){
                        callback.onComplete(null)
                        return
                    }

                    // get the image of the item
                    firebaseStorageManager.getImage(
                        FirebaseNames.FOLDER_LOST_IMAGE,
                        lostItemID,
                        object: FirebaseStorageManager.Callback<Uri?>{
                            override fun onComplete(result: Uri?) {
                                // initialise the item image to the default image
                                var itemImage: Uri =
                                    Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image)

                                // if the result is not null, replace it by actual item image
                                if (result != null) {
                                    itemImage = result
                                }

                                // get the status of the item
                                getLostItemStatus(lostItemID, object : ItemManager.StatusCallback {
                                    override fun onComplete(status: Int) {
                                        // create lost item class object
                                        val thisLostItem = LostItem(
                                            itemID = lostItemID,
                                            userID = FirebaseUtility.getUserID(),
                                            itemName = itemResult[FirebaseNames.LOSTFOUND_ITEMNAME] as String,
                                            category = itemResult[FirebaseNames.LOSTFOUND_CATEGORY] as String,
                                            subCategory = itemResult[FirebaseNames.LOSTFOUND_SUBCATEGORY] as String,
                                            color = itemResult[FirebaseNames.LOSTFOUND_COLOR] as String,
                                            brand = itemResult[FirebaseNames.LOSTFOUND_BRAND] as String,
                                            dateTime = itemResult[FirebaseNames.LOSTFOUND_EPOCHDATETIME] as Long,
                                            location = LocationManager.LocationToPair(
                                                itemResult[FirebaseNames.LOSTFOUND_LOCATION] as HashMap<*, *>
                                            ),
                                            description = itemResult[FirebaseNames.LOSTFOUND_DESCRIPTION] as String,
                                            timePosted = itemResult[FirebaseNames.LOSTFOUND_TIMEPOSTED] as Long,
                                            status = status,
                                            image = itemImage.toString()  // uri to string
                                        )

                                        // return the generated lost item
                                        callback.onComplete(thisLostItem)
                                    }
                                })
                            }
                        }
                    )
                }
            }
        )
    }


    // method to get the founditem as a LostItem object when given a found item id
    fun getFoundItemFromId(foundItemID: String, callback: FoundItemCallback){
        val firestoreManager = FirestoreManager()
        val firebaseStorageManager = FirebaseStorageManager()

        firestoreManager.get(
            FirebaseNames.COLLECTION_FOUND_ITEMS,
            foundItemID,
            object : FirestoreManager.Callback<Map<String, Any>> {
                override fun onComplete(itemResult: Map<String, Any>?) {
                    // if itemResult is null, fetching data failed
                    if (itemResult == null){
                        callback.onComplete(null)
                        return
                    }

                    // get the image of the item
                    firebaseStorageManager.getImage(
                        FirebaseNames.FOLDER_FOUND_IMAGE,
                        foundItemID,
                        object: FirebaseStorageManager.Callback<Uri?>{
                            override fun onComplete(result: Uri?) {
                                // initialise the item image to the default image
                                var itemImage: Uri =
                                    Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image)

                                // if the result is not null, replace it by actual item image
                                if (result != null) {
                                    itemImage = result
                                }

                                // get the status of the item
                                getFoundItemStatus(foundItemID, object : ItemManager.StatusCallback {
                                    override fun onComplete(status: Int) {
                                        // create found item class object
                                        val thisFoundItem = FoundItem(
                                            itemID = foundItemID,
                                            userID = FirebaseUtility.getUserID(),
                                            itemName = itemResult[FirebaseNames.LOSTFOUND_ITEMNAME] as String,
                                            category = itemResult[FirebaseNames.LOSTFOUND_CATEGORY] as String,
                                            subCategory = itemResult[FirebaseNames.LOSTFOUND_SUBCATEGORY] as String,
                                            color = itemResult[FirebaseNames.LOSTFOUND_COLOR] as String,
                                            brand = itemResult[FirebaseNames.LOSTFOUND_BRAND] as String,
                                            dateTime = itemResult[FirebaseNames.LOSTFOUND_EPOCHDATETIME] as Long,
                                            location = LocationManager.LocationToPair(
                                                itemResult[FirebaseNames.LOSTFOUND_LOCATION] as HashMap<*, *>
                                            ),
                                            description = itemResult[FirebaseNames.LOSTFOUND_DESCRIPTION] as String,
                                            timePosted = itemResult[FirebaseNames.LOSTFOUND_TIMEPOSTED] as Long,
                                            status = status,
                                            image = itemImage.toString(),  // uri to string
                                            securityQuestion = itemResult[FirebaseNames.FOUND_SECURITY_Q] as String,
                                            securityQuestionAns = itemResult[FirebaseNames.FOUND_SECURITY_Q_ANS] as String
                                        )

                                        // return the generated found item
                                        callback.onComplete(thisFoundItem)
                                    }
                                })
                            }
                        }
                    )
                }
            }
        )
    }


    // method to get the status given a lost item id, by querying the claim collection
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

    // method to get the status given a found item id, by querying the claim collection
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
                    .addOnFailureListener { exception ->
                        Log.d("Status exception", exception.message ?: "")
                        callback.onComplete(0)
                    }

            }
            .addOnFailureListener { exception ->
                Log.d("Status exception", exception.message ?: "")
                callback.onComplete(0)
            }
    }
}