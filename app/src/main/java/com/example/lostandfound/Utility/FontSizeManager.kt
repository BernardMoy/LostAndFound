package com.example.lostandfound.Utility

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.lostandfound.Data.SharedPreferencesNames

object FontSizeManager {
    val isLargeFontSizeValue: MutableState<Boolean> = mutableStateOf(false)

    fun setFontSize(
        isLargeFont: Boolean,
        context: Context
    ){
        val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_ISLARGEFONT, Context.MODE_PRIVATE)
        sp.edit().putBoolean(SharedPreferencesNames.ISLARGEFONT_VALUE, isLargeFont).apply()
        isLargeFontSizeValue.value = isLargeFont
    }

    fun loadFontSize(
        context: Context
    ){
        // first get the font size from sp, or false if not exist yet
        val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_ISLARGEFONT, Context.MODE_PRIVATE)
        val currentIsLargeFont = sp.getBoolean(SharedPreferencesNames.ISLARGEFONT_VALUE, false)

        // modify the theme value
        isLargeFontSizeValue.value = currentIsLargeFont
    }


    // function to update all child recursively in a viewgroup, used for XML
    fun setFontSizeXML(
        parentView: ViewGroup,
        isLargeFont: Boolean
    ){
        for (i in 0 until parentView.childCount) {
            val child = parentView.getChildAt(i)
            when (child) {
                is TextView -> if (isLargeFont) child.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22F)
                                else child.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                is ViewGroup -> setFontSizeXML(child, isLargeFont)  // recursive call for nested components
            }
        }
    }
}