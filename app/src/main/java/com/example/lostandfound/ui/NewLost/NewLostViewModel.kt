package com.example.lostandfound.ui.NewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Category
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.LocationManager
import com.google.android.gms.maps.model.LatLng

interface Callback<T> {
    fun onComplete(result: T)
}

class NewLostViewModel : ViewModel() {
    val itemName: MutableState<String> = mutableStateOf("")
    val itemImage: MutableState<Uri?> =
        mutableStateOf(null)   // stores the image to be added to storage
    val itemBrand: MutableState<String> = mutableStateOf("")
    val selectedDate: MutableState<Long?> =
        mutableStateOf(null)     // date, time can be null if not selected
    val isDateDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val selectedHour: MutableState<Int?> = mutableStateOf(null)
    val selectedMinute: MutableState<Int?> = mutableStateOf(null)
    val isTimeDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val selectedLocation: MutableState<LatLng?> = mutableStateOf(null) // default location
    val additionalDescription: MutableState<String> = mutableStateOf("")
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // user info to be retrieved from shared pref using context
    var userAvatar: String = ""
    var userFirstName: String = ""
    var userLastName: String = ""

    // initially the selected category is null
    var selectedCategory by mutableStateOf<Category?>(null)

    // the subcategory is also null
    val selectedSubCategory = mutableStateOf("")


    val selectedColor: SnapshotStateList<String> = mutableStateListOf()

    // the bottom sheet for displaying camera and gallery options
    val isSheetOpen: MutableState<Boolean> = mutableStateOf(false)


    // error fields
    val nameError: MutableState<String> = mutableStateOf("")
    val categoryError: MutableState<String> = mutableStateOf("")
    val subCategoryError: MutableState<String> = mutableStateOf("")
    val colorError: MutableState<String> = mutableStateOf("")
    val dateError: MutableState<String> = mutableStateOf("")
    val timeError: MutableState<String> = mutableStateOf("")

    fun onImagePicked(uri: Uri?) {
        itemImage.value = uri
    }

    fun onImageCrossClicked() {
        itemImage.value = null
    }

    fun onItemNameChanged(s: String) {
        itemName.value = s
    }

    fun onItemBrandChanged(s: String) {
        itemBrand.value = s
    }

    fun onDescriptionChanged(d: String) {
        additionalDescription.value = d
    }

    fun onCategorySelected(c: Category?) {
        selectedCategory = c
    }

    // return true if successful, return false otherwise (When attempting to select more than 3 colors)
    fun onColorSelected(c: String): Boolean {
        if (selectedColor.contains(c)) {
            selectedColor.remove(c)
        } else {
            if (selectedColor.size == 3) {
                return false
            }
            selectedColor.add(c)
        }

        return true
    }

    fun isCategorySelected(c: Category): Boolean {
        return selectedCategory == c
    }

    fun isColorSelected(c: String): Boolean {
        // return true if the color exist in the set
        return selectedColor.contains(c)
    }

    fun onSubCategorySelected(s: String) {
        selectedSubCategory.value = s
    }

    fun onAddLocationClicked() {
        // make the dialog appear
        isLocationDialogShown.value = true
    }

    // function to validate the input fields
    fun validateInput(): Boolean {
        // initially reset all error fields
        nameError.value = ""
        categoryError.value = ""
        subCategoryError.value = ""
        colorError.value = ""
        dateError.value = ""
        timeError.value = ""

        // check each of them
        if (itemName.value.trim().isEmpty()) {
            nameError.value = "Item name cannot be empty"
            return false
        }
        if (selectedCategory == null) {
            categoryError.value = "Category cannot be empty"
            return false
        }
        if (selectedSubCategory.value.trim().isEmpty()) {
            subCategoryError.value = "Subcategory cannot be empty"
            return false
        }
        if (selectedColor.isEmpty()) {
            colorError.value = "Color cannot be empty"
            return false
        }
        if (selectedDate.value == null) {
            dateError.value = "Date cannot be empty"
            return false
        }
        if (selectedHour.value == null || selectedMinute.value == null) {
            timeError.value = "Time cannot be empty"
            return false
        }
        if (DateTimeManager.isTimeInFuture(
                // these values must be non-null, otherwise would have been caught in the above methods
                DateTimeManager.getDateTimeEpoch(
                    selectedDate.value!!,
                    selectedHour.value!!, selectedMinute.value!!
                )
            )
        ) {
            timeError.value = "The date and time cannot be in future"
            return false
        }

        // implement location error

        return true
    }

    // when the done button is clicked, add data to db
    // return the LostItem object generated which is added to the db, or null if failed
    fun onDoneButtonClicked(callback: Callback<LostItem?>) {
        // ensure the current time is consistent
        val currentTime = DateTimeManager.getCurrentEpochTime()

        val data = mapOf(
            FirebaseNames.LOSTFOUND_USER to UserManager.getUserID(),
            FirebaseNames.LOSTFOUND_ITEMNAME to itemName.value,
            FirebaseNames.LOSTFOUND_CATEGORY to selectedCategory!!.name,      // wont be null, otherwise it is captured above
            FirebaseNames.LOSTFOUND_SUBCATEGORY to selectedSubCategory.value,
            FirebaseNames.LOSTFOUND_COLOR to selectedColor.toList().sorted(),
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to DateTimeManager.getDateTimeEpoch(
                selectedDate.value ?: 0L, selectedHour.value ?: 0, selectedMinute.value ?: 0
            ),
            FirebaseNames.LOSTFOUND_BRAND to itemBrand.value,
            FirebaseNames.LOSTFOUND_LOCATION to selectedLocation.value,
            FirebaseNames.LOSTFOUND_DESCRIPTION to additionalDescription.value,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to currentTime,
            FirebaseNames.LOST_IS_TRACKING to false, // default not tracking
            FirebaseNames.USERS_AVATAR to userAvatar,
            FirebaseNames.USERS_FIRSTNAME to userFirstName,
            FirebaseNames.USERS_LASTNAME to userLastName
        )

        // add to the firestore db
        val firestoreManager = FirestoreManager()
        val storageManager = FirebaseStorageManager()

        firestoreManager.putWithUniqueId(
            FirebaseNames.COLLECTION_LOST_ITEMS,
            data,
            object : FirestoreManager.Callback<String> {
                override fun onComplete(result: String) {     // the unique id generated is returned
                    if (result.trim().isEmpty()) {
                        callback.onComplete(null)
                        return
                    }

                    // add the image to firebase storage if it is not null
                    if (itemImage.value != null) {
                        storageManager.putImage(
                            FirebaseNames.FOLDER_LOST_IMAGE,
                            result,
                            itemImage.value,
                            object : FirebaseStorageManager.Callback<String> {
                                override fun onComplete(resultImage: String) {
                                    if (resultImage.isEmpty()) {
                                        // delete the previously uploaded firestore object if image uploading fails
                                        firestoreManager.delete(
                                            FirebaseNames.COLLECTION_LOST_ITEMS,
                                            result,
                                            object : FirestoreManager.Callback<Boolean> {
                                                override fun onComplete(resultError: Boolean?) {
                                                }
                                            })
                                        callback.onComplete(null)  // also return null if image adding failed
                                        return
                                    }

                                    // create lost item here
                                    val generatedLostItem: LostItem = LostItem(
                                        itemID = result,
                                        itemName = itemName.value,
                                        category = selectedCategory!!.name,
                                        subCategory = selectedSubCategory.value,
                                        color = selectedColor.toList().sorted(),
                                        brand = itemBrand.value,
                                        dateTime = DateTimeManager.getDateTimeEpoch(
                                            selectedDate.value ?: 0L,
                                            selectedHour.value ?: 0,
                                            selectedMinute.value ?: 0
                                        ),
                                        location = if (selectedLocation.value != null) LocationManager.latLngToPair(
                                            selectedLocation.value!!
                                        ) else null,
                                        description = additionalDescription.value,
                                        timePosted = currentTime,
                                        image = resultImage,
                                        status = 0,
                                        isTracking = false,
                                        user = User(
                                            userID = UserManager.getUserID(),
                                            avatar = userAvatar,
                                            firstName = userFirstName,
                                            lastName = userLastName
                                        ),
                                    )

                                    // add activity log item
                                    firestoreManager.putWithUniqueId(
                                        FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS,
                                        mapOf(
                                            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 0,
                                            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to itemName.value + " (#" + result + ")",
                                            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to UserManager.getUserID(),
                                            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
                                        ),
                                        object : FirestoreManager.Callback<String> {
                                            override fun onComplete(result: String?) {
                                                // not necessary to throw an error if failed here
                                                callback.onComplete(generatedLostItem)  //return the lost item added
                                            }
                                        }
                                    )
                                }
                            })

                    } else {
                        // create lost item here
                        // only exception is that there are no item images
                        val generatedLostItem: LostItem = LostItem(
                            itemID = result,
                            itemName = itemName.value,
                            category = selectedCategory!!.name,
                            subCategory = selectedSubCategory.value,
                            color = selectedColor.toList().sorted(),
                            brand = itemBrand.value,
                            dateTime = DateTimeManager.getDateTimeEpoch(
                                selectedDate.value ?: 0L,
                                selectedHour.value ?: 0,
                                selectedMinute.value ?: 0
                            ),
                            location = if (selectedLocation.value != null) LocationManager.latLngToPair(
                                selectedLocation.value!!
                            ) else null,
                            description = additionalDescription.value,
                            timePosted = currentTime,
                            image = "",
                            status = 0,
                            isTracking = false,
                            user = User(
                                userID = UserManager.getUserID(),
                                avatar = userAvatar,
                                firstName = userFirstName,
                                lastName = userLastName
                            ),
                        )

                        // add activity log item
                        firestoreManager.putWithUniqueId(
                            FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS,
                            mapOf(
                                FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 0,
                                FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to itemName.value + " (#" + result + ")",
                                FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to UserManager.getUserID(),
                                FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to DateTimeManager.getCurrentEpochTime()
                            ),
                            object : FirestoreManager.Callback<String> {
                                override fun onComplete(result: String?) {
                                    // not necessary to throw an error if failed here
                                    callback.onComplete(generatedLostItem)  //return the lost item added
                                }
                            }
                        )
                    }
                }
            })
    }
}