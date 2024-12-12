package com.example.lostandfound

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

data class CustomTestStyles (
    val blackContent: TextStyle,
    val toolbarTitle: TextStyle
)

/*
// define custom text styles here
@Composable
fun getTextStyles() : CustomTestStyles{
    val blackContent = TextStyle(
        fontSize = dimensionResource(id = R.dimen.content_font_size).value.sp,
        color = Color.Black
    )

    val toolbarTitle = TextStyle(

    )

    // return all custom text styles when called
    return
}

 */