package com.example.lostandfound.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryColorLight,
    onPrimaryContainer = PrimaryColorVariant,   // the darker primary color
    onPrimary = OnPrimary,

    secondary = SecondaryColor,
    secondaryContainer = SecondaryColorLight,
    onSecondaryContainer = SecondaryColorVariant,
    onSecondary = OnSecondary,

    background = BackgroundColorDark,   // depending on the light / dark theme
    onBackground = OnBackgroundDark,

    error = Error,
    surface = LightGray,     // adjust base on light / dark
    onSurface = DarkGray,
    surfaceVariant = Gray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryColorLight,
    onPrimaryContainer = PrimaryColorVariant,   // the darker primary color
    onPrimary = OnPrimary,

    secondary = SecondaryColor,
    secondaryContainer = SecondaryColorLight,
    onSecondaryContainer = SecondaryColorVariant,
    onSecondary = OnSecondary,

    background = BackgroundColorLight,   // depending on the light / dark theme
    onBackground = OnBackgroundLight,

    error = Error,
    surface = DarkGray,      // adjust base on light / dark
    onSurface = White,
    surfaceVariant = Gray
)

// "ComposeTheme" is the theme for colors developed in compose
@Composable
fun ComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+  (Default to false now)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}