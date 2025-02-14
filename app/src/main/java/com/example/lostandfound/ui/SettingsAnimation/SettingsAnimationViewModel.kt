package com.example.lostandfound.ui.AboutApp

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Utility.AnimationManager.animationEnabled

class SettingsAnimationViewModel: ViewModel() {
    val showAnimation by animationEnabled

}