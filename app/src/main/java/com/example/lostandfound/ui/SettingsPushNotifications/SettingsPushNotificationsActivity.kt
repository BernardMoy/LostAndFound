package com.example.lostandfound.ui.SettingsPushNotifications

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

/*
Setting screen of whether to enable push notifications for items (type 0 to 3) and chat messages
 */
class SettingsPushNotificationsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsPushNotificationsScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    SettingsPushNotificationsScreen(activity = MockActivity())
}

@Composable
fun SettingsPushNotificationsScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Push Notifications", activity = activity)
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
fun MainContent(viewModel: SettingsPushNotificationsViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // initially, load the push notif states
    LaunchedEffect(Unit) {
        viewModel.loadUserSettings(object : PushNotificationSettingsCallback {
            override fun onComplete(success: Boolean) {
                if (!success) {
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // the switches of item notifications and chat notifications
    Column {
        Row(
            modifier = Modifier.padding(
                dimensionResource(R.dimen.content_margin)
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
        ) {
            Icon(
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = "Item notification",
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "Item notifications",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = viewModel.isItemNotificationChecked.value,
                onCheckedChange = {
                    viewModel.isItemNotificationChecked.value =
                        !viewModel.isItemNotificationChecked.value
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.background,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Row(
            modifier = Modifier.padding(
                dimensionResource(R.dimen.content_margin)
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Message,
                contentDescription = "Message notification",
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "Message notifications",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = viewModel.isMessageNotificationChecked.value,
                onCheckedChange = {
                    viewModel.isMessageNotificationChecked.value = it
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.background,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = MaterialTheme.colorScheme.background
                )
            )
        }

        if (viewModel.isLoading.value) {
            CustomProgressBar()
        }
        DoneButton(context, viewModel)
    }
}


@Composable
fun DoneButton(
    context: Context,
    viewModel: SettingsPushNotificationsViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.header_margin)),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomButton(
            text = "Done",
            type = ButtonType.FILLED,
            enabled = !viewModel.isLoading.value,
            onClick = {
                viewModel.isLoading.value = true

                viewModel.updateUserSettings(
                    object : PushNotificationSettingsCallback {
                        override fun onComplete(success: Boolean) {
                            viewModel.isLoading.value = false
                            if (!success) {
                                Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT)
                                    .show()
                                return
                            }
                            Toast.makeText(
                                context,
                                "Settings updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // exit activity
                            (context as Activity?)?.finish()
                        }
                    }
                )
            }
        )
    }
}



