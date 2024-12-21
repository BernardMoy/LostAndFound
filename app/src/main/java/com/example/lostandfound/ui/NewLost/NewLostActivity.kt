package com.example.lostandfound.ui.NewLost

import android.app.Activity
import android.content.Context
import android.graphics.Paint.Align
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import com.example.lostandfound.R
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.outlined.Close
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
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomDropdownMenu
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomErrortext
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomInputField
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.Utility.SharedPreferencesNames
import com.example.lostandfound.ui.EditProfile.EditProfileViewModel
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

    val itemName = remember { mutableStateOf("") }

    val itemImage = remember {
        mutableStateOf<Uri?>(
            null    // image is initially null
        )
    }

    // launcher to pick image from device
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            itemImage.value = uri
        }
    )

    // Display different input fields
    ItemName(itemName = itemName)
    ItemImage(imageUri = itemImage, launcher = launcher)
    Category()
    Subcategory(context = context)

    DoneButton()
}

@Composable
fun ItemName(
    itemName: MutableState<String>
){
    CustomGrayTitle(text = "Name of item")
    CustomInputField(
        fieldContent = itemName.value,
        isEditable = true,
        onTextChanged = {itemName.value = it},
        placeholder = "e.g. Bluetooth earbuds"
    )
}

@Composable
fun ItemImage(
    imageUri: MutableState<Uri?>,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
){
    CustomGrayTitle(text = "Item image")

    // Box for storing the image and the add button
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, dimensionResource(id = R.dimen.title_margin)),
        contentAlignment = Alignment.CenterStart
    ){
        Column(

        ){
            // box for storing the image
            if (imageUri.value != null) {
                Box(
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
                ){
                    // display the image of the item only when it is not null
                    val painter = rememberAsyncImagePainter(model = imageUri.value)

                    Image(
                        painter = painter,
                        contentDescription = "Item image",
                        contentScale = ContentScale.FillWidth,  // make the image fill width
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    // the cross button at the top right of the image to remove it
                    IconButton(onClick = {
                        // remove the image
                        imageUri.value = null
                    },
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.image_button_size))
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(imageVector = Icons.Outlined.Close,
                            contentDescription = "Remove image",
                            tint = Color.White)
                    }
                }
            }


            // the clickable text to add new image
            Box(
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
            ){
                CustomActionText(
                    text = "Add Image",
                    onClick = {
                        // pick image from the gallery to modify the item image
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
            }
        }
    }
}


@Composable
fun Category(

){
    CustomGrayTitle(text = "Select category")
}

@Composable
fun Subcategory(
    context: Context
){
    CustomGrayTitle(text = "Select subcategory")
    
    CustomDropdownMenu(
        items = listOf<String>("Wallet", "Key", "Watch", "Umbrella", "Eyeglasses"),
        onItemSelected = { item ->
            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun DateAndTime(

){

}

@Composable
fun Location(

){

}

@Composable
fun AdditionalDescription(

){

}

@Composable
fun ReminderMessage(

){

}

@Composable
fun DoneButton(

) {
    // box for save button
    // isloading state to display the loading animation
    var isLoading by remember{mutableStateOf(false)}

    // when isLoading changes, functions that uses the variable are re-composed
    if (isLoading){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.image_button_size))
                    .padding(dimensionResource(id = R.dimen.content_margin_half)),
                color = MaterialTheme.colorScheme.background,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        CustomButton(
            text = "Done",
            type = ButtonType.FILLED,
            onClick = {

            }
        )
    }
}


