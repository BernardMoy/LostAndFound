package com.example.lostandfound.ui.NewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewLostViewModel: ViewModel() {

    val itemName: MutableState<String> = mutableStateOf("")
    val itemImage: MutableState<Uri?> = mutableStateOf(null)
    val selectedDate: MutableState<String> = mutableStateOf("")
    val isDateDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val selectedTime: MutableState<String> = mutableStateOf("")
    val isTimeDialogShown: MutableState<Boolean> = mutableStateOf(false)
    val additionalDescription: MutableState<String> = mutableStateOf("")

    val nameError: MutableLiveData<String> = MutableLiveData("")


    // set functions
    fun setNameError(s: String){
        nameError.value = s
    }

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
}