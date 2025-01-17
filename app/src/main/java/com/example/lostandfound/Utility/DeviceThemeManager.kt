package com.example.lostandfound.Utility

import android.content.Context
import android.content.SharedPreferences
import com.example.lostandfound.Data.SharedPreferencesNames.THEME_NAME
import com.example.lostandfound.Data.SharedPreferencesNames.THEME_VALUE

object DeviceThemeManager{

    fun setLightTheme(context: Context){
        val sp = context.getSharedPreferences(THEME_NAME, Context.MODE_PRIVATE)
        sp.edit().putInt(THEME_VALUE, 0).apply()
    }

    fun setDarkTheme(context: Context){
        val sp = context.getSharedPreferences(THEME_NAME, Context.MODE_PRIVATE)
        sp.edit().putInt(THEME_VALUE, 1).apply()
    }

    fun setDeviceTheme(context: Context){
        val sp = context.getSharedPreferences(THEME_NAME, Context.MODE_PRIVATE)
        sp.edit().remove(THEME_VALUE).apply()
    }

}