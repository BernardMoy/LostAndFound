package com.example.lostandfound.ui.EditProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditProfileViewModel : ViewModel(){
    // mutable live data for first name and last names

    // mutable live data for displaying profile error
    val profileError: MutableLiveData<String> = MutableLiveData("")

    fun setError(s: String){
        profileError.value = s
    }

    // validate the mutable first names and last names, and return true if validated, false otherwise
    // also sets the profileError accordingly
    fun validateNames(firstName: String, lastName: String): Boolean{
        if (firstName.isEmpty()){
            setError("First name cannot be empty")
            return false;
        }
        if (lastName.isEmpty()){
            setError("Last name cannot be empty")
            return false;
        }
        setError("")
        return true
    }

}