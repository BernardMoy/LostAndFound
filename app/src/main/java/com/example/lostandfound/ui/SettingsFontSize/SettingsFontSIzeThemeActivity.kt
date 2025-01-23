package com.example.lostandfound.ui.SettingsFontSize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.material.icons.outlined.TextIncrease
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
import com.example.lostandfound.ui.AboutApp.SettingsFontSizeViewModel
import com.example.lostandfound.ui.theme.ComposeTheme


class SettingsFontSizeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsFontSizeScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    SettingsFontSizeScreen(activity = MockActivity())
}

@Composable
fun SettingsFontSizeScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Font size", activity = activity)
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
fun MainContent(viewModel: SettingsFontSizeViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Column (
    ){
        CustomChoiceTextField(
            title = "Normal text",
            leadingIcon = Icons.Outlined.TextFormat,
            state = !viewModel.isLargeFontSize.value,
            onClick = {
                // set is large font size to false
                viewModel.isLargeFontSize.value = false
            }
        )

        CustomChoiceTextField(
            title = "Large text",
            leadingIcon = Icons.Outlined.TextIncrease,
            state = viewModel.isLargeFontSize.value,
            onClick = {
                viewModel.isLargeFontSize.value = true
            }
        )
    }
}


