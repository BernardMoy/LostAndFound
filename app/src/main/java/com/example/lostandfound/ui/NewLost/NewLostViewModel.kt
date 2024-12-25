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

class NewLostViewModel: ViewModel() {

    val itemName: MutableState<String> = mutableStateOf("")
    val itemImage: MutableState<Uri?> = mutableStateOf(null)
    val selectedDate: MutableState<String> = mutableStateOf("")
    val isDateDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val selectedTime: MutableState<String> = mutableStateOf("")
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
    val dateTimeError: MutableState<String> = mutableStateOf("")
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
        dateTimeError.value = ""
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
        if (selectedDate.value.isEmpty()){
            dateTimeError.value = "Date cannot be empty"
            return false
        }
        if (selectedTime.value.isEmpty()){
            dateTimeError.value = "Time cannot be empty"
            return false
        }

        // implement location error

        return true
    }

    // when the done button is clicked
    fun onDoneButtonClicked(callback: ErrorCallback){

    }
}