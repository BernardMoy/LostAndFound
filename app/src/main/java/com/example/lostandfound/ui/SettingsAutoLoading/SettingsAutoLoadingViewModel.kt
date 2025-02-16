package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.AnimationManager.animationEnabled
import com.example.lostandfound.Utility.AutoLoadingManager

class SettingsAutoLoadingViewModel: ViewModel() {
    val autoLoading by AutoLoadingManager.autoLoadingEnabled

}