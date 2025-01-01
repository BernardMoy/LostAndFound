package com.example.lostandfound.ui.Lost

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager

interface Callback<T> {
    fun onComplete(result: T)
}

class LostFragmentViewModel : ViewModel(){
    // whether the screen is loading
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    // store the list of data in a map
    val itemData: MutableList<Map<String, Any>> = mutableListOf()

    // firestore manager
    val firestoreManager = FirestoreManager()

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
            FirebaseNames.COLLECTION_LOST_ITEMS,
            FirebaseNames.LOSTFOUND_USER,
            userID,
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

                    Log.d("RESULTSIZE", resultSize.toString())

                    // for each retrieved item id, get their data and store that data into the itemData list
                    result.forEach { itemID ->
                        firestoreManager.get(
                            FirebaseNames.COLLECTION_LOST_ITEMS,
                            itemID,
                            object : FirestoreManager.Callback<Map<String, Any>> {
                                override fun onComplete(itemResult: Map<String, Any>?) {
                                    // if itemResult is null, fetching data failed
                                    if (itemResult == null){
                                        callback.onComplete(false)
                                        return
                                    }

                                    // add the data to the list
                                    itemData.add(itemResult)
                                    fetchedItems ++

                                    // return true when all items have been fetched
                                    if (fetchedItems == resultSize){
                                        callback.onComplete(true)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}