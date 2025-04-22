package com.example.lostandfound.Utility

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.lostandfound.Data.SharedPreferencesNames

/*
Methods to interact with the device's local database
to control whether auto loading is enabled
 */
object AutoLoadingManager {
    // observe this variable to determine if auto loadings should be performed
    val autoLoadingEnabled = mutableStateOf(true)

    fun setAutoLoadingEnabled(
        enabled: Boolean,
        context: Context
    ) {
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_AUTO_LOADING,
            Context.MODE_PRIVATE
        )
        sp.edit().putBoolean(SharedPreferencesNames.AUTO_LOADING_VALUE, enabled).apply()
        autoLoadingEnabled.value = enabled
    }

    // load animation to be called on create in the main activity
    fun loadAutoLoadingEnabled(
        context: Context
    ) {
        // first get the settings from sp or true if not exist yet
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_AUTO_LOADING,
            Context.MODE_PRIVATE
        )
        val currentAutoLoadingEnabled =
            sp.getBoolean(SharedPreferencesNames.AUTO_LOADING_VALUE, true)

        // modify the animation value
        autoLoadingEnabled.value = currentAutoLoadingEnabled
    }
}
