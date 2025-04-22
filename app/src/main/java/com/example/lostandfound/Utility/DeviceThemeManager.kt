package com.example.lostandfound.Utility

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableIntStateOf
import com.example.lostandfound.Data.SharedPreferencesNames

/*
Methods to interact with the device's local database
to control theme (light, dark, device theme)
 */
object DeviceThemeManager {
    val themeValue = mutableIntStateOf(2)  // to be observed

    fun setTheme(
        themeNum: Int,  // either 0 1 2
        context: Context
    ) {
        val sp =
            context.getSharedPreferences(SharedPreferencesNames.NAME_THEME, Context.MODE_PRIVATE)
        sp.edit().putInt(SharedPreferencesNames.THEME_VALUE, themeNum).apply()
        themeValue.intValue = themeNum

        // modify the XML themes
        setXMLTheme(themeNum)
    }

    // load theme to be called on create in the main activity
    fun loadTheme(
        context: Context
    ) {
        // first get the theme from sp, or =2 if not exist yet
        val sp =
            context.getSharedPreferences(SharedPreferencesNames.NAME_THEME, Context.MODE_PRIVATE)
        val currentThemeNum =
            sp.getInt(SharedPreferencesNames.THEME_VALUE, 2) // 2 refers to using device theme

        // modify the theme value
        themeValue.intValue = currentThemeNum

        // modify the XML themes
        setXMLTheme(currentThemeNum)
    }

    private fun setXMLTheme(
        themeNum: Int
    ) {
        // modify the XML themes
        when (themeNum) {
            0 ->
                // change the xml to day mode
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )

            1 ->
                // change the xml to day mode
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )

            2 ->
                // change the xml to day mode
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
        }
    }
}