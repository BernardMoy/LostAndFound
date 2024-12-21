package com.example.lostandfound.ui.NewLost

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewLostViewModel: ViewModel() {

    val nameError: MutableLiveData<String> = MutableLiveData("")


    // set functions
    fun setNameError(s: String){
        nameError.value = s
    }

    // function to validate the input fields
    fun validateInput(){

    }
}