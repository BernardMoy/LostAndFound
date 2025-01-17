package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsThemeViewModel: ViewModel() {
    val isUseLightSelected: MutableState<Boolean> = mutableStateOf(false)
    val isUseDarkSelected: MutableState<Boolean> = mutableStateOf(false)
    val isUseDeviceThemeSelected: MutableState<Boolean> = mutableStateOf(false)

}