package com.example.lostandfound.Utility

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.lostandfound.Data.SharedPreferencesNames

object FontSizeManager {
    val isLargeFontSize: MutableState<Boolean> = mutableStateOf(false)

    fun setTheme(
        isLargeFont: Boolean,
        context: Context
    ){
        val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_ISLARGEFONT, Context.MODE_PRIVATE)
        sp.edit().putBoolean(SharedPreferencesNames.ISLARGEFONT_VALUE, isLargeFont).apply()
        isLargeFontSize.value = isLargeFont
    }

    fun loadTheme(
        context: Context
    ){
        // first get the theme from sp, or false if not exist yet
        val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_ISLARGEFONT, Context.MODE_PRIVATE)
        val currentIsLargeFont = sp.getBoolean(SharedPreferencesNames.ISLARGEFONT_VALUE, false)

        // modify the theme value
        isLargeFontSize.value = currentIsLargeFont
    }
}