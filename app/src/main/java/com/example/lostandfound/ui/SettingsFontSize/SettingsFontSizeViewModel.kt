package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.DeviceThemeManager.themeValue
import com.example.lostandfound.Utility.FontSizeManager
import com.example.lostandfound.ui.theme.isLargeFont

class SettingsFontSizeViewModel: ViewModel() {
    val isLargeFontSize by FontSizeManager.isLargeFontSizeValue
}