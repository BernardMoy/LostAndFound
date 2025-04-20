package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class EmailSenderTestViewModel : ViewModel() {
    val email: MutableState<String> = mutableStateOf("")
}