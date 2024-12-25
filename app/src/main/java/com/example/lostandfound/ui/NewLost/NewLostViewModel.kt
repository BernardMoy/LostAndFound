package com.example.lostandfound.ui.NewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lostandfound.ErrorCallback
import com.example.lostandfound.Utility.Category
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.FirestoreNames

class NewLostViewModel: ViewModel() {

    val itemName: MutableState<String> = mutableStateOf("")
    val itemImage: MutableState<Uri?> = mutableStateOf(null)
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

    // error fields
    val nameError: MutableState<String> = mutableStateOf("")
    val categoryError: MutableState<String> = mutableStateOf("")
    val subCategoryError: MutableState<String> = mutableStateOf("")
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

    fun onDescriptionChanged(d: String){
        additionalDescription.value = d
    }

    fun onCategorySelected(c: Category?){
        selectedCategory = c
    }
    fun isCategorySelected(c: Category): Boolean{
        return selectedCategory == c
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
        val myMap = mapOf(
            FirestoreNames.LOSTFOUND_ITEMNAME to itemName.value,
            FirestoreNames.LOSTFOUND_CATEGORY to selectedCategory!!.name,      // wont be null, otherwise it is captured above
            FirestoreNames.LOSTFOUND_SUBCATEGORY to selectedSubCategory.value,
            FirestoreNames.LOSTFOUND_EPOCHDATETIME to DateTimeManager.getDateTimeEpoch(
                selectedDate.value?:0L, selectedHour.value?:0, selectedMinute.value?:0
            ),
            FirestoreNames.LOSTFOUND_DESCRIPTION to additionalDescription.value
        )

        // add to the firestore db
    }
}