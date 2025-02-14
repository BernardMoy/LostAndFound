package com.example.lostandfound.ui.Lost

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager

interface Callback<T> {
    fun onComplete(result: T)
}

class LostFragmentViewModel : ViewModel(){
    // whether the screen is loading
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // search word
    val searchWord: MutableState<String> = mutableStateOf("")

    // store the list of data in a map
    val itemData: MutableList<LostItem> = mutableListOf()

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
            FirebaseNames.COLLECTION_LOST_ITEMS,  // collection
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

                    // initialise the itemData array with dummy variables of lost items
                    itemData.addAll(List(resultSize) { LostItem() })

                    // if no result, return
                    // as the code below would not be executed
                    if (resultSize == 0){
                        callback.onComplete(true)
                        return
                    }

                    // for each retrieved item id, get their data and store that data into the itemData list
                    result.forEachIndexed { index, itemID ->
                        ItemManager.getLostItemFromId(itemID, object: ItemManager.LostItemCallback{
                            override fun onComplete(lostItem: LostItem?) {
                                if (lostItem == null){
                                    callback.onComplete(false)  // fetching data failed
                                    return
                                }

                                // add the data to the list at the correct position
                                itemData[index] = lostItem
                                fetchedItems ++

                                // return true when all items have been fetched
                                if (fetchedItems == resultSize){
                                    callback.onComplete(true)
                                }
                            }
                        })
                    }
                }
            }
        )
    }
}