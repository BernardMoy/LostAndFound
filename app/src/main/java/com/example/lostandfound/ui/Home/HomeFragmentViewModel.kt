package com.example.lostandfound.ui.Home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

interface Callback<T> {
    fun onComplete(result: T)
}

class HomeFragmentViewModel : ViewModel() {

    val isLoadingLostItem: MutableState<Boolean> = mutableStateOf(false)
    val isLoadingFoundItem: MutableState<Boolean> = mutableStateOf(false)

    val isLoggedIn: MutableState<Boolean> = mutableStateOf(FirebaseUtility.isUserLoggedIn())

    // for displaying the small lost item
    var latestLostItem: MutableState<LostItem?> =
        mutableStateOf(null)  // if this is null, then the user has no recently lost items
    var numberFound: MutableState<Int> = mutableIntStateOf(0)
    var numberClaimApproved: MutableState<Int> = mutableIntStateOf(0)

    var isInitialStartUp = true

    var isWelcomeDialogShown: MutableState<Boolean> = mutableStateOf(false)


    // load data into latestLostItem and latestLostItemMatches
    fun loadLatestLostItem(
        callback: Callback<Boolean>
    ) {
        // if not logged in, reset it
        if (!FirebaseUtility.isUserLoggedIn()) {
            latestLostItem.value = null
            callback.onComplete(true)
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .whereEqualTo(FirebaseNames.LOSTFOUND_USER, FirebaseUtility.getUserID())
            .orderBy(FirebaseNames.LOSTFOUND_TIMEPOSTED, Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // set the latest item to null
                    latestLostItem.value = null
                    callback.onComplete(true)

                } else {
                    // get the lost item id
                    val lostItemID = result.documents[0].id

                    // get the lost item
                    ItemManager.getLostItemFromId(
                        lostItemID,
                        object : ItemManager.LostItemCallback {
                            override fun onComplete(lostItem: LostItem?) {
                                if (lostItem == null) {
                                    callback.onComplete(false)
                                    return
                                }

                                // set data
                                latestLostItem.value = lostItem
                                callback.onComplete(true)
                            }
                        })
                }
            }.addOnFailureListener { error ->
                Log.d("HOME PAGE LOST ITEM LOAD ERROR", error.message.toString())
                callback.onComplete(false)
            }
    }


    // method to get number of found items from the current user
    fun loadFoundItemCount(
        callback: Callback<Boolean>
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
            .whereEqualTo(FirebaseNames.LOSTFOUND_USER, FirebaseUtility.getUserID())
            .get()
            .addOnSuccessListener { querySnapshot ->
                numberFound.value = querySnapshot.size()
                callback.onComplete(true)

            }.addOnFailureListener { error ->
                Log.d("FIRESTORE ERROR", error.message.toString())
                callback.onComplete(false)
            }
    }


    // method to get number of approved claims where the found item id
    fun loadApprovedClaimsCount(
        callback: Callback<Boolean>
    ) {
        val db = FirebaseFirestore.getInstance()
        val firestoreManager = FirestoreManager()
        db.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
            .whereEqualTo(FirebaseNames.CLAIM_IS_APPROVED, true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // for each query, find its found item user
                if (querySnapshot.isEmpty) {
                    // return 0
                    numberClaimApproved.value = 0
                    callback.onComplete(true)

                } else {
                    var count = 0
                    var fetchedItem = 0
                    val totalCount = querySnapshot.size() // wont be 0

                    for (document in querySnapshot) {
                        val foundItemID = document[FirebaseNames.CLAIM_FOUND_ITEM_ID].toString()
                        firestoreManager.get(
                            FirebaseNames.COLLECTION_FOUND_ITEMS,
                            foundItemID,
                            object : FirestoreManager.Callback<Map<String, Any>> {
                                override fun onComplete(result: Map<String, Any>?) {
                                    if (result == null) {
                                        callback.onComplete(false)
                                        return
                                    }

                                    if (result[FirebaseNames.LOSTFOUND_USER] == FirebaseUtility.getUserID()) {
                                        count++
                                    }

                                    fetchedItem++
                                    if (fetchedItem == totalCount) {
                                        numberClaimApproved.value = count
                                        callback.onComplete(true)
                                    }
                                }
                            })
                    }
                }

            }.addOnFailureListener { error ->
                Log.d("FIRESTORE ERROR", error.message.toString())
                callback.onComplete(false)
            }
    }
}