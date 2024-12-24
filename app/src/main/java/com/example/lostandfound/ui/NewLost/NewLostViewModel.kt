package com.example.lostandfound.ui.NewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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


    val nameError: MutableLiveData<String> = MutableLiveData("")


    // function to validate the input fields
    fun validateInput(){

    }

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

    fun onCategorySelected(c: Category){
        selectedCategory = c
    }
    fun isCategorySelected(c: Category): Boolean{
        return selectedCategory == c
    }
}