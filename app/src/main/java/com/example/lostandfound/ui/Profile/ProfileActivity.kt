package com.example.lostandfound.ui.Profile

import android.content.Context
import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.BackToolbarColored
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionRow
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProfileScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ProfileScreen(activity = MockActivity())
}

@Composable
fun ProfileScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            val isDialogShown = remember { mutableStateOf(false) }

            // open the dialog when the back button on device is pressed
            BackHandler {
                isDialogShown.value = true
            }

            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbarColored(
                        title = "Profile",
                        activity = activity,
                        backButtonOnClick = {
                            // show the dialog
                            isDialogShown.value = true
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .verticalScroll(rememberScrollState())   // make screen scrollable
                ) {
                    // content goes here
                    MainContent()
                }
            }

            // the dialog when the back button is pressed
            CustomTextDialog(
                icon = Icons.Outlined.Cancel,
                title = "Discard changes?",
                content = "All your changes will be lost.",
                confirmButton = {
                    CustomButton(
                        text = "Discard",
                        type = ButtonType.FILLED,
                        onClick = {
                            // dismiss the dialog
                            isDialogShown.value = false
                            // exit the activity
                            activity.finish()
                        })
                },
                dismissButton = {
                    CustomButton(text = "Cancel",
                        type = ButtonType.OUTLINED,
                        onClick = {
                            // dismiss the dialog
                            isDialogShown.value = false
                        })
                },
                isDialogShown = isDialogShown    // dialog is shown only when the value of isDialogShown is true
            )
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: ProfileViewModel = viewModel()) {

    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // get the first and last name from shared preferences
    val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)
    
    TopProfileBox(viewModel = viewModel)
    Column(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.title_margin))
    ){
        AccountDetails(viewModel = viewModel)
        Actions(viewModel = viewModel)
    }
}

@Composable
fun TopProfileBox(
    viewModel: ProfileViewModel
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                // gradient goes here
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                ),
            )
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.title_margin)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ){
            // profile icon
            Image(
                painter = if (viewModel.imageUri.value != null) {
                    // if imageuri is not null, display it as an asyncimagepainter
                    rememberAsyncImagePainter(model = viewModel.imageUri.value)
                } else {
                    // else, display the default profile icon
                    painterResource(id = R.drawable.profile_icon)
                },

                contentDescription = "Your avatar",
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.profile_image_size_large))
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            // displayed name
            Text(
                text = viewModel.firstName.value + " " + viewModel.lastName.value,
                color = MaterialTheme.colorScheme.onPrimary,
                style = Typography.bodyLarge
            )

            // email
            Text(
                text = viewModel.email.value,
                color = MaterialTheme.colorScheme.onPrimary,
                style = Typography.bodyMedium
            )

            // edit profile button
            CustomButton(
                text = "Edit profile",
                type = ButtonType.WHITE,
                onClick = {

                }
            )
        }
    }
}

@Composable
fun AccountDetails(
    viewModel: ProfileViewModel
){
    Column(
    ){
        CustomGrayTitle(text = "Account details")

        // first name field
        CustomEditText(fieldLabel = "First name",
            fieldContent = viewModel.firstName.value,
            leftIcon = Icons.Outlined.AccountCircle,
            isEditable = false,
            onTextChanged = {viewModel.onFirstNameChanged(it)},
        )

        HorizontalDivider(thickness = 1.dp)

        // last name field
        CustomEditText(fieldLabel = "Last name",
            fieldContent = viewModel.lastName.value,
            leftIcon = Icons.Outlined.AccountCircle,
            isEditable = false,
            onTextChanged = {viewModel.onLastNameChanged(it)}
        )

        HorizontalDivider(thickness = 1.dp)
    }
}


@Composable
fun Actions(
    viewModel: ProfileViewModel
){
    Column(
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.content_margin_half)
        )
    ){
        CustomGrayTitle(text = "Actions")

        // log out button
        CustomActionRow(
            text = "Logout",
            onClick = { /*TODO*/ },
            leftIcon = Icons.AutoMirrored.Outlined.Logout
        )

        HorizontalDivider(thickness = 1.dp)

        // change password
        CustomActionRow(
            text = "Change password",
            onClick = { /*TODO*/ },
            leftIcon = Icons.Outlined.Lock
        )

        HorizontalDivider(thickness = 1.dp)

        // disable account
        CustomActionRow(
            text = "Disable account",
            onClick = { /*TODO*/ },
            leftIcon = Icons.Outlined.Lock
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

// function to update the fields (names, emails, avatar) based on shared preference
fun UpdateData(
    context: Context,  // used to accessing shared pref
    viewModel: ProfileViewModel
){

}




