package com.example.lostandfound.ui.Lost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager

class LostFragmentViewModel : ViewModel(){
    // store the list of data in a map
    val itemData = MutableLiveData<List<Map<String, Any>>>()

    // firestore manager
    val firestoreManager = FirestoreManager()

    /*
    function to get all item data corresponding to the current user, and update the mutable itemData list.
    called everytime the screen is reloaded
    return whether the data are successfully retrieved
     */
    fun getAllData(){
        // first clear the item data array
        itemData.

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
                        return
                    }

                    // for each retrieved item id, get their data and store that data into the itemData list
                    result.forEach { itemID ->
                        firestoreManager.get(
                            FirebaseNames.COLLECTION_LOST_ITEMS,
                            itemID,
                            object : FirestoreManager.Callback<Map<String, Any>> {
                                override fun onComplete(itemResult: Map<String, Any>?) {
                                    // if itemResult is null, fetching data failed
                                    if (itemResult == null){
                                        return
                                    }

                                    // add the data to the list
                                    itemData.add(itemResult)
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}