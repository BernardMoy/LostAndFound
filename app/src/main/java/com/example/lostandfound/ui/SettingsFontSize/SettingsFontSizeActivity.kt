package com.example.lostandfound.ui.SettingsFontSize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material3.MaterialTheme
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
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomChoiceTextField
import com.example.lostandfound.R
import com.example.lostandfound.Utility.FontSizeManager
import com.example.lostandfound.ui.theme.ComposeTheme

/*
Setting screen of choosing regular or large font size
 */
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
fun MainContent(viewModel: SettingsFontSizeViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Column {
        CustomChoiceTextField(
            title = "Normal text",
            leadingIcon = Icons.Outlined.TextFormat,
            state = !viewModel.isLargeFontSize,
            onClick = {
                // set is large font size to false
                FontSizeManager.setFontSize(false, context)
            }
        )

        CustomChoiceTextField(
            title = "Large text",
            leadingIcon = Icons.Outlined.TextIncrease,
            state = viewModel.isLargeFontSize,
            onClick = {
                FontSizeManager.setFontSize(true, context)
            }
        )

        // reminder to reload for changes to take effect
        ReminderMessage(context = context, viewModel = viewModel)

        RestartButton(context = context, viewModel = viewModel)
    }
}


@Composable
fun ReminderMessage(
    context: Context,
    viewModel: SettingsFontSizeViewModel
) {
    Text(
        text = "Please restart the app for the changes to take effect.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                dimensionResource(id = R.dimen.header_margin),
            ),
        textAlign = TextAlign.Center   // center text
    )
}


@Composable
fun RestartButton(
    context: Context,
    viewModel: SettingsFontSizeViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomButton(
            text = "Restart now",
            type = ButtonType.TONAL,
            onClick = {
                val packageManager = context.packageManager
                val intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                val componentName = intent.component!!
                val restartIntent = Intent.makeRestartActivityTask(componentName)
                context.startActivity(restartIntent)

                // exit current app
                Runtime.getRuntime().exit(0)
            }
        )
    }
}



