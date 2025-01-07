package com.example.lostandfound.ui.Found

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.LocationManager

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


                                    // get the image of the item
                                    firebaseStorageManager.getImage(FirebaseNames.FOLDER_FOUND_IMAGE, itemID, object: FirebaseStorageManager.Callback<Uri?>{
                                        override fun onComplete(result: Uri?) {
                                            // initialise the item image to the default image
                                            var itemImage: Uri = Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image)

                                            // if the result is not null, replace it by actual item image
                                            if (result != null) {
                                                itemImage = result
                                            }

                                            // create found item class object
                                            val thisFoundItem = FoundItem(
                                                itemID = itemID,
                                                userID = userID,
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
                                                image = itemImage.toString(),  // uri to string
                                                securityQuestion = itemResult[FirebaseNames.FOUND_SECURITY_Q] as String,
                                                securityQuestionAns = itemResult[FirebaseNames.FOUND_SECURITY_Q_ANS] as String
                                            )

                                            // add the data to the list
                                            itemData.add(thisFoundItem)
                                            fetchedItems ++

                                            // return true when all items have been fetched
                                            if (fetchedItems == resultSize){
                                                // sort the data
                                                itemData.sortByDescending { key ->
                                                    key.timePosted
                                                }
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