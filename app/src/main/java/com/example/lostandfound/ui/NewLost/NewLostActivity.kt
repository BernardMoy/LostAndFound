package com.example.lostandfound.ui.NewLost

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionRow
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomDatePickerTextField
import com.example.lostandfound.CustomElements.CustomDropdownMenu
import com.example.lostandfound.CustomElements.CustomErrorText
import com.example.lostandfound.CustomElements.CustomFilterChip
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomInputField
import com.example.lostandfound.CustomElements.CustomLoginDialog
import com.example.lostandfound.CustomElements.CustomPickLocationDialog
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.CustomElements.CustomTimePickerTextField
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.Data.categories
import com.example.lostandfound.Data.stringToColor
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R
import com.example.lostandfound.ui.Login.LoginActivity
import com.example.lostandfound.ui.Search.SearchActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import java.io.File


class NewLostActivity : ComponentActivity() {
    val viewModel: NewLostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // first check if user is logged in
            if (!UserManager.isUserLoggedIn()) {
                val isDialogOpen = remember { mutableStateOf(true) }
                ComposeTheme {
                    CustomLoginDialog(
                        onDismissClicked = {
                            isDialogOpen.value = false
                            finish()
                        },
                        onLoginClicked = {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            isDialogOpen.value = false
                            finish()
                        },
                        isDialogShown = isDialogOpen
                    )
                }
            } else {
                NewLostScreen(activity = this, viewModel = viewModel)
            }
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    NewLostScreen(activity = MockActivity(), viewModel = viewModel())
}

@Composable
fun NewLostScreen(activity: ComponentActivity, viewModel: NewLostViewModel) {
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
                    MainContent(viewModel = viewModel)
                }
            }

            // the dialog when the back button is pressed
            CustomTextDialog(
                icon = Icons.Outlined.Cancel,
                isWarning = true ,
                title = "Discard changes?",
                content = "All your changes will be lost.",
                confirmButton = {
                    CustomButton(
                        text = "Discard",
                        type = ButtonType.WARNING,
                        onClick = {
                            // dismiss the dialog
                            isDialogShown.value = false
                            // exit the activity
                            activity.finish()
                        }
                    )
                },
                dismissButton = {
                    CustomButton(text = "Cancel",
                        type = ButtonType.OUTLINED,
                        onClick = {
                            // dismiss the dialog
                            isDialogShown.value = false
                        }
                    )
                },
                isDialogShown = isDialogShown    // dialog is shown only when the value of isDialogShown is true
            )
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: NewLostViewModel) {

    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // the bottom sheet for getting image from camera or gallery options
    ImageBottomSheet(isSheetOpen = viewModel.isSheetOpen, viewModel = viewModel, context = context)

    // load data from shared prefs to view models
    val sp: SharedPreferences = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)
    viewModel.userAvatar = sp.getString(SharedPreferencesNames.USER_AVATAR, "") ?: ""
    viewModel.userFirstName = sp.getString(SharedPreferencesNames.USER_FIRSTNAME, "") ?: ""
    viewModel.userLastName = sp.getString(SharedPreferencesNames.USER_LASTNAME, "") ?: ""

    // Display different input fields
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        ItemName(viewModel = viewModel)
        ItemImage(viewModel = viewModel)
        Category(viewModel = viewModel)
        Subcategory(viewModel = viewModel)
        ItemColor(context = context, viewModel = viewModel)
        ItemBrand(viewModel = viewModel)
        DateAndTime(viewModel = viewModel)
        Location(context = context, viewModel = viewModel)
        AdditionalDescription(viewModel = viewModel)
        ReminderMessage(viewModel = viewModel)
        DoneButton(context = context, viewModel = viewModel)
    }

}

@Composable
fun ItemName(
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Name of item")
    CustomInputField(
        fieldContent = viewModel.itemName.value,
        isEditable = true,
        onTextChanged = { viewModel.onItemNameChanged(it) },
        placeholder = "e.g. Bluetooth earbuds",
        isError = viewModel.nameError.value.isNotEmpty(),
        leadingIcon = Icons.Outlined.Edit,
        testTag = "NameInput"
    )

    CustomErrorText(
        text = viewModel.nameError.value
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemImage(
    viewModel: NewLostViewModel,
) {
    CustomGrayTitle(text = "Item image (Optional)")

    // Box for storing the image and the add button
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, dimensionResource(id = R.dimen.title_margin)),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            // box for storing the image
            if (viewModel.itemImage.value != null) {
                Box(
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
                ) {
                    // display the image of the item only when it is not null
                    val painter = rememberAsyncImagePainter(model = viewModel.itemImage.value)
                    Image(
                        painter = painter,
                        contentDescription = "Item image",
                        contentScale = ContentScale.FillWidth,  // make the image fill width
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    // the cross button at the top right of the image to remove it
                    IconButton(
                        onClick = {
                            // remove the image
                            viewModel.onImageCrossClicked()
                        },
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.image_button_size))
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Remove image",
                            tint = Color.White
                        )
                    }
                }
            }


            // the clickable text to add new image
            Box(
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
            ) {
                CustomActionText(
                    text = "Add Image",
                    onClick = {
                        // Make the bottom sheet visible
                        viewModel.isSheetOpen.value = true
                    },
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Category(
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Category")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        // for each category, create a custom filter chip for that
        categories.forEach { category ->
            CustomFilterChip(
                label = category.name,
                leadingIcon = category.leadingIcon,
                // change the selected category var
                // to ensure only one can be selected at a time
                onClick = {
                    viewModel.onCategorySelected(category)

                    // reset the selected subcategory every time
                    viewModel.onSubCategorySelected("")
                },
                isSelected = viewModel.isCategorySelected(category),
                isError = viewModel.categoryError.value.isNotEmpty()
            )
        }
    }

    CustomErrorText(text = viewModel.categoryError.value)
}

@Composable
fun Subcategory(
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Subcategory")

    // display different fields depending on the selected category
    if (viewModel.selectedCategory == null) {
        Text(
            text = "Please select a category first",
            style = Typography.bodyMedium,
            color = Color.Gray
        )

    } else if (viewModel.selectedCategory!!.name == "Others") {
        CustomInputField(
            fieldContent = "",   // have no initial value
            isEditable = true,
            onTextChanged = { viewModel.onSubCategorySelected(it) },  // change the subcat to the string inputted
            placeholder = "Describe the category...",
            isError = viewModel.subCategoryError.value.isNotEmpty(),
            testTag = "SubCategoryInput"
        )


    } else {
        Box(
            modifier = Modifier.testTag("SubCategoryDropdown")
        ) {
            CustomDropdownMenu(
                items = viewModel.selectedCategory!!.subCategories,
                selectedText = viewModel.selectedSubCategory,   // will be changed when new option is selectedisError = viewModel.subCategoryError.value.isNotEmpty()
                isError = viewModel.subCategoryError.value.isNotEmpty()
            )
        }
    }

    CustomErrorText(text = viewModel.subCategoryError.value)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemColor(
    context: Context,
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Color")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        // for each category, create a custom filter chip for that
        for ((name, color) in stringToColor) {
            CustomFilterChip(
                label = name,
                leadingIcon = Icons.Filled.Circle,
                leadingIconTint = color,

                // change the selected category var
                // to ensure only one can be selected at a time
                onClick = {
                    if (!viewModel.onColorSelected(name)) {
                        Toast.makeText(
                            context,
                            "You can only select at most 3 colors",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                isSelected = viewModel.isColorSelected(name),
                isError = viewModel.colorError.value.isNotEmpty()
            )
        }
    }

    CustomErrorText(text = viewModel.colorError.value)
}


@Composable
fun ItemBrand(
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Brand (Optional)")
    CustomInputField(
        fieldContent = viewModel.itemBrand.value,
        isEditable = true,
        onTextChanged = { viewModel.onItemBrandChanged(it) },
        placeholder = "What is the brand of your item?",
        leadingIcon = Icons.Outlined.Title,
        isError = false, // wont have error
        testTag = "BrandInput"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTime(
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Date and time")

    CustomDatePickerTextField(
        selectedDate = viewModel.selectedDate,
        isDialogShown = viewModel.isDateDialogShown,
        placeholder = "Select a date...",
        isError = viewModel.dateError.value.isNotEmpty(),
        datePickerTitle = "Date of lost item",
        testTag = "DateInput",
    )

    CustomErrorText(text = viewModel.dateError.value)

    CustomTimePickerTextField(
        selectedHour = viewModel.selectedHour,
        selectedMinute = viewModel.selectedMinute,
        isDialogShown = viewModel.isTimeDialogShown,
        placeholder = "Select a time...",
        isError = viewModel.timeError.value.isNotEmpty(),
        testTag = "TimeInput"
    )

    CustomErrorText(text = viewModel.timeError.value)

}

@Composable
fun Location(
    context: Context,
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Location")

    /*
    // not necessary to display location description
    Text(
        text = LocationManager.getLocationDescription(context, viewModel.selectedLocation.value),
        style = Typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )

     Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.content_margin_half)))
     */

    // text message to show that location has been selected
    if (viewModel.selectedLocation.value != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.content_margin))
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                tint = colorResource(R.color.status2),
                contentDescription = "Checkmark",
                modifier = Modifier.width(dimensionResource(id = R.dimen.title_margin))
            )
            Text(
                text = "Location added",
                color = colorResource(R.color.status2),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }


    // the action text to choose a location from google maps or delete it
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.content_margin))
    ) {
        CustomActionText(
            text = "Add location",
            onClick = {
                // is dialog shown become true
                viewModel.onAddLocationClicked()
            },
        )

        if (viewModel.selectedLocation.value != null) {
            CustomActionText(
                text = "Remove location",
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    // reset the selected location
                    viewModel.selectedLocation.value = null
                },
            )
        }
    }


    // the google maps dialog
    CustomPickLocationDialog(
        isDialogShown = viewModel.isLocationDialogShown,
        selectedLocation = viewModel.selectedLocation,
        context = context
    )
}

@Composable
fun AdditionalDescription(
    viewModel: NewLostViewModel
) {
    CustomGrayTitle(text = "Additional description (Optional)")
    CustomInputField(
        fieldContent = viewModel.additionalDescription.value,
        isEditable = true,
        leadingIcon = Icons.Outlined.Description,
        onTextChanged = { viewModel.onDescriptionChanged(it) },
        placeholder = "What should be noted of about your item?",
        isMultiLine = true,
        testTag = "DescriptionInput"
    )
}

@Composable
fun ReminderMessage(
    viewModel: NewLostViewModel
) {
    Text(
        text = "Please do not share sensitive personal information, " +
                "including bank account details, credit/debit card numbers, " +
                "biometric data or political or religious information.",
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
fun DoneButton(
    context: Context,
    viewModel: NewLostViewModel
) {

    // box for save button
    // isloading state to display the loading animation
    var isLoading by remember { mutableStateOf(false) }

    // when isLoading changes, functions that uses the variable are re-composed
    if (isLoading) {
        CustomProgressBar()
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CustomButton(
            text = "Done",
            type = ButtonType.FILLED,
            enabled = !isLoading,  // if loading, disable the button
            onClick = {
                // check if the input fields are valid
                if (!viewModel.validateInput()) {
                    return@CustomButton
                }

                // begin loading
                isLoading = true

                // add to firebase database
                viewModel.onDoneButtonClicked(object : Callback<LostItem?> {
                    override fun onComplete(result: LostItem?) {
                        isLoading = false

                        // if the item is null, it error
                        if (result == null) {
                            Toast.makeText(context, "Error adding item", Toast.LENGTH_SHORT).show()

                        } else {
                            val intent = Intent(context, SearchActivity::class.java)
                            // pass the generated lost item to the intent
                            intent.putExtra(
                                IntentExtraNames.INTENT_LOST_ID,
                                result
                            )
                            context.startActivity(intent)

                            // finish current activity
                            (context as Activity).finish()
                        }
                    }
                })
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBottomSheet(
    context: Context,
    viewModel: NewLostViewModel,
    isSheetOpen: MutableState<Boolean>
) {
    // stores the file of the image taken by camera
    val cameraImageFile = remember {
        File(context.externalCacheDir, "capture_${System.currentTimeMillis()}.jpg")   // make each file a unique name so that new images can override the old one
    }

    // stores the image uri of the image taken by the camera
    val cameraImageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        cameraImageFile
    )

    // launcher to take image from camera
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // Update the image uri with the taken photo
                viewModel.itemImage.value = cameraImageUri
                // close the bottom sheet here after the imageuri value has been set
                isSheetOpen.value = false

            } else {
                Toast.makeText(context, "Failed to take image", Toast.LENGTH_SHORT).show()
            }
        }

    // launcher to request camera permission
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { success ->
            if (success) {
                // launch camera if permission is granted
                cameraLauncher.launch(cameraImageUri)
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    // bottom avatar sheet main content
    if (isSheetOpen.value) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen.value = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            // launcher to request image from the device
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = {
                    viewModel.onImagePicked(it)

                    // close the bottom sheet here after the imageuri value has been set
                    isSheetOpen.value = false
                }
            )

            // content of the bottom sheet
            Column(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.content_margin)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                CustomActionRow(
                    text = "Take image with camera",
                    leftIcon = Icons.Outlined.CameraAlt,
                    rightIcon = null,
                    onClick = {
                        // pick image from device camera
                        // request camera permission
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            // if camera permission is granted, take picture
                            cameraLauncher.launch(cameraImageUri)
                        } else {
                            // else, request the camera permission
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                )
                CustomActionRow(
                    text = "Select image from gallery",
                    leftIcon = Icons.Outlined.PhotoAlbum,
                    rightIcon = null,
                    onClick = {
                        // pick image from gallery
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
            }
        }
    }
}



