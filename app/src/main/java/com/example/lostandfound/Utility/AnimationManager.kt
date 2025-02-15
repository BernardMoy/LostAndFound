package com.example.lostandfound.Utility

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.lostandfound.Data.SharedPreferencesNames

object AnimationManager {
    // observe this variable to determine if animations should be shown
    val animationEnabled = mutableStateOf(true)

    fun setAnimationEnabled(
        enabled: Boolean,
        context: Context
    ) {
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_ANIMATION,
            Context.MODE_PRIVATE
        )
        sp.edit().putBoolean(SharedPreferencesNames.ANIMATION_VALUE, enabled).apply()
        animationEnabled.value = enabled
    }

    // load animation to be called on create in the main activity
    fun loadAnimationEnabled(
        context: Context
    ) {
        // first get the settings from sp or true if not exist yet
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_ANIMATION,
            Context.MODE_PRIVATE
        )
        val currentAnimationEnabled = sp.getBoolean(SharedPreferencesNames.ANIMATION_VALUE, true)

        // modify the animation value
        animationEnabled.value = currentAnimationEnabled
    }
}
