package com.example.lostandfound.ui.SettingsTheme

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomChoiceTextField
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.Utility.DeviceThemeManager
import com.example.lostandfound.ui.AboutApp.SettingsThemeViewModel
import com.example.lostandfound.ui.theme.ComposeTheme


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

    Column (

    ){

        CustomChoiceTextField(
            title = "Light theme",
            leadingIcon = Icons.Outlined.LightMode,
            state = viewModel.selectedTheme.value == 0,
            onClick = {
                viewModel.selectedTheme.value = 0
                DeviceThemeManager.setTheme(0, context)
            }
        )

        CustomChoiceTextField(
            title = "Dark theme",
            leadingIcon = Icons.Outlined.DarkMode,
            state = viewModel.selectedTheme.value == 1,
            onClick = {
                viewModel.selectedTheme.value = 1
                DeviceThemeManager.setTheme(1, context)
            }
        )

        CustomChoiceTextField(
            title = "Use device theme",
            leadingIcon = Icons.Outlined.Smartphone,
            state = viewModel.selectedTheme.value == 2,
            onClick = {
                viewModel.selectedTheme.value = 2
                DeviceThemeManager.setTheme(2, context)
            }
        )

    }
}

// load status from shared preferences
fun loadStatus(
    context: Context,
    viewModel: SettingsThemeViewModel
){
    val savedTheme = DeviceThemeManager.getTheme(context)

    // set it in the view model
    viewModel.selectedTheme.value = savedTheme
}


