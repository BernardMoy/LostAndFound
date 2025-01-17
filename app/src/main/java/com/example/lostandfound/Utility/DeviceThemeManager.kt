package com.example.lostandfound.Utility

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.lostandfound.Data.SharedPreferencesNames

object DeviceThemeManager {
    val themeValue = mutableIntStateOf(2)  // to be observed

    fun getTheme(
        context: Context
    ): Int{
        val sp = context.getSharedPreferences(SharedPreferencesNames.THEME_NAME, Context.MODE_PRIVATE)
        return sp.getInt(SharedPreferencesNames.THEME_VALUE, 2) // 2 refers to using device theme
    }

    fun setTheme(
        themeNum: Int,  // either 0 1 2
        context: Context
    ){
        val sp = context.getSharedPreferences(SharedPreferencesNames.THEME_NAME, Context.MODE_PRIVATE)
        sp.edit().putInt(SharedPreferencesNames.THEME_VALUE, themeNum).apply()
        themeValue.intValue = themeNum
    }
}