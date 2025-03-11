package com.example.lostandfound.ui.PushNotificationsTest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomInputField
import com.example.lostandfound.FirebaseManagers.FirebaseMessagingManager
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme


class PushNotificationsTestActivity : ComponentActivity() {

    val viewModel: PushNotificationsTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PushNotificationsTestScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
// class MockActivity : PushNotificationsTestActivity()
/*
@Preview(showBackground = true)
@Composable
fun Preview() {
    // PushNotificationsTestScreen(activity = this)
}
 */

@Composable
fun PushNotificationsTestScreen(
    activity: PushNotificationsTestActivity,
    viewModel: PushNotificationsTestViewModel
) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Push notifications test", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // includes the top tab bar and the main content
                    MainContent(activity = activity, viewModel = viewModel)
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(
    activity: PushNotificationsTestActivity,   // for calling the permissions functions
    viewModel: PushNotificationsTestViewModel
) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // launcher for getting image from GALLERY
    Column(
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.title_margin)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin_half))
    ) {
        FcmTokenInput(context, viewModel)
        UserIDInput(context, viewModel)
        TitleInput(context, viewModel)
        ContentInput(context, viewModel)
        SendButton(context, viewModel)
    }

}

@Composable
fun FcmTokenInput(
    context: Context,
    viewModel: PushNotificationsTestViewModel
) {
    CustomGrayTitle(text = "FCM token")
    CustomInputField(
        fieldContent = viewModel.fcmToken.value,
        isEditable = true,
        onTextChanged = { t ->
            viewModel.fcmToken.value = t
        },
        placeholder = "",
        leadingIcon = Icons.Outlined.Edit,
    )
}

@Composable
fun UserIDInput(
    context: Context,
    viewModel: PushNotificationsTestViewModel
) {
    CustomGrayTitle(text = "User ID")
    CustomInputField(
        fieldContent = viewModel.userID.value,
        isEditable = true,
        onTextChanged = { t ->
            viewModel.userID.value = t
        },
        placeholder = "",
        leadingIcon = Icons.Outlined.Edit,
    )
}

@Composable
fun TitleInput(
    context: Context,
    viewModel: PushNotificationsTestViewModel
) {
    CustomGrayTitle(text = "Title")
    CustomInputField(
        fieldContent = viewModel.title.value,
        isEditable = true,
        onTextChanged = { t ->
            viewModel.title.value = t
        },
        placeholder = "",
        leadingIcon = Icons.Outlined.Edit,
    )
}

@Composable
fun ContentInput(
    context: Context,
    viewModel: PushNotificationsTestViewModel
) {
    CustomGrayTitle(text = "Content")
    CustomInputField(
        fieldContent = viewModel.content.value,
        isEditable = true,
        onTextChanged = { t ->
            viewModel.content.value = t
        },
        placeholder = "",
        leadingIcon = Icons.Outlined.Edit,
    )
}

@Composable
fun SendButton(
    context: Context,
    viewModel: PushNotificationsTestViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.title_margin)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomButton(
            text = "Send",
            type = ButtonType.FILLED,
            onClick = {
                // send the notification here
                viewModel.sendNotification(context)
            }
        )
    }
}




