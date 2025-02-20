package com.example.lostandfound.ui.EditProfile

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditProfileViewModel : ViewModel() {
    // mutable state data for first name and last names - as they are passed to other jetpack composables
    val firstName: MutableState<String> = mutableStateOf("")
    val lastName: MutableState<String> = mutableStateOf("")
    val imageUri: MutableState<Uri?> = mutableStateOf(null)

    // mutable live data for displaying profile error
    val profileError: MutableLiveData<String> = MutableLiveData("")

    fun setError(s: String) {
        profileError.value = s
    }

    // validate the mutable first names and last names, and return true if validated, false otherwise
    // also sets the profileError accordingly
    fun validateNames(): Boolean {
        if (firstName.value.isEmpty()) {
            setError("First name cannot be empty")
            return false
        }
        if (lastName.value.isEmpty()) {
            setError("Last name cannot be empty")
            return false
        }
        setError("")
        return true
    }


    // functions to change the edit text fields within the edit texts
    fun onFirstNameChanged(s: String) {
        firstName.value = s
    }

    fun onLastNameChanged(s: String) {
        lastName.value = s
    }

    fun onAvatarChanged(uri: Uri?) {
        imageUri.value = uri
    }


}