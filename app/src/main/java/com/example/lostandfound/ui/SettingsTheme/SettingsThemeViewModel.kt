package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.DeviceThemeManager.themeValue

class SettingsThemeViewModel : ViewModel() {
    val selectedTheme by themeValue

}