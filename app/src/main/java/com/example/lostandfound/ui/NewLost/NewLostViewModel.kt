package com.example.lostandfound.ui.NewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.Data.Category
import com.example.lostandfound.Data.Colors
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Data.FirebaseNames

class NewLostViewModel: ViewModel() {

    val itemName: MutableState<String> = mutableStateOf("")
    val itemImage: MutableState<Uri?> = mutableStateOf(null)   // stores the image to be added to storage
    val itemBrand: MutableState<String> = mutableStateOf("")
    val selectedDate: MutableState<Long?> = mutableStateOf(null)     // date, time can be null if not selected
    val isDateDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val selectedHour: MutableState<Int?> = mutableStateOf(null)
    val selectedMinute: MutableState<Int?> = mutableStateOf(null)
    val isTimeDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val additionalDescription: MutableState<String> = mutableStateOf("")

    // initially the selected category is null
    var selectedCategory by mutableStateOf<Category?>(null)

    // the subcategory is also null
    val selectedSubCategory = mutableStateOf("")

    // the selected color is also null
    var selectedColor by mutableStateOf<Colors?>(null)


    // error fields
    val nameError: MutableState<String> = mutableStateOf("")
    val categoryError: MutableState<String> = mutableStateOf("")
    val subCategoryError: MutableState<String> = mutableStateOf("")
    val colorError: MutableState<String> = mutableStateOf("")
    val dateError: MutableState<String> = mutableStateOf("")
    val timeError: MutableState<String> = mutableStateOf("")
    val locationError: MutableState<String> = mutableStateOf("")

    fun onImagePicked(uri: Uri?){
        itemImage.value = uri
    }

    fun onImageCrossClicked(){
        itemImage.value = null
    }

    fun onItemNameChanged(s: String){
        itemName.value = s
    }

    fun onItemBrandChanged(s: String){
        itemBrand.value = s
    }

    fun onDescriptionChanged(d: String){
        additionalDescription.value = d
    }

    fun onCategorySelected(c: Category?){
        selectedCategory = c
    }
    fun onColorSelected(c: Colors?){
        selectedColor = c
    }
    fun isCategorySelected(c: Category): Boolean{
        return selectedCategory == c
    }
    fun isColorSelected(c: Colors): Boolean {
        return selectedColor == c
    }
    fun onSubCategorySelected(s: String){
        selectedSubCategory.value = s
    }

    // function to validate the input fields
    fun validateInput(): Boolean{
        // initially reset all error fields
        nameError.value = ""
        categoryError.value = ""
        subCategoryError.value = ""
        dateError.value = ""
        timeError.value = ""
        locationError.value = ""

        // check each of them
        if (itemName.value.isEmpty()){
            nameError.value = "Item name cannot be empty"
            return false
        }
        if (selectedCategory == null){
            categoryError.value = "Category cannot be empty"
            return false
        }
        if (selectedSubCategory.value.isEmpty()) {
            subCategoryError.value = "Subcategory cannot be empty"
            return false
        }
        if (selectedColor == null){
            colorError.value = "Color cannot be empty"
            return false
        }
        if (selectedDate.value == null){
            dateError.value = "Date cannot be empty"
            return false
        }
        if (selectedHour.value == null || selectedMinute.value == null){
            timeError.value = "Time cannot be empty"
            return false
        }
        if (DateTimeManager.isTimeInFuture(
                // these values must be non-null, otherwise would have been caught in the above methods
                DateTimeManager.getDateTimeEpoch(
                    selectedDate.value!!,
                    selectedHour.value!!, selectedMinute.value!!
                ))){
            timeError.value = "The date and time cannot be in future"
            return false
        }

        // implement location error

        return true
    }

    // when the done button is clicked, add data to db
    fun onDoneButtonClicked(callback: ErrorCallback){
        val data = mapOf(
            FirebaseNames.LOSTFOUND_USER to FirebaseUtility.getUserID(),
            FirebaseNames.LOSTFOUND_ITEMNAME to itemName.value,
            FirebaseNames.LOSTFOUND_CATEGORY to selectedCategory!!.name,      // wont be null, otherwise it is captured above
            FirebaseNames.LOSTFOUND_SUBCATEGORY to selectedSubCategory.value,
            FirebaseNames.LOSTFOUND_COLOR to selectedColor!!.name,   // null is checked
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to DateTimeManager.getDateTimeEpoch(
                selectedDate.value?:0L, selectedHour.value?:0, selectedMinute.value?:0
            ),
            FirebaseNames.LOSTFOUND_BRAND to itemBrand.value,
            FirebaseNames.LOSTFOUND_DESCRIPTION to additionalDescription.value,
            FirebaseNames.LOSTFOUND_STATUS to 0 // represent the lost status
        )

        // add to the firestore db
        val firestoreManager = FirestoreManager()
        val storageManager = FirebaseStorageManager()

        firestoreManager.putWithUniqueId(FirebaseNames.COLLECTION_LOST_ITEMS, data, object: FirestoreManager.Callback<String>{
            override fun onComplete(result: String) {     // the unique id generated is returned
                if (result.isEmpty()){
                    callback.onComplete("Error adding item to database")
                    return
                }

                // add the image to firebase storage if it is not null
                if (itemImage.value != null){
                    storageManager.putImage(FirebaseNames.LOSTFOUND_IMAGE_FOLDER, result, itemImage.value, object: FirebaseStorageManager.Callback<Boolean>{
                        override fun onComplete(resultImage: Boolean) {
                            if (!resultImage){
                                // delete the previously uploaded firestore object if image uploading fails
                                firestoreManager.delete(FirebaseNames.COLLECTION_LOST_ITEMS, result, object: FirestoreManager.Callback<Boolean>{
                                    override fun onComplete(result: Boolean?) {
                                    }
                                })
                                callback.onComplete("Error adding image to database")
                                return
                            }

                            callback.onComplete("")  // exit with no errors
                        }
                    })
                } else {
                    // if image is null, exit activity
                    callback.onComplete("")
                }
            }
        })
    }
}