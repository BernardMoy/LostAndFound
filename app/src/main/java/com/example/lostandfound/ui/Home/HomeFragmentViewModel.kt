package com.example.lostandfound.ui.Home

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseManagers.ItemManager.StatusCallback
import com.example.lostandfound.FirebaseManagers.ItemManager.getLostItemStatus
import com.example.lostandfound.R
import com.example.lostandfound.Utility.LocationManager
import com.google.android.gms.common.internal.Objects
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

interface Callback<T>{
    fun onComplete(result: T)
}

class HomeFragmentViewModel : ViewModel(){

    val isLoggedIn: MutableState<Boolean> = mutableStateOf(FirebaseUtility.isUserLoggedIn())

    // for displaying the small lost item
    var latestLostItem: LostItem? = null   // if this is null, then the user has no recently lost items

    // load data into latestLostItem and latestLostItemMatches
    fun loadLatestLostItem(
        callback: Callback<Boolean>
    ){
        // if not logged in, reset it
        if (!FirebaseUtility.isUserLoggedIn()){
            latestLostItem = null
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
                if (result.isEmpty){
                    // set the latest item to null
                    latestLostItem = null
                    callback.onComplete(true)

                } else {
                    // get the lost item id
                    val lostItemID = result.documents[0].id

                    // get the lost item
                    ItemManager.getLostItemFromId(lostItemID, object: ItemManager.LostItemCallback{
                        override fun onComplete(lostItem: LostItem?) {
                            if (lostItem == null){
                                callback.onComplete(false)
                                return
                            }

                            // set data
                            latestLostItem = lostItem
                            callback.onComplete(true)
                        }
                    })
                }
            }.addOnFailureListener { error ->
                Log.d("HOME PAGE LOST ITEM LOAD ERROR", error.message.toString())
                callback.onComplete(false)
            }
    }
}