package com.example.lostandfound.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.ui.AboutApp.SettingsThemeViewModel

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
    onBackground = OnBackgroundDark,     // this is the color of most text displayed on screen

    error = Error,
    errorContainer = ErrorLight,
    onErrorContainer = ErrorDark,

    surface = BackgroundColorDark,  // adjust based on light / dark
    onSurface = OnBackgroundDark,
    surfaceVariant = Gray,

    outline = Color.Gray,
    outlineVariant = LightGray

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
    errorContainer = ErrorLight,
    onErrorContainer = ErrorDark,

    surface = BackgroundColorLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = Gray,

    outline = Color.Gray,
    outlineVariant = LightGray
)

// "ComposeTheme" is the theme for colors developed in compose
@Composable
fun ComposeTheme(
    // Dynamic color is available on Android 12+  (Default to false now)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // if shared preferences does not contain value, then ignore
    val context = LocalContext.current
    val sp = context.getSharedPreferences(SharedPreferencesNames.THEME_NAME, Context.MODE_PRIVATE)

    val darkTheme = if (sp.contains(SharedPreferencesNames.THEME_VALUE))
        (sp.getInt(SharedPreferencesNames.THEME_NAME, 0) == 1)
        else isSystemInDarkTheme()  // else use system default

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
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