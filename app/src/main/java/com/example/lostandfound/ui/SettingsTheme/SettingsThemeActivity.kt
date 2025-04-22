package com.example.lostandfound.ui.SettingsTheme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomChoiceTextField
import com.example.lostandfound.Utility.DeviceThemeManager
import com.example.lostandfound.ui.AboutApp.SettingsThemeViewModel
import com.example.lostandfound.ui.theme.ComposeTheme

/*
Setting screen of light theme, dark theme, or device theme
 */
class SettingsThemeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsThemeScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    SettingsThemeScreen(activity = MockActivity())
}

@Composable
fun SettingsThemeScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Select Theme", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // includes the top tab bar and the main content
                    MainContent()
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: SettingsThemeViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Column {

        CustomChoiceTextField(
            title = "Light theme",
            leadingIcon = Icons.Outlined.LightMode,
            state = viewModel.selectedTheme == 0,
            onClick = {
                DeviceThemeManager.setTheme(0, context)

                // change the xml to day mode
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        )

        CustomChoiceTextField(
            title = "Dark theme",
            leadingIcon = Icons.Outlined.DarkMode,
            state = viewModel.selectedTheme == 1,
            onClick = {
                DeviceThemeManager.setTheme(1, context)

                // change the xml to night mode
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }
        )

        CustomChoiceTextField(
            title = "Use device theme",
            leadingIcon = Icons.Outlined.Smartphone,
            state = viewModel.selectedTheme == 2,
            onClick = {
                DeviceThemeManager.setTheme(2, context)

                // change the xml to auto
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
        )
    }
}


