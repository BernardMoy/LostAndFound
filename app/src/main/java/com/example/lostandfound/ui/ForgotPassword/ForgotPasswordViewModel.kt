package com.example.lostandfound.ui.ForgotPassword

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel : ViewModel() {
    val email: MutableState<String> = mutableStateOf("")
    val error: MutableState<String> = mutableStateOf("")

    fun onEmailChanged(s: String) {
        email.value = s
    }

    fun validateEmail(): Boolean {
        error.value = ""

        if (email.value.trim().isEmpty()) {
            error.value = "Email cannot be empty"
            return false

        } else if (!email.value.contains("@") || !email.value.endsWith("warwick.ac.uk")) {
            error.value = "DevData must be university email (@warwick.ac.uk)"
            return false
        }

        return true
    }
}