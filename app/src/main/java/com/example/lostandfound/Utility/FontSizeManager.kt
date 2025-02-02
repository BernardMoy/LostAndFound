package com.example.lostandfound.Utility

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.sp
import com.example.lostandfound.Data.SharedPreferencesNames

object FontSizeManager {
    val isLargeFontSizeValue: MutableState<Boolean> = mutableStateOf(false)

    // set font size through changing the value in shared preferences and also change the compose value
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
        context: Context
    ){
        // first get the font size from sp
        val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_ISLARGEFONT, Context.MODE_PRIVATE)
        val currentIsLargeFont = sp.getBoolean(SharedPreferencesNames.ISLARGEFONT_VALUE, false)

        // if the font size is large, modify its child
        if (currentIsLargeFont){
            for (i in 0 until parentView.childCount) {
                val child = parentView.getChildAt(i)
                when (child) {
                    is TextView -> child.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        child.textSize/context.resources.displayMetrics.scaledDensity*1.38F   // convert the dp from child.textSize to sp
                    )
                    is ViewGroup -> setFontSizeXML(child, context)  // recursive call for nested components
                }
            }
        }
    }
}