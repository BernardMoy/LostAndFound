package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.AnimationManager.animationEnabled

class EmailSenderTestViewModel : ViewModel() {
    val email: MutableState<String> = mutableStateOf("")
}