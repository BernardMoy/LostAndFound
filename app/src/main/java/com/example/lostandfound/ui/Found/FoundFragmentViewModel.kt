package com.example.lostandfound.ui.Found

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager

interface Callback<T> {
    fun onComplete(result: T)
}

class FoundFragmentViewModel : ViewModel(){
    // whether the screen is loading
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // search word
    val searchWord: MutableState<String> = mutableStateOf("")

    // store the list of data in a map
    val itemData: MutableList<FoundItem> = mutableListOf()

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

                    // initialise the itemData array with dummy variables of found items
                    itemData.addAll(List(resultSize) { FoundItem() })

                    // if no result, return
                    // as the code below would not be executed
                    if (resultSize == 0){
                        callback.onComplete(true)
                        return
                    }

                    // for each retrieved item id, get their data and store that data into the itemData list
                    result.forEachIndexed { index, itemID ->
                        ItemManager.getFoundItemFromId(itemID, object: ItemManager.FoundItemCallback{
                            override fun onComplete(foundItem: FoundItem?) {
                                if (foundItem == null){
                                    callback.onComplete(false)  // fetching data failed
                                    return
                                }

                                // add the data to the list at the specified position
                                itemData[index] = foundItem
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