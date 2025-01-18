package com.example.lostandfound.ui.ForgotPassword

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel: ViewModel() {
    val email: MutableState<String> = mutableStateOf("")
    val error: MutableState<String> = mutableStateOf("")

    fun onEmailChanged(s: String){
        email.value = s
    }

    fun validateEmail(): Boolean{
        error.value = ""

        if (email.value.isEmpty()){
            error.value = "Email cannot be empty"
            return false
        }

        return true
    }
}