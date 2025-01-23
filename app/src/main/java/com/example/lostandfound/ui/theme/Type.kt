package com.example.lostandfound.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.lostandfound.R
import com.example.lostandfound.Utility.FontSizeManager

// Set of Material typography styles to start with
val isLargeFont by FontSizeManager.isLargeFontSize

val Typography = Typography(
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = if (isLargeFont) 18.sp else 14.sp  // Smaller than bodyLarge
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = if (isLargeFont) 22.sp else 16.sp, // Smaller than bodyLarge
        lineHeight = if (isLargeFont) 24.sp else 20.sp,
        letterSpacing = if (isLargeFont) 0.4.sp else 0.25.sp
    ),

    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // Medium weight text
        fontSize = if (isLargeFont) 26.sp else 18.sp, // Smaller than titleLarge but still prominent
        lineHeight = if (isLargeFont) 28.sp else 24.sp,
        letterSpacing = 0.sp
    ),
)