package com.example.lostandfound.ui.Found

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.R

interface Callback<T> {
    fun onComplete(result: T)
}

class FoundFragmentViewModel : ViewModel(){
    // whether the screen is loading
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // store the list of data in a map
    val itemData: MutableList<Map<String, Any>> = mutableListOf()

    // firestore manager
    val firestoreManager = FirestoreManager()
    val firebaseStorageManager = FirebaseStorageManager()

    /*
    function to get all item data corresponding to the current user, and update the mutable itemData list.
    called everytime the screen is reloaded
    return true if data are successfully retrieved (or no data), false otherwise
     */
    fun getAllData(callback: Callback<Boolean>){
        // first clear the item data array
        itemData.clear()

        // get the current user id
        val userID = FirebaseUtility.getUserID()

        // get all data from firebase with matching user ids
        firestoreManager.getIdsWhere(
            FirebaseNames.COLLECTION_FOUND_ITEMS,  // collection
            FirebaseNames.LOSTFOUND_USER,  // where attribute
            userID,  // attribute value
            FirebaseNames.LOSTFOUND_TIMEPOSTED,  // order by
            object : FirestoreManager.Callback<List<String>> {
                override fun onComplete(result: List<String>?) {
                    // if result is null, fetching data failed
                    if (result == null){
                        callback.onComplete(false)
                        return
                    }

                    // get the total length of result
                    val resultSize = result.size
                    var fetchedItems = 0

                    // if no result, return
                    // as the code below would not be executed
                    if (resultSize == 0){
                        callback.onComplete(true)
                        return
                    }

                    // for each retrieved item id, get their data and store that data into the itemData list
                    result.forEach { itemID ->
                        firestoreManager.get(
                            FirebaseNames.COLLECTION_FOUND_ITEMS,
                            itemID,
                            object : FirestoreManager.Callback<Map<String, Any>> {
                                override fun onComplete(itemResult: Map<String, Any>?) {
                                    // if itemResult is null, fetching data failed
                                    if (itemResult == null){
                                        callback.onComplete(false)
                                        return
                                    }

                                    // add the item id to the itemResult map
                                    val mutableItemResult = itemResult.toMutableMap()
                                    mutableItemResult[FirebaseNames.LOSTFOUND_ID] = itemID

                                    // also add the image
                                    firebaseStorageManager.getImage(FirebaseNames.FOLDER_FOUND_IMAGE, itemID, object: FirebaseStorageManager.Callback<Uri?>{
                                        override fun onComplete(result: Uri?) {
                                            // if the result is null, replace it by placeholder image
                                            if (result == null){
                                                val placeHolder: Uri = Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image)
                                                mutableItemResult[FirebaseNames.LOSTFOUND_IMAGE] = placeHolder

                                            } else {
                                                mutableItemResult[FirebaseNames.LOSTFOUND_IMAGE] = result
                                            }

                                            // add the data to the list
                                            itemData.add(mutableItemResult)
                                            fetchedItems ++

                                            // return true when all items have been fetched
                                            if (fetchedItems == resultSize){
                                                callback.onComplete(true)
                                            }
                                        }
                                    })

                                }
                            }
                        )
                    }
                }
            }
        )
    }
}