package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsThemeViewModel: ViewModel() {
    val selectedTheme: MutableState<Int> = mutableIntStateOf(0)


}