package com.example.lostandfound.ui.SettingsAutoLoading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomChoiceTextField
import com.example.lostandfound.R
import com.example.lostandfound.Utility.AutoLoadingManager
import com.example.lostandfound.ui.AboutApp.SettingsAutoLoadingViewModel
import com.example.lostandfound.ui.theme.ComposeTheme


class SettingsAutoLoadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsAutoLoadingScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    SettingsAutoLoadingScreen(activity = MockActivity())
}

@Composable
fun SettingsAutoLoadingScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Auto Loading Settings", activity = activity)
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
fun MainContent(viewModel: SettingsAutoLoadingViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Column {

        CustomChoiceTextField(
            title = "Enable auto loading",
            leadingIcon = Icons.Outlined.Refresh,
            state = viewModel.autoLoading,
            onClick = {
                AutoLoadingManager.setAutoLoadingEnabled(
                    enabled = true,
                    context = context
                )
            }
        )

        CustomChoiceTextField(
            title = "Disable auto loading",
            leadingIcon = Icons.Outlined.Cancel,
            state = !viewModel.autoLoading,
            onClick = {
                AutoLoadingManager.setAutoLoadingEnabled(
                    enabled = false,
                    context = context
                )
            }
        )

        Text(
            text = "If auto loading is disabled, items will only be refreshed when the refresh button is clicked. This may improve app performance.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(dimensionResource(R.dimen.header_margin))
        )
    }
}


