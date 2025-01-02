package com.example.lostandfound.ui.Profile

import android.content.Context
import android.content.Intent
import android.graphics.Paint.Align
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
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
import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.ui.EditProfile.EditProfileActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class ProfileActivity : ComponentActivity() {

    val viewModel:ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProfileScreen(activity = this, viewModel = viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        updateData(
            context = this,
            viewModel = viewModel
        )
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ProfileScreen(activity = MockActivity(), viewModel = ProfileViewModel())
}

@Composable
fun ProfileScreen(activity: ComponentActivity, viewModel: ProfileViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbarColored(
                        title = "Profile",
                        activity = activity
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
                    MainContent(viewModel = viewModel)
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: ProfileViewModel) {

    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    TopProfileBox(context = context, viewModel = viewModel)
    Column(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.title_margin))
    ){
        AccountDetails(viewModel = viewModel)
        Actions(viewModel = viewModel)
    }

    // the dialog for logging out
    CustomTextDialog(
        icon = Icons.AutoMirrored.Outlined.Logout,
        title = "Logout",
        content = "Are you sure you want to logout?",
        confirmButton = {
            CustomButton(
                text = "Logout",
                type = ButtonType.FILLED,
                onClick = {
                    val manager: FirebaseAuthManager = FirebaseAuthManager(context)
                    manager.logoutUser()
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    viewModel.isLogoutDialogShown.value = false
                    (context as ProfileActivity).finish()
                }
            )
        },
        dismissButton = {
            CustomButton(text = "Cancel",
                type = ButtonType.OUTLINED,
                onClick = {
                    // dismiss the dialog
                    viewModel.isLogoutDialogShown.value = false
                }
            )
        },
        isDialogShown = viewModel.isLogoutDialogShown
    )
}

@Composable
fun TopProfileBox(
    context: Context,
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
                style = Typography.bodyMedium,
                fontSize = 24.sp  // force large font here
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
                    // start edit profile activity
                    val intent = Intent(context, EditProfileActivity::class.java)
                    context.startActivity(intent)
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
            onClick = {
                // show dialog
                viewModel.isLogoutDialogShown.value = true
            },
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
            leftIcon = Icons.Outlined.ErrorOutline,
            tint = Color.Red
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

// function to update the fields (names, emails, avatar) based on shared preference
fun updateData(
    context: Context,  // used to accessing shared pref
    viewModel: ProfileViewModel
){
    val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)

    // set the data from shared pref
    viewModel.onFirstNameChanged(sp.getString(SharedPreferencesNames.USER_FIRSTNAME, "") ?: "")
    viewModel.onLastNameChanged(sp.getString(SharedPreferencesNames.USER_LASTNAME, "") ?: "")
    viewModel.onEmailChanged(sp.getString(SharedPreferencesNames.USER_EMAIL, "") ?: "")
    viewModel.onImageChanged(
        ImageManager.stringToUri(
            context = context,
            string = sp.getString(SharedPreferencesNames.USER_AVATAR, "") ?: ""
        )
    )
}




