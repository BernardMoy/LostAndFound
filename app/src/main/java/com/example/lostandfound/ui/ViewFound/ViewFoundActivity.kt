package com.example.lostandfound.ui.ViewFound

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.CustomElements.CustomUserDialog
import com.example.lostandfound.CustomElements.CustomViewLocationDialog
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.Data.statusColor
import com.example.lostandfound.Data.stringToColor
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.MainActivity
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.Utility.LocationManager
import com.example.lostandfound.ui.ViewClaimList.ViewClaimListActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class ViewFoundActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: ViewFoundViewModel by viewModels()

        // load the passed intent data into the view model
        val passedItem = intent.getParcelableExtra<FoundItem>(IntentExtraNames.INTENT_FOUND_ID)
        if (passedItem != null) {
            viewModel.itemData = passedItem
        }

        setContent {
            ViewFoundScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ViewFoundScreen(activity = MockActivity(), viewModel = ViewFoundViewModel())
}

@Composable
fun ViewFoundScreen(activity: ComponentActivity, viewModel: ViewFoundViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "View found item", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin))
                        .verticalScroll(rememberScrollState()),   // make screen scrollable
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
fun MainContent(viewModel: ViewFoundViewModel) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    LaunchedEffect(Unit) {
        loadData(context, viewModel)
    }

    // display content
    if (!inPreview && viewModel.isLoading.value) {
        CustomCenteredProgressbar()

    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {
            Reference(viewModel = viewModel)
            Status(viewModel = viewModel)
            ItemImage(viewModel = viewModel)
            ItemDetails(viewModel = viewModel)
            LocationData(viewModel = viewModel)
            UserData(context = context, viewModel = viewModel)
            ActionButtons(context = context, inPreview = inPreview, viewModel = viewModel)

            // also display the user
        }
    }
}

@Composable
fun Reference(viewModel: ViewFoundViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Reference: #" + viewModel.itemData.itemID,
            style = Typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Status(viewModel: ViewFoundViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Status: " + foundStatusText[viewModel.itemData.status],
            style = Typography.bodyMedium,
            color = colorResource(
                id = statusColor[viewModel.itemData.status] ?: R.color.status0
            ),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemImage(viewModel: ViewFoundViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        val displayedImage =
            if (viewModel.itemData.image.isEmpty()) ImageManager.PLACEHOLDER_IMAGE_STRING else viewModel.itemData.image
        // image of the item
        GlideImage(
            model = Uri.parse(displayedImage),
            contentDescription = if (viewModel.itemData.image.isEmpty()) "No image provided" else "Item image",
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.Center
        )
    }
}

@Composable
fun ItemDetails(viewModel: ViewFoundViewModel) {
    Column {
        CustomGrayTitle(text = "Item details")

        // Name of item
        CustomEditText(
            fieldLabel = "Item name",
            fieldContent = viewModel.itemData.itemName,
            leftIcon = Icons.Outlined.Edit,
            isEditable = false,
            testTag = "ViewFoundName"
        )
        HorizontalDivider(thickness = 1.dp)

        // category and subcategory
        CustomEditText(
            fieldLabel = "Category",
            fieldContent = viewModel.itemData.category + ", " + viewModel.itemData.subCategory,
            leftIcon = Icons.Outlined.Folder,
            isEditable = false,
            testTag = "ViewFoundCategory"
        )
        HorizontalDivider(thickness = 1.dp)

        // date and time
        CustomEditText(
            fieldLabel = "Date and time",
            fieldContent = DateTimeManager.dateTimeToString(viewModel.itemData.dateTime),
            leftIcon = Icons.Outlined.CalendarMonth,
            isEditable = false,
            testTag = "ViewFoundDateTime"
        )
        HorizontalDivider(thickness = 1.dp)

        // color
        val colorText = viewModel.itemData.color.joinToString(", ")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
        ) {
            // display colors in text
            Box(modifier = Modifier.weight(1f)) {
                CustomEditText(
                    fieldLabel = "Color",
                    fieldContent = colorText,
                    leftIcon = Icons.Outlined.Palette,
                    isEditable = false,
                    testTag = "ViewFoundColor"
                )
            }

            // display colors in circles
            for (colorString in viewModel.itemData.color) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = "Color",
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.title_margin))
                        .border(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                            shape = CircleShape
                        ),

                    tint = stringToColor[colorString] ?: Color.Gray
                )
            }
        }
        HorizontalDivider(thickness = 1.dp)

        // brand (Optional)
        CustomEditText(
            fieldLabel = "Brand",
            fieldContent = if (viewModel.itemData.brand.isNotEmpty()) viewModel.itemData.brand else "(Not provided)",
            leftIcon = Icons.Outlined.Title,
            isEditable = false,
            testTag = "ViewFoundBrand"
        )
        HorizontalDivider(thickness = 1.dp)

        // description (Optional)
        CustomEditText(
            fieldLabel = "Description",
            fieldContent = if (viewModel.itemData.description.isNotEmpty()) viewModel.itemData.description else "(Not provided)",
            leftIcon = Icons.Outlined.Description,
            isEditable = false,
            testTag = "ViewFoundDescription"
        )
        HorizontalDivider(thickness = 1.dp)

        // security question - whether it exists
        CustomEditText(
            fieldLabel = "Security question",
            fieldContent = if (viewModel.itemData.securityQuestion.isEmpty()) "No" else "Yes",
            isEditable = false,
            leftIcon = Icons.Outlined.Lock,
            testTag = "ViewFoundSecurityQuestion"
        )
    }
}

@Composable
fun LocationData(
    viewModel: ViewFoundViewModel
) {
    Column {
        CustomGrayTitle(text = "Location")

        // if the location is not null, users can view it
        if (viewModel.itemData.location != null) {
            CustomActionText(
                text = "View location",
                onClick = {
                    viewModel.isLocationDialogShown.value = true
                },
            )
        } else {
            Text(
                text = "(Location is not provided)",
                color = Color.Gray,
                style = Typography.bodyMedium
            )
        }
    }

    CustomViewLocationDialog(
        isDialogShown = viewModel.isLocationDialogShown,
        selectedLocation = if (viewModel.itemData.location != null) LocationManager.pairToLatlng(
            viewModel.itemData.location!!
        ) else null
    )
}


@Composable
fun UserData(
    context: Context,
    viewModel: ViewFoundViewModel
) {
    Column {
        CustomGrayTitle(text = "User information")

        // Name of user
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) { // Name of user
                val userDisplayName =
                    viewModel.foundUser.firstName + ' ' + viewModel.foundUser.lastName

                CustomEditText(
                    fieldLabel = "User",
                    fieldContent = if (viewModel.itemData.userID == FirebaseUtility.getUserID()) "$userDisplayName (You)" else userDisplayName,
                    leftIcon = Icons.Outlined.AccountCircle,
                    isEditable = false,
                    testTag = "ViewFoundUser"
                )
            }

            // contact user button and dialog, when the user is not the current user
            if (viewModel.foundUser.userID != FirebaseUtility.getUserID()) {
                CustomButton(
                    text = "Contact",
                    type = ButtonType.TONAL,
                    onClick = {
                        viewModel.isContactUserDialogShown.value = true
                    },
                    small = true
                )
            }

        }

        CustomUserDialog(
            user = viewModel.foundUser,
            context = context,
            isDialogShown = viewModel.isContactUserDialogShown
        )




        HorizontalDivider(thickness = 1.dp)

        // time posted
        CustomEditText(
            fieldLabel = "Time posted",
            fieldContent = DateTimeManager.dateTimeToString(viewModel.itemData.timePosted),
            leftIcon = Icons.Outlined.CalendarMonth,
            isEditable = false,
            testTag = "ViewFoundTimePosted"
        )
        HorizontalDivider(thickness = 1.dp)
    }
}


@Composable
fun ActionButtons(
    context: Context,
    inPreview: Boolean,
    viewModel: ViewFoundViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.content_margin))
    ) {
        // only display buttons when the lost item is found by the current user
        if (inPreview || FirebaseUtility.getUserID() == viewModel.itemData.userID) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // if the status is 0, display a message saying there are no claims
                if (inPreview || viewModel.itemData.status == 0) {
                    Text(
                        text = "There are currently no users who has claimed this item.",
                        style = Typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
                    )
                }

                // if the status is 1 or 2, display this
                if (inPreview || viewModel.itemData.status == 1 || viewModel.itemData.status == 2) {
                    CustomButton(
                        text = "View claims for this item",  // there can only be one claim for each lost item
                        type = ButtonType.FILLED,
                        onClick = {
                            // start new claim list activity
                            val intent = Intent(context, ViewClaimListActivity::class.java)

                            // pass only the found item to the claim list activity
                            intent.putExtra(IntentExtraNames.INTENT_FOUND_ID, viewModel.itemData)

                            context.startActivity(intent)
                        }
                    )
                }

                // if the status is 0, display the deletable option
                if (inPreview || viewModel.itemData.status == 0) {
                    CustomButton(
                        text = "Delete item",
                        type = ButtonType.WARNING,
                        onClick = {
                            // open delete dialog
                            viewModel.isDeleteDialogShown.value = true
                        }
                    )

                    // the dialog for logging out
                    CustomTextDialog(
                        icon = Icons.Outlined.DeleteOutline,
                        title = "Delete item",
                        content = "Are you sure to delete this item? This action cannot be undone.",
                        confirmButton = {
                            CustomButton(
                                text = "Delete",
                                type = ButtonType.WARNING,
                                onClick = {
                                    // delete item
                                    viewModel.deleteItem(object : Callback<Boolean> {
                                        override fun onComplete(result: Boolean) {
                                            if (!result) {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to delete item",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return
                                            }

                                            // show successfully deleted message
                                            Toast.makeText(
                                                context,
                                                "Item deleted!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            // delete item successful, close dialog
                                            viewModel.isDeleteDialogShown.value = false

                                            // redirect the activity to home
                                            val intent =
                                                Intent(context, MainActivity::class.java).apply {
                                                    flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                                }

                                            // start main activity
                                            context.startActivity(intent)

                                            // finish current activity
                                            (context as Activity).finish()

                                        }
                                    })
                                }
                            )
                        },
                        dismissButton = {
                            CustomButton(text = "Cancel",
                                type = ButtonType.OUTLINED,
                                onClick = {
                                    // dismiss the dialog
                                    viewModel.isDeleteDialogShown.value = false
                                }
                            )
                        },
                        isDialogShown = viewModel.isDeleteDialogShown
                    )
                }
            }
        }
    }
}

// function to load data, called when the activity is created
fun loadData(
    context: Context,
    viewModel: ViewFoundViewModel
) {
    // is loading initially
    viewModel.isLoading.value = true

    // load found item data of the current user from the view model
    viewModel.getUser(object : Callback<Boolean> {
        override fun onComplete(result: Boolean) {
            viewModel.isLoading.value = false

            if (!result) {
                // display toast message for failed data retrieval
                Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
            }
        }
    })
}





