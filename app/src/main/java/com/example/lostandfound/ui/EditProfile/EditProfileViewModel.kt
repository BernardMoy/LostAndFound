package com.example.lostandfound.ui.EditProfile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditProfileViewModel : ViewModel(){
    // mutable live data for first name and last names
    val firstName: MutableState<String> = mutableStateOf("")
    val lastName: MutableState<String> = mutableStateOf("")

    // mutable live data for displaying profile error
    val profileError: MutableLiveData<String> = MutableLiveData("")

    fun setFirstName(s: String){
        firstName.value = s
    }
    fun setLastName(s: String){
        lastName.value = s
    }

    fun setError(s: String){
        profileError.value = s
    }

    // validate the mutable first names and last names, and return true if validated, false otherwise
    // also sets the profileError accordingly
    fun validateNames(): Boolean{
        if (firstName.value.isEmpty()){
            setError("First name cannot be empty")
            return false;
        }
        if (lastName.value.isEmpty()){
            setError("Last name cannot be empty")
            return false;
        }
        setError("")
        return true
    }


    // functions to change the edit text fields within the edit texts
    fun onFirstNameChanged(s: String){
        setFirstName(s)
    }
    fun onLastNameChanged(s: String){
        setLastName(s)
    }


}