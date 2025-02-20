package com.example.lostandfound.ui.SettingsAnimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.Cancel
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
import com.example.lostandfound.Utility.AnimationManager
import com.example.lostandfound.ui.AboutApp.SettingsAnimationViewModel
import com.example.lostandfound.ui.theme.ComposeTheme


class SettingsAnimationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsAnimationScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    SettingsAnimationScreen(activity = MockActivity())
}

@Composable
fun SettingsAnimationScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Animation Settings", activity = activity)
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
fun MainContent(viewModel: SettingsAnimationViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Column {

        CustomChoiceTextField(
            title = "Enable animation",
            leadingIcon = Icons.Outlined.Animation,
            state = viewModel.showAnimation,
            onClick = {
                AnimationManager.setAnimationEnabled(
                    enabled = true,
                    context = context
                )
            }
        )

        CustomChoiceTextField(
            title = "Disable animation",
            leadingIcon = Icons.Outlined.Cancel,
            state = !viewModel.showAnimation,
            onClick = {
                AnimationManager.setAnimationEnabled(
                    enabled = false,
                    context = context
                )
            }
        )
    }
}


