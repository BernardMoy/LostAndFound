package com.example.lostandfound.Utility

import android.content.Context
import com.example.lostandfound.Data.SharedPreferencesNames

object DeviceThemeManager {
    fun getTheme(
        context: Context
    ): Int{
        val sp = context.getSharedPreferences(SharedPreferencesNames.THEME_NAME, Context.MODE_PRIVATE)
        return sp.getInt(SharedPreferencesNames.THEME_VALUE, 0) // 0 refers to the default value
    }

    fun setTheme(
        themeNum: Int,  // either 0 1 2
        context: Context
    ){
        val sp = context.getSharedPreferences(SharedPreferencesNames.THEME_NAME, Context.MODE_PRIVATE)
        sp.edit().putInt(SharedPreferencesNames.THEME_VALUE, themeNum).apply()
    }
}