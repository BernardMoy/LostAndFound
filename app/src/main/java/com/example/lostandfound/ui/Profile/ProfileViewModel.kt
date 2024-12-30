package com.example.lostandfound.ui.Profile

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel(){
    val imageUri: MutableState<Uri?> = mutableStateOf(null)
    val email: MutableState<String> = mutableStateOf("E")
    val firstName: MutableState<String> = mutableStateOf("se")
    val lastName: MutableState<String> = mutableStateOf("e")

    fun onImageChanged(uri: Uri?){
        imageUri.value = uri
    }

    fun onEmailChanged(s: String){
        email.value = s
    }

    fun onFirstNameChanged(s: String){
        firstName.value = s
    }

    fun onLastNameChanged(s: String){
        lastName.value = s
    }
}