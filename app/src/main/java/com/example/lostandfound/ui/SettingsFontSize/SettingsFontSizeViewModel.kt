package com.example.lostandfound.ui.SettingsFontSize

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.FontSizeManager

class SettingsFontSizeViewModel : ViewModel() {
    val isLargeFontSize by FontSizeManager.isLargeFontSizeValue
}