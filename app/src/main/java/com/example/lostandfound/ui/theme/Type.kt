package com.example.lostandfound.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.lostandfound.R

// Set of Material typography styles to start with
val Typography = Typography(

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // Smaller than bodyLarge
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // Medium weight text
        fontSize = 18.sp, // Smaller than titleLarge but still prominent
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
)