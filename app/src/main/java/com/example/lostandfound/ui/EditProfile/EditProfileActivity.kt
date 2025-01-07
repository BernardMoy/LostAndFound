package com.example.lostandfound.ui.EditProfile

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import com.example.lostandfound.R
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionRow
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomErrorText
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.ui.theme.ComposeTheme


class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EditProfileScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    EditProfileScreen(activity = MockActivity())
}

@Composable
fun EditProfileScreen(activity: ComponentActivity) {
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
                    BackToolbar(
                        title = "Edit Profile",
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
                        .padding(dimensionResource(id = R.dimen.title_margin))
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
fun MainContent(viewModel: EditProfileViewModel = viewModel()) {

    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current


    // get the first and last name from shared preferences
    val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)

    // set the first names and last names from the view model
    viewModel.onFirstNameChanged(sp.getString(SharedPreferencesNames.USER_FIRSTNAME, "") ?: "")
    viewModel.onLastNameChanged(sp.getString(SharedPreferencesNames.USER_LASTNAME, "") ?: "")


    val email = sp.getString(SharedPreferencesNames.USER_EMAIL, "") ?: ""

    // set the uri from the view model
    viewModel.onAvatarChanged(
        // convert the stored string to uri
        if (inPreview) null else
            ImageManager.stringToUri(
                context,
                sp.getString(SharedPreferencesNames.USER_AVATAR, "") ?: ""
            )
    )

    // the bottom sheet that is displayed at the bottom
    val isSheetOpen = remember { mutableStateOf(false) }
    AvatarBottomSheet(isSheetOpen = isSheetOpen, viewModel = viewModel)

    Avatar(imageUri = viewModel.imageUri, isSheetOpen = isSheetOpen)
    FirstAndLastNameFields(viewModel = viewModel)
    ErrorMessage(viewModel = viewModel)
    ReminderMessage()
    SaveButton(
        viewModel = viewModel,
        inPreview = inPreview,
        context = context,
        email = email
    )

}

@Composable
fun Avatar(
    imageUri: MutableState<Uri?>,
    isSheetOpen: MutableState<Boolean>,
){
// box for avatar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, dimensionResource(id = R.dimen.title_margin)),
        contentAlignment = Alignment.Center     // align the avatar to center
    ){
        // box for storing the image and the add button
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.profile_image_size_large))
        ){
            // display the profile icon
            Image(
                painter = if (imageUri.value != null) {
                    // if imageuri is not null, display it as an asyncimagepainter
                    rememberAsyncImagePainter(model = imageUri.value)
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
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )


            IconButton(onClick = {
                // show the bottom sheet when the + button is pressed
                isSheetOpen.value = true
            },
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.image_button_size))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Change avatar",
                    tint = Color.White)
            }
        }
    }
}

@Composable
fun FirstAndLastNameFields(
    viewModel: EditProfileViewModel,
){
    Column {
        // first name field
        CustomEditText(fieldLabel = "First name",
            fieldContent = viewModel.firstName.value,
            leftIcon = Icons.Outlined.AccountCircle,
            isEditable = true,
            onTextChanged = {viewModel.onFirstNameChanged(it)},
        )

        HorizontalDivider(thickness = 1.dp)

        // last name field
        CustomEditText(fieldLabel = "Last name",
            fieldContent = viewModel.lastName.value,
            leftIcon = Icons.Outlined.AccountCircle,
            isEditable = true,
            onTextChanged = {viewModel.onLastNameChanged(it)}
        )

        HorizontalDivider(thickness = 1.dp)
    }
}


@Composable
fun ErrorMessage(
    viewModel: EditProfileViewModel
) {
    // this error message is set to be displayed only when it is not empty
    // observe the viewmodel live data of the error
    val error by viewModel.profileError.observeAsState("")
    CustomErrorText(text = error)
}

@Composable
fun ReminderMessage(

) {
    // reminder message
    Text(
        text = "Only your name and avatar would be visible to others.",
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
fun SaveButton(
    viewModel: EditProfileViewModel,
    inPreview: Boolean,
    context: Context,
    email: String,
) {
    // box for save button
    // isloading state to display the loading animation
    var isLoading by remember{mutableStateOf(false)}

    // when isLoading changes, functions that uses the variable are re-composed
    if (isLoading){
        CustomProgressBar()
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        CustomButton(
            text = "Save Profile",
            type = ButtonType.FILLED,
            enabled = !isLoading,  // if loading, disable the button
            onClick = {
                // handle save logic here, as it involves the context (Toast message and auth manager)
                // update the data from the firestore database
                // but only when not in preview to avoid firebase errors
                if (inPreview) return@CustomButton

                // verify the input fields
                if (!viewModel.validateNames()){
                    return@CustomButton
                }

                // begin button operation
                isLoading = true

                // update user data
                val firebaseAuthManager = FirebaseAuthManager(context)

                // last lambda argument can be out of parenthesis
                firebaseAuthManager.updateUser(
                    email,
                    viewModel.firstName.value,
                    viewModel.lastName.value,
                    ImageManager.uriToString(context, viewModel.imageUri.value)

                ) { error ->
                    // finish loading
                    isLoading = false

                    // handle remaining actions
                    if (error.isEmpty()) {
                        // no errors

                        // display success toast message
                        Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_SHORT)
                            .show()

                        // exit activity
                        (context as? Activity)?.finish()  // safe cast and safe call

                    } else {
                        // display fail toast message
                        Toast.makeText(context, "Profile update failed", Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarBottomSheet(
    viewModel: EditProfileViewModel,
    isSheetOpen: MutableState<Boolean>
){
    if (isSheetOpen.value){
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen.value = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            // launcher to request image from the device
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = {
                    viewModel.onAvatarChanged(it)

                    // close the bottom sheet here after the imageuri value has been set
                    isSheetOpen.value = false
                }
            )

            // content of the bottom sheet
            Column(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.content_margin)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ){
                CustomActionRow(
                    text = "New avatar",
                    leftIcon = Icons.Outlined.Add,
                    rightIcon = null,
                    onClick = {
                        // pick image from the gallery to modify the imageuri (The image that is displayed on screen)
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
                CustomActionRow(
                    text = "Remove avatar",
                    leftIcon = Icons.Outlined.Delete,
                    rightIcon = null,
                    tint = MaterialTheme.colorScheme.error,
                    onClick = {
                        // remove the profile avatar
                        viewModel.onAvatarChanged(null)

                        // dismiss the bottom sheet
                        isSheetOpen.value = false
                    },
                )
            }
        }
    }
}





