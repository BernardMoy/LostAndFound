package com.example.lostandfound.FirebaseManagers

import android.net.Uri
import android.util.Log
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.ChatMessagePreview
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.ClaimPreview
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.R
import com.example.lostandfound.Utility.LocationManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

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
        fun onComplete(claim: Claim?)  // return the claim, or null if failed
    }

    interface FoundClaimCallback {
        fun onComplete(claimList: MutableList<Claim>)  // return the list of claims, or empty list if failed
    }

    interface ClaimPreviewCallback{
        fun onComplete(claimPreview: ClaimPreview?)  // return the claim preview or null if failed
    }

    interface ChatMessagePreviewCallback{
        fun onComplete(chatMessagePreview: ChatMessagePreview?)
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
                    // if itemResult is null, fetching data failed
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
                                    Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image)

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
                                            userID = itemResult[FirebaseNames.LOSTFOUND_USER] as String,
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
    fun getFoundItemFromId(foundItemID: String, callback: FoundItemCallback) {
        val firestoreManager = FirestoreManager()
        val firebaseStorageManager = FirebaseStorageManager()

        firestoreManager.get(
            FirebaseNames.COLLECTION_FOUND_ITEMS,
            foundItemID,
            object : Callback<Map<String, Any>> {
                override fun onComplete(itemResult: Map<String, Any>?) {
                    // if itemResult is null, fetching data failed
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
                                    Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image)

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
                                                userID = itemResult[FirebaseNames.LOSTFOUND_USER] as String,
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
                                    lostItemID = result[FirebaseNames.CLAIM_LOST_ITEM_ID].toString(),
                                    foundItemID = result[FirebaseNames.CLAIM_FOUND_ITEM_ID].toString(),
                                    isApproved = result[FirebaseNames.CLAIM_IS_APPROVED] as Boolean,
                                    timestamp = result[FirebaseNames.CLAIM_TIMESTAMP] as Long,
                                    securityQuestionAns = result[FirebaseNames.CLAIM_SECURITY_QUESTION_ANS].toString()
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
                        callback.onComplete(mutableListOf())
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
                                        lostItemID = result[FirebaseNames.CLAIM_LOST_ITEM_ID].toString(),
                                        foundItemID = result[FirebaseNames.CLAIM_FOUND_ITEM_ID].toString(),
                                        isApproved = result[FirebaseNames.CLAIM_IS_APPROVED] as Boolean,
                                        timestamp = result[FirebaseNames.CLAIM_TIMESTAMP] as Long,
                                        securityQuestionAns = result[FirebaseNames.CLAIM_SECURITY_QUESTION_ANS].toString()
                                    )

                                    //add the item to the list
                                    claimList.add(thisClaim)

                                    fetchedItems ++
                                    if (fetchedItems == claimListSize){
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

    // method to get a ClaimPreview object given a Claim object
    fun getClaimPreviewFromClaim(claimItem: Claim, callback: ClaimPreviewCallback){
        // get the lost item name and image
        getLostItemFromId(claimItem.lostItemID, object: LostItemCallback{
            override fun onComplete(lostItem: LostItem?) {
                if (lostItem == null){
                    callback.onComplete(null)
                    return
                }

                UserManager.getUserFromId(lostItem.userID, object: UserManager.UserCallback{
                    override fun onComplete(user: com.example.lostandfound.Data.User?) {
                        if (user == null){
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


    // method to get chat preview from chat id
    fun getChatMessagePreviewFromId(messageID: String, callback: ChatMessagePreviewCallback) {
        // first get chat
        val firestoreManager = FirestoreManager()

        firestoreManager.get(
            FirebaseNames.COLLECTION_CHATS,
            messageID,
            object : Callback<Map<String, Any>> {
                override fun onComplete(itemResult: Map<String, Any>?) {
                    if (itemResult == null){
                        callback.onComplete(null)
                        return
                    }

                    // get the chat object
                    val senderUserID = itemResult[FirebaseNames.CHAT_SENDER_USER_ID].toString()

                    val chatMessage = ChatMessage(
                        messageID = messageID,
                        senderUserID = senderUserID,
                        recipientUserID = itemResult[FirebaseNames.CHAT_RECIPIENT_USER_ID].toString(),
                        text = itemResult[FirebaseNames.CHAT_CONTENT].toString(),
                        timestamp = itemResult[FirebaseNames.CHAT_TIMESTAMP] as Long
                    )

                    // then get the user name that is required for the chat preview
                    UserManager.getUserFromId(senderUserID, object: UserManager.UserCallback{
                        override fun onComplete(user: com.example.lostandfound.Data.User?) {
                            if (user == null){
                                callback.onComplete(null)
                                return
                            }

                            // create the chat message preview
                            val chatMessagePreview = ChatMessagePreview(
                                chatMessage,
                                user.firstName + ' ' + user.lastName
                            )

                            // return it
                            callback.onComplete(chatMessagePreview)
                        }
                    })
                }
            }
        )
    }
}