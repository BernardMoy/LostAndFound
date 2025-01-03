package com.example.lostandfound.ui.ViewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.R
import com.google.android.gms.maps.model.LatLng

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewLostViewModel : ViewModel(){
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // item data are stored here
    var itemID: String = ""  // retrieved as soon as the activity is created

    var userID: String = ""
    var itemName: String = ""
    var category: String = ""
    var subCategory: String = ""
    var color: String = ""
    var dateTime: Long = 0L
    var brand: String = ""  // nullable
    var description: String = ""  // nullable
    var status: Int = 0
    var userName: String = ""
    var timePosted: Long = 0L
    var location: LatLng = LatLng(52.37930763817003,-1.5614912710215834)

    // image stored here
    var image: Uri? = null

    // function to get item data from firebase. Called only after itemID has been loaded from intent
    // return true if successful, false otherwise
    fun getItemData(callback: Callback<Boolean>){

        // managers
        val firestoreManager = FirestoreManager()
        val firebaseStorageManager = FirebaseStorageManager()

        // get data from firebase db
        firestoreManager.get(FirebaseNames.COLLECTION_LOST_ITEMS, itemID, object : FirestoreManager.Callback<Map<String, Any>> {
            override fun onComplete(result: Map<String, Any>?) {
                if (result == null){
                    // failed
                    callback.onComplete(false)
                    return
                }

                // load the data into the view model
                itemName = result[FirebaseNames.LOSTFOUND_ITEMNAME].toString()
                category = result[FirebaseNames.LOSTFOUND_CATEGORY].toString()
                subCategory = result[FirebaseNames.LOSTFOUND_SUBCATEGORY].toString()
                color = result[FirebaseNames.LOSTFOUND_COLOR].toString()
                dateTime = result[FirebaseNames.LOSTFOUND_EPOCHDATETIME] as Long
                brand = result[FirebaseNames.LOSTFOUND_BRAND].toString()
                description = result[FirebaseNames.LOSTFOUND_DESCRIPTION].toString()
                status = (result[FirebaseNames.LOSTFOUND_STATUS] as Long).toInt()

                // user data
                userID = result[FirebaseNames.LOSTFOUND_USER].toString()
                timePosted = result[FirebaseNames.LOSTFOUND_TIMEPOSTED] as Long

                // location data: Reconstruct the latlng object
                location = LatLng(
                    (result[FirebaseNames.LOSTFOUND_LOCATION] as HashMap<*, *>)["latitude"] as Double,
                    (result[FirebaseNames.LOSTFOUND_LOCATION] as HashMap<*, *>)["longitude"] as Double
                )



                // get image of the item from firebase storage
                firebaseStorageManager.getImage(FirebaseNames.FOLDER_LOST_IMAGE, itemID, object: FirebaseStorageManager.Callback<Uri?>{
                    override fun onComplete(result: Uri?) {
                        // if the result is null, replace it by placeholder image
                        if (result == null){
                            val placeHolder: Uri = Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image)
                            image = placeHolder

                        } else {
                            image = result
                        }


                        // get name of the user from firebase firestore
                        firestoreManager.get(FirebaseNames.COLLECTION_USERS, userID, object : FirestoreManager.Callback<Map<String, Any>>{
                            override fun onComplete(result: Map<String, Any>?) {
                                if (result == null) {
                                    userName = "Unknown"

                                } else {
                                    val name = result[FirebaseNames.USERS_FIRSTNAME].toString() + " " + result[FirebaseNames.USERS_LASTNAME].toString()

                                    // set username
                                    userName = name
                                }

                                // return true after all data has been fetched
                                callback.onComplete(true)
                            }
                        })
                    }
                })
            }
        })
    }
}