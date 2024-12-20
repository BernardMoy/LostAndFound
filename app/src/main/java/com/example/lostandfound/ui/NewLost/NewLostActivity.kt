package com.example.lostandfound.ui.NewLost

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.lostandfound.CustomElements.CustomErrortext
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.Utility.SharedPreferencesNames
import com.example.lostandfound.ui.theme.ComposeTheme


class NewLostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NewLostScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    NewLostScreen(activity = MockActivity())
}

@Composable
fun NewLostScreen(activity: ComponentActivity) {
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
                        title = "New Lost item",
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
fun MainContent(viewModel: NewLostViewModel = viewModel()) {

    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarBottomSheet(isSheetOpen: MutableState<Boolean>, imageUri: MutableState<Uri?>){
    if (isSheetOpen.value){
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen.value = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            // launcher to request image from the device
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    imageUri.value = uri

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
                        imageUri.value = null

                        // dismiss the bottom sheet
                        isSheetOpen.value = false
                    },
                )
            }
        }
    }
}





