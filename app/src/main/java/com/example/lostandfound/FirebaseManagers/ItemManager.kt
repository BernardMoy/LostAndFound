package com.example.lostandfound.FirebaseManagers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.ClaimPreview
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.MatchingFunctions.SCORE_THRESHOLD
import com.example.lostandfound.MatchingFunctions.ScoreDataCallback
import com.example.lostandfound.MatchingFunctions.getMatchingScores
import com.example.lostandfound.Utility.LocationManager
import com.google.firebase.firestore.FirebaseFirestore

object ItemManager {
    interface LostItemCallback {
        fun onComplete(lostItem: LostItem?)  // return the lost item generated, or null if failed
    }

    interface FoundItemCallback {
        fun onComplete(foundItem: FoundItem?)  // return the found item generated, or null if failed
    }

    interface StatusCallback {
        fun onComplete(status: Int) // return the status, which must be 0, 1 or 2 (0 if error)
    }

    interface LostClaimCallback {
        fun onComplete(claim: Claim?)  // return the claim, or null if there arent any
    }

    interface FoundClaimCallback {
        fun onComplete(claimList: MutableList<Claim>)  // return the list of claims, or empty list if failed
    }

    interface ClaimCallback {
        fun onComplete(claim: Claim?)  // return the claim, or null if failed
    }

    interface ClaimPreviewCallback {
        fun onComplete(claimPreview: ClaimPreview?)  // return the claim preview or null if failed
    }

    // the special callback when attempting to search for found items from a lost item
    interface MatchFoundCallback {
        fun onComplete(result: MutableList<Pair<FoundItem, ScoreData>>?)    // return a nullable pair with found item list and the scores
    }

    interface MatchLostCallback {
        fun onComplete(result: MutableList<Pair<LostItem, ScoreData>>?)
    }

    interface UpdateLostCallback {
        fun onComplete(result: Boolean)
    }

    // method to get the lostitem as a LostItem object when given a lost item id
    fun getLostItemFromId(lostItemID: String, callback: LostItemCallback) {
        val firestoreManager = FirestoreManager()
        val firebaseStorageManager = FirebaseStorageManager()

        firestoreManager.get(
            FirebaseNames.COLLECTION_LOST_ITEMS,
            lostItemID,
            object : Callback<Map<String, Any>> {
                override fun onComplete(itemResult: Map<String, Any>?) {
                    // if itemResult is null, the item does not exist
                    if (itemResult == null) {
                        callback.onComplete(null)
                        return
                    }

                    // get the image of the item
                    firebaseStorageManager.getImage(
                        FirebaseNames.FOLDER_LOST_IMAGE,
                        lostItemID,
                        object : FirebaseStorageManager.Callback<Uri?> {
                            override fun onComplete(result: Uri?) {
                                // initialise the item image to the default image
                                var itemImage: Uri =
                                    Uri.parse("")

                                // if the result is not null, replace it by actual item image
                                if (result != null) {
                                    itemImage = result
                                }

                                // get the status of the item
                                getLostItemStatus(lostItemID, object : StatusCallback {
                                    override fun onComplete(status: Int) {
                                        // create lost item class object
                                        val thisLostItem = LostItem(
                                            itemID = lostItemID,
                                            userID = itemResult[FirebaseNames.LOSTFOUND_USER] as? String
                                                ?: "Unknown",
                                            itemName = itemResult[FirebaseNames.LOSTFOUND_ITEMNAME] as? String
                                                ?: "Unknown",
                                            category = itemResult[FirebaseNames.LOSTFOUND_CATEGORY] as? String
                                                ?: "Unknown",
                                            subCategory = itemResult[FirebaseNames.LOSTFOUND_SUBCATEGORY] as? String
                                                ?: "Unknown",
                                            color = itemResult[FirebaseNames.LOSTFOUND_COLOR] as? List<String>
                                                ?: listOf(),
                                            brand = itemResult[FirebaseNames.LOSTFOUND_BRAND] as? String
                                                ?: "Unknown",
                                            dateTime = itemResult[FirebaseNames.LOSTFOUND_EPOCHDATETIME] as? Long
                                                ?: 0L,
                                            location = if (itemResult[FirebaseNames.LOSTFOUND_LOCATION] != null) LocationManager.LocationToPair(
                                                itemResult[FirebaseNames.LOSTFOUND_LOCATION] as HashMap<*, *>
                                            ) else null,
                                            description = itemResult[FirebaseNames.LOSTFOUND_DESCRIPTION] as? String
                                                ?: "Unknown",
                                            timePosted = itemResult[FirebaseNames.LOSTFOUND_TIMEPOSTED] as? Long
                                                ?: 0L,
                                            status = status,
                                            image = itemImage.toString(),  // uri to string
                                            isTracking = itemResult[FirebaseNames.LOST_IS_TRACKING] as? Boolean
                                                ?: false
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


    // function to update the tracking status of the lost item to the isTracking value
    // return true if successful, false otherwise
    fun updateIsTracking(
        lostItemID: String,
        isTracking: Boolean,
        callback: UpdateLostCallback
    ) {
        val firestoreManager: FirestoreManager = FirestoreManager()
        firestoreManager.update(
            FirebaseNames.COLLECTION_LOST_ITEMS,
            lostItemID,
            FirebaseNames.LOST_IS_TRACKING,
            isTracking,
            object : Callback<Boolean> {
                override fun onComplete(result: Boolean) {
                    if (!result) {
                        callback.onComplete(false)
                        return
                    }

                    // return successful
                    callback.onComplete(true)
                }
            }
        )
    }


    // method to get the founditem as a LostItem object when given a found item id
    fun getFoundItemFromId(foundItemID: String, callback: FoundItemCallback) {
        val firestoreManager = FirestoreManager()
        val firebaseStorageManager = FirebaseStorageManager()

        firestoreManager.get(
            FirebaseNames.COLLECTION_FOUND_ITEMS,
            foundItemID,
            object : Callback<Map<String, Any>> {
                override fun onComplete(itemResult: Map<String, Any>?) {
                    // if itemResult is null, the item does not exist
                    if (itemResult == null) {
                        callback.onComplete(null)
                        return
                    }

                    // get the image of the item
                    firebaseStorageManager.getImage(
                        FirebaseNames.FOLDER_FOUND_IMAGE,
                        foundItemID,
                        object : FirebaseStorageManager.Callback<Uri?> {
                            override fun onComplete(result: Uri?) {
                                // initialise the item image to the default image
                                var itemImage: Uri =
                                    Uri.parse("")

                                // if the result is not null, replace it by actual item image
                                if (result != null) {
                                    itemImage = result
                                }

                                // get the status of the item
                                getFoundItemStatus(
                                    foundItemID,
                                    object : StatusCallback {
                                        override fun onComplete(status: Int) {
                                            // create found item class object
                                            val thisFoundItem = FoundItem(
                                                itemID = foundItemID,
                                                userID = itemResult[FirebaseNames.LOSTFOUND_USER] as? String
                                                    ?: "Unknown",
                                                itemName = itemResult[FirebaseNames.LOSTFOUND_ITEMNAME] as? String
                                                    ?: "Unknown",
                                                category = itemResult[FirebaseNames.LOSTFOUND_CATEGORY] as? String
                                                    ?: "Unknown",
                                                subCategory = itemResult[FirebaseNames.LOSTFOUND_SUBCATEGORY] as? String
                                                    ?: "Unknown",
                                                color = itemResult[FirebaseNames.LOSTFOUND_COLOR] as? List<String>
                                                    ?: listOf(),
                                                brand = itemResult[FirebaseNames.LOSTFOUND_BRAND] as? String
                                                    ?: "Unknown",
                                                dateTime = itemResult[FirebaseNames.LOSTFOUND_EPOCHDATETIME] as? Long
                                                    ?: 0L,
                                                location = if (itemResult[FirebaseNames.LOSTFOUND_LOCATION] != null) LocationManager.LocationToPair(
                                                    itemResult[FirebaseNames.LOSTFOUND_LOCATION] as HashMap<*, *>
                                                ) else null,
                                                description = itemResult[FirebaseNames.LOSTFOUND_DESCRIPTION] as? String
                                                    ?: "Unknown",
                                                timePosted = itemResult[FirebaseNames.LOSTFOUND_TIMEPOSTED] as? Long
                                                    ?: 0L,
                                                status = status,
                                                image = itemImage.toString(),  // uri to string
                                                securityQuestion = itemResult[FirebaseNames.FOUND_SECURITY_Q] as? String
                                                    ?: "Unknown",
                                                securityQuestionAns = itemResult[FirebaseNames.FOUND_SECURITY_Q_ANS] as? String
                                                    ?: "Unknown"
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

    // method to get claim id given a lost item.
    // A lost item can only associate with one claim id
    fun getClaimFromLostId(lostItemID: String, callback: LostClaimCallback) {
        val firestoreManager = FirestoreManager()

        firestoreManager.getIdsWhere(FirebaseNames.COLLECTION_CLAIMED_ITEMS,
            FirebaseNames.CLAIM_LOST_ITEM_ID,
            lostItemID,
            FirebaseNames.CLAIM_TIMESTAMP,   // order by
            object : Callback<List<String>> {
                override fun onComplete(result: List<String>?) {
                    if (result.isNullOrEmpty()) {
                        callback.onComplete(null)
                        return

                    }
                    // there will only be one claim associated with lost item
                    val claimID = result[0]

                    firestoreManager.get(
                        FirebaseNames.COLLECTION_CLAIMED_ITEMS,
                        claimID,
                        object : Callback<Map<String, Any>> {
                            override fun onComplete(result: Map<String, Any>?) {
                                if (result == null) {
                                    callback.onComplete(null)
                                    return
                                }

                                // construct the claim item
                                val thisClaim: Claim = Claim(
                                    claimID = claimID,
                                    lostItemID = result[FirebaseNames.CLAIM_LOST_ITEM_ID]?.toString()
                                        ?: "Unknown",
                                    foundItemID = result[FirebaseNames.CLAIM_FOUND_ITEM_ID]?.toString()
                                        ?: "Unknown",
                                    isApproved = result[FirebaseNames.CLAIM_IS_APPROVED] as? Boolean
                                        ?: false,
                                    timestamp = result[FirebaseNames.CLAIM_TIMESTAMP] as? Long
                                        ?: 0L,
                                    securityQuestionAns = result[FirebaseNames.CLAIM_SECURITY_QUESTION_ANS]?.toString()
                                        ?: "Unknown"
                                )

                                // return the claim item
                                callback.onComplete(thisClaim)
                            }
                        })

                }
            }
        )
    }


    // method to get claim ids given a found item.
    // A found item can be associated with more than one claims
    fun getClaimsFromFoundId(foundItemID: String, callback: FoundClaimCallback) {
        val firestoreManager = FirestoreManager()

        firestoreManager.getIdsWhere(FirebaseNames.COLLECTION_CLAIMED_ITEMS,
            FirebaseNames.CLAIM_FOUND_ITEM_ID,
            foundItemID,
            FirebaseNames.CLAIM_TIMESTAMP,
            object : Callback<List<String>> {
                override fun onComplete(result: List<String>?) {
                    if (result.isNullOrEmpty()) {
                        callback.onComplete(mutableListOf()) // return empty list if that found item has no claims
                        return

                    }

                    // for each result, get its claim id and construct object
                    val claimListSize = result.size
                    var fetchedItems = 0
                    val claimList: MutableList<Claim> = mutableListOf()

                    result.forEach { claimID ->
                        firestoreManager.get(
                            FirebaseNames.COLLECTION_CLAIMED_ITEMS,
                            claimID,
                            object : Callback<Map<String, Any>> {
                                override fun onComplete(result: Map<String, Any>?) {
                                    // if any fails, return empty list
                                    if (result == null) {
                                        callback.onComplete(mutableListOf())
                                        return
                                    }

                                    // construct the claim item
                                    val thisClaim: Claim = Claim(
                                        claimID = claimID,
                                        lostItemID = result[FirebaseNames.CLAIM_LOST_ITEM_ID]?.toString()
                                            ?: "Unknown",
                                        foundItemID = result[FirebaseNames.CLAIM_FOUND_ITEM_ID]?.toString()
                                            ?: "Unknown",
                                        isApproved = result[FirebaseNames.CLAIM_IS_APPROVED] as? Boolean
                                            ?: false,
                                        timestamp = result[FirebaseNames.CLAIM_TIMESTAMP] as? Long
                                            ?: 0L,
                                        securityQuestionAns = result[FirebaseNames.CLAIM_SECURITY_QUESTION_ANS]?.toString()
                                            ?: "Unknown"
                                    )

                                    //add the item to the list
                                    claimList.add(thisClaim)

                                    fetchedItems++
                                    if (fetchedItems == claimListSize) {
                                        // return the list
                                        callback.onComplete(claimList)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }

    // get claim from claim id, used to access view claim from notifications
    fun getClaimFromClaimId(claimID: String, callback: ClaimCallback) {
        val firestoreManager = FirestoreManager()

        firestoreManager.get(
            FirebaseNames.COLLECTION_CLAIMED_ITEMS,
            claimID,
            object : Callback<Map<String, Any>> {
                override fun onComplete(result: Map<String, Any>?) {
                    if (result == null) {
                        callback.onComplete(null)
                        return
                    }

                    // construct the claim item
                    val thisClaim: Claim = Claim(
                        claimID = claimID,
                        lostItemID = result[FirebaseNames.CLAIM_LOST_ITEM_ID]?.toString()
                            ?: "Unknown",
                        foundItemID = result[FirebaseNames.CLAIM_FOUND_ITEM_ID]?.toString()
                            ?: "Unknown",
                        isApproved = result[FirebaseNames.CLAIM_IS_APPROVED] as? Boolean ?: false,
                        timestamp = result[FirebaseNames.CLAIM_TIMESTAMP] as? Long ?: 0L,
                        securityQuestionAns = result[FirebaseNames.CLAIM_SECURITY_QUESTION_ANS]?.toString()
                            ?: "Unknown"
                    )

                    // return the claim item
                    callback.onComplete(thisClaim)
                }
            })
    }

    // method to get a ClaimPreview object given a Claim object
    fun getClaimPreviewFromClaim(claimItem: Claim, callback: ClaimPreviewCallback) {
        // get the lost item name and image
        getLostItemFromId(claimItem.lostItemID, object : LostItemCallback {
            override fun onComplete(lostItem: LostItem?) {
                if (lostItem == null) {
                    callback.onComplete(null)
                    return
                }

                UserManager.getUserFromId(lostItem.userID, object : UserManager.UserCallback {
                    override fun onComplete(user: com.example.lostandfound.Data.User?) {
                        if (user == null) {
                            callback.onComplete(null)
                            return
                        }

                        // create the preview item
                        val thisClaimPreview = ClaimPreview(
                            lostItemImage = lostItem.image,
                            lostItemName = lostItem.itemName,
                            lostUserName = user.firstName + ' ' + user.lastName,
                            claimItem = claimItem
                        )

                        // return the preview item
                        callback.onComplete(thisClaimPreview)
                    }
                })
            }
        })
    }

    // method to get the status given a lost item id, by querying the claim collection
    // defaults to return status 0 if the item does not exist
    fun getLostItemStatus(lostItemID: String, callback: StatusCallback) {
        val db = FirebaseFirestore.getInstance()

        // check if the item exists
        db.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
            .whereEqualTo(FirebaseNames.CLAIM_LOST_ITEM_ID, lostItemID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No claims found for the lost item -> status 0
                    callback.onComplete(0)
                    return@addOnSuccessListener
                }

                // A claim exist, then check if it is approved
                // Only one claim can exist at a time for lost items
                for (claim in querySnapshot) {
                    val isApproved = claim.getBoolean(FirebaseNames.CLAIM_IS_APPROVED) ?: false
                    if (isApproved) {
                        // claim is approved -> status 2
                        callback.onComplete(2)
                        return@addOnSuccessListener
                    }
                }

                // A claim exist but is not approved -> status 1
                callback.onComplete(1)

            }
            .addOnFailureListener { exception ->
                Log.d("Status exception", exception.message ?: "")
                callback.onComplete(0)
            }
    }

    // method to get the status given a found item id, by querying the claim collection
    // default to status 0 if not exist
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
                        if (!querySnapshot2.isEmpty) {
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

    // given a lost item, return from db all the matching found items
    // return null only when an error occurred
    // if there are no items return empty list
    fun getMatchItemsFromLostItem(
        context: Context,
        lostItem: LostItem,
        callback: MatchFoundCallback

    ) {
        val matchingItemList: MutableList<Pair<FoundItem, ScoreData>> = mutableListOf()

        // extract all found items from the database
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
            .whereNotEqualTo(  // the found item user id cannot be equal to the lost item uid
                FirebaseNames.LOSTFOUND_USER,
                lostItem.userID
            )
            .get()   // currently there are no orderby, they are done later
            .addOnSuccessListener { result ->

                // get the total length of result
                val resultSize = result.size()
                var fetchedItems = 0

                // if no result, return
                // as the code below would not be executed
                if (resultSize == 0) {
                    callback.onComplete(matchingItemList)
                    return@addOnSuccessListener
                }

                // for each retrieved item id, get their data and store that data into the itemData list
                result.forEachIndexed { index, querySnapshot ->
                    // get the found item id
                    val itemID = querySnapshot.id

                    // return the Found item from the item id
                    getFoundItemFromId(itemID, object : FoundItemCallback {
                        override fun onComplete(foundItem: FoundItem?) {
                            if (foundItem == null) {
                                callback.onComplete(null)
                                return
                            }

                            // add the data to the list only if the found item matches the lost item
                            // i.e. the overall score greater than the threshold
                            getMatchingScores(
                                context = context,
                                lostItem = lostItem,
                                foundItem = foundItem,
                                object : ScoreDataCallback {
                                    override fun onScoreCalculated(scoreData: ScoreData) {
                                        if (scoreData.overallScore >= SCORE_THRESHOLD) {
                                            matchingItemList.add(
                                                Pair(
                                                    foundItem,
                                                    scoreData
                                                )     // add both the found item and score data
                                            )
                                        }

                                        // add as fetched item regardless
                                        fetchedItems++
                                        // return true when all items have been fetched
                                        if (fetchedItems == resultSize) {
                                            // sort the data here
                                            matchingItemList.sortByDescending { key ->
                                                key.second.overallScore
                                            }

                                            // return the matching item list
                                            callback.onComplete(matchingItemList)
                                        }
                                    }
                                })
                        }
                    })
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Matching item exception", exception.message ?: "")
                callback.onComplete(null)
            }
    }

    // given a found item, return all lost items that matches it which are also being tracked.
    // return a list (Can be empty) if successful, null otherwise
    fun getTrackingMatchItemsFromFoundItem(
        context: Context,
        foundItem: FoundItem,
        callback: MatchLostCallback
    ) {
        val matchingItemList: MutableList<Pair<LostItem, ScoreData>> = mutableListOf()

        // extract all found items from the database
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .whereEqualTo(
                FirebaseNames.LOST_IS_TRACKING,
                true   // only consider tracking items
            )
            .whereNotEqualTo(
                FirebaseNames.LOSTFOUND_USER,
                foundItem.userID
            )  // the lost user id cannot be same as the current one (Found user id)
            .get()    // no need order by
            .addOnSuccessListener { result ->

                // get the total length of result
                val resultSize = result.size()
                var fetchedItems = 0

                // if no result, return
                // as the code below would not be executed
                if (resultSize == 0) {
                    callback.onComplete(matchingItemList)
                    return@addOnSuccessListener
                }

                // for each retrieved item id, get their data and store that data into the itemData list
                result.forEachIndexed { index, querySnapshot ->
                    // get the found item id
                    val itemID = querySnapshot.id

                    // return the Found item from the item id
                    getLostItemFromId(itemID, object : LostItemCallback {
                        override fun onComplete(lostItem: LostItem?) {
                            if (lostItem == null) {
                                callback.onComplete(null)
                                return
                            }

                            // add the data to the list only if the found item matches the lost item
                            getMatchingScores(
                                context = context,
                                lostItem = lostItem,
                                foundItem = foundItem,
                                object : ScoreDataCallback {
                                    override fun onScoreCalculated(scoreData: ScoreData) {
                                        if (scoreData.overallScore >= SCORE_THRESHOLD) {
                                            matchingItemList.add(Pair(lostItem, scoreData))
                                        }

                                        // add as fetched item regardless
                                        fetchedItems++

                                        // return true when all items have been fetched
                                        if (fetchedItems == resultSize) {
                                            // sort the data here
                                            matchingItemList.sortByDescending { key ->
                                                key.first.timePosted
                                            }

                                            // return the matching item list
                                            callback.onComplete(matchingItemList)
                                        }
                                    }
                                })
                        }
                    })
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Matching item exception", exception.message ?: "")
                callback.onComplete(null)
            }
    }
}