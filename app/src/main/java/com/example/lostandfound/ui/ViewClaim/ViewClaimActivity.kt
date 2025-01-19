package com.example.lostandfound.ui.ViewClaim

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomComparisonField
import com.example.lostandfound.CustomElements.CustomComparisonTextField
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.CustomElements.CustomUserDialog
import com.example.lostandfound.CustomElements.CustomViewTwoLocationsDialog
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.Data.lostStatusText
import com.example.lostandfound.Data.statusColor
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.Utility.LocationManager
import com.example.lostandfound.ui.Done.DoneActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

class ViewClaimActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: ViewClaimViewModel by viewModels()

        // load the passed intent data into the view model
        val passedClaim = intent.getParcelableExtra<Claim>(IntentExtraNames.INTENT_CLAIM_ITEM)
        if (passedClaim != null) {
            viewModel.claimData = passedClaim
        }

        setContent {
            ViewClaimScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ViewClaimScreen(activity = MockActivity(), viewModel = ViewClaimViewModel())
}

@Composable
fun ViewClaimScreen(activity: ComponentActivity, viewModel: ViewClaimViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "View Claim", activity = activity)
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
fun MainContent(viewModel: ViewClaimViewModel) {
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
            ClaimStatus(viewModel = viewModel)
            Reference(viewModel = viewModel)
            Status(viewModel = viewModel)
            ItemImage(viewModel = viewModel)
            ItemDetails(viewModel = viewModel)
            LocationData(context = context, viewModel = viewModel)
            SecurityQuestion(viewModel = viewModel)
            UserData(context = context, viewModel = viewModel)

            // the user have the power to accept this claim if:
            // 1. they are the owner of the found item
            // 2. the found item has status 1, i.e. no claims that are approved
            val canAccept = viewModel.foundItemData.userID == FirebaseUtility.getUserID()
                    && viewModel.foundItemData.status == 1

            // action buttons
            AcceptButton(context = context, viewModel = viewModel, canAccept = canAccept)
            DoneButton(context = context, viewModel = viewModel, canAccept = canAccept)
        }
    }
}

@Composable
fun ClaimStatus(
    viewModel: ViewClaimViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.title_margin),
                horizontal = dimensionResource(id = R.dimen.content_margin)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        if (viewModel.claimData.isApproved) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Approved",
                modifier = Modifier.size(dimensionResource(id = R.dimen.image_button_size)),
                tint = colorResource(id = R.color.status2)
            )
            Text(
                text = "This claim has been approved by the found user.",
                color = colorResource(id = R.color.status2),
                fontWeight = FontWeight.Bold,
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Cancel,
                contentDescription = "Not been approved",
                modifier = Modifier.size(dimensionResource(id = R.dimen.image_button_size)),
                tint = colorResource(id = R.color.status0)
            )
            Text(
                text = "This claim has not been approved by the found user.",
                color = colorResource(id = R.color.status0),
                fontWeight = FontWeight.Bold,
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun Reference(viewModel: ViewClaimViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        CustomComparisonField(
            centerLabel = {},

            contentLeft = {
                Text(
                    text = "Lost item",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                )
            },
            contentRight = {
                Text(
                    text = "Found item",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                )
            }
        )

        CustomComparisonField(
            centerLabel = {

            },
            contentLeft = {
                Text(
                    text = "#" + viewModel.lostItemData.itemID,
                    color = Color.Gray,
                    style = Typography.bodyMedium
                )
            },
            contentRight = {
                Text(
                    text = "#" + viewModel.foundItemData.itemID,
                    color = Color.Gray,
                    style = Typography.bodyMedium
                )
            }
        )
    }
}

@Composable
fun Status(viewModel: ViewClaimViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        CustomComparisonField(
            centerLabel = {

            },
            contentLeft = {
                Text(
                    text = lostStatusText[viewModel.lostItemData.status] ?: "",
                    style = Typography.bodyMedium,
                    color = colorResource(
                        id = statusColor[viewModel.lostItemData.status] ?: R.color.status0
                    ),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            contentRight = {
                Text(
                    text = foundStatusText[viewModel.foundItemData.status] ?: "",
                    style = Typography.bodyMedium,
                    color = colorResource(
                        id = statusColor[viewModel.foundItemData.status] ?: R.color.status0
                    ),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemImage(viewModel: ViewClaimViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomComparisonField(
            contentLeft = {
                // image of the item
                GlideImage(
                    model = Uri.parse(viewModel.lostItemData.image),
                    contentDescription = "Lost item image",
                    modifier = Modifier
                        .weight(3f)
                        .padding(horizontal = dimensionResource(id = R.dimen.content_margin_half)),
                    alignment = Alignment.Center
                )
            },
            contentRight = {
                GlideImage(
                    model = Uri.parse(viewModel.foundItemData.image),
                    contentDescription = "Found item image",
                    modifier = Modifier
                        .weight(3f)
                        .padding(horizontal = dimensionResource(id = R.dimen.content_margin_half)),
                    alignment = Alignment.Center
                )
            },
            centerLabel = {}
        )
    }
}

@Composable
fun ItemDetails(viewModel: ViewClaimViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        CustomGrayTitle(text = "Item details")

        // Name of item
        CustomComparisonTextField(
            centerLabel = "Name",
            contentLeft = viewModel.lostItemData.itemName,
            contentRight = viewModel.foundItemData.itemName,
            icon = Icons.Outlined.Edit
        )
        HorizontalDivider(thickness = 1.dp)

        // category and subcategory
        CustomComparisonTextField(
            centerLabel = "Category",
            contentLeft = viewModel.lostItemData.category + ", " + viewModel.lostItemData.subCategory,
            contentRight = viewModel.foundItemData.category + ", " + viewModel.foundItemData.subCategory,
            icon = Icons.Outlined.Folder
        )
        HorizontalDivider(thickness = 1.dp)

        // date and time
        CustomComparisonTextField(
            centerLabel = "Time",
            contentLeft = DateTimeManager.dateTimeToString(viewModel.lostItemData.dateTime),
            contentRight = DateTimeManager.dateTimeToString(viewModel.foundItemData.dateTime),
            icon = Icons.Outlined.CalendarMonth
        )
        HorizontalDivider(thickness = 1.dp)

        // color
        /*
        CustomComparisonTextField(
            centerLabel = "Category",
            contentLeft = viewModel.lostItemData.color,
            contentRight = viewModel.foundItemData.color,
            icon = Icons.Outlined.Palette
        )

         */
        HorizontalDivider(thickness = 1.dp)

        // brand (Optional)
        CustomComparisonTextField(
            centerLabel = "Brand",
            contentLeft = viewModel.lostItemData.brand.ifEmpty { "Unknown" },
            contentRight = viewModel.foundItemData.brand.ifEmpty { "Unknown" },
            icon = Icons.Outlined.Title
        )
        HorizontalDivider(thickness = 1.dp)

        // description (Optional)
        CustomComparisonTextField(
            centerLabel = "Description",
            contentLeft = viewModel.lostItemData.description.ifEmpty { "Unknown" },
            contentRight = viewModel.foundItemData.description.ifEmpty { "Unknown" },
            icon = Icons.Outlined.Description
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
fun LocationData(
    context: Context,
    viewModel: ViewClaimViewModel
) {
    Column {
        CustomGrayTitle(text = "Location")

        CustomActionText(
            text = "View location",
            onClick = {
                viewModel.isLocationDialogShown.value = true
            },
        )
    }

    CustomViewTwoLocationsDialog(
        context = context,
        isDialogShown = viewModel.isLocationDialogShown,
        selectedLocation1 = LocationManager.pairToLatlng(viewModel.lostItemData.location),
        selectedLocation2 = LocationManager.pairToLatlng(viewModel.foundItemData.location)
    )
}

@Composable
fun UserData(
    context: Context,
    viewModel: ViewClaimViewModel
) {
    // the view claim screen can be viewed by either the lost user or the found user.
    // if the viewer is the lost user, display this
    if (viewModel.lostItemData.userID == FirebaseUtility.getUserID()) {
        Column {
            CustomGrayTitle(text = "Contact user who found this item")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    CustomEditText(
                        fieldLabel = "User",
                        fieldContent = viewModel.foundUser.firstName + ' ' + viewModel.foundUser.lastName,  // display the opposite user name
                        leftIcon = Icons.Outlined.AccountCircle,
                        isEditable = false
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
            HorizontalDivider(thickness = 1.dp)
        }
    }

    // if the viewer is the found user, display this
    if (viewModel.foundItemData.userID == FirebaseUtility.getUserID()) {
        Column {
            CustomGrayTitle(text = "Contact user who claimed this item")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    CustomEditText(
                        fieldLabel = "User",
                        fieldContent = viewModel.lostUser.firstName + ' ' + viewModel.lostUser.lastName,
                        leftIcon = Icons.Outlined.AccountCircle,
                        isEditable = false
                    )
                }

                // contact user button and dialog, when the user is not the current user
                if (viewModel.lostUser.userID != FirebaseUtility.getUserID()) {
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
            HorizontalDivider(thickness = 1.dp)
        }
    }

    // common dialog for contacting user
    CustomUserDialog(
        user = viewModel.foundUser,
        context = context,
        isDialogShown = viewModel.isContactUserDialogShown
    )
}

@Composable
fun SecurityQuestion(
    viewModel: ViewClaimViewModel
) {
    // if the viewer is the found user and the found item has security question, display this
    if (viewModel.foundItemData.userID == FirebaseUtility.getUserID()
        && viewModel.foundItemData.securityQuestion.isNotEmpty()
    ) {

        Column {
            CustomGrayTitle(text = "Security question")
            CustomEditText(
                fieldLabel = "Security question",
                fieldContent = viewModel.foundItemData.securityQuestion,
                leftIcon = Icons.AutoMirrored.Outlined.HelpOutline,
                isEditable = false
            )
            CustomEditText(
                fieldLabel = "Your answer",
                fieldContent = viewModel.foundItemData.securityQuestionAns,
                leftIcon = Icons.Outlined.CheckCircle,
                isEditable = false
            )
            CustomEditText(
                fieldLabel = "Answer provided by the claim user",
                fieldContent = viewModel.claimData.securityQuestionAns,
                leftIcon = Icons.Outlined.CheckCircle,
                isEditable = false
            )
            HorizontalDivider(thickness = 1.dp)
        }
    }
}

@Composable
fun DoneButton(
    context: Context,
    viewModel: ViewClaimViewModel,
    canAccept: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.content_margin_half)),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomButton(
            text = "Done",
            type = if (canAccept) ButtonType.OUTLINED else ButtonType.FILLED,
            onClick = {
                (context as Activity).finish()
            }
        )
    }
}

// accept and reject buttons, if the found item hasnt been accepted by any lost item claims (status 1)
@Composable
fun AcceptButton(
    context: Context,
    viewModel: ViewClaimViewModel,
    canAccept: Boolean
) {
    // display the button only when can accept
    if (canAccept) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.content_margin_half)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You can choose to approve this claim as you owns the found item.",
                style = Typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.title_margin)
                )
            )

            CustomButton(
                text = "Approve this claim",
                type = ButtonType.FILLED,
                onClick = {
                    // open the dialog to accept
                    viewModel.isAcceptClaimDialogShown.value = true
                }
            )
        }

        // dialog when trying to click the accept button
        CustomTextDialog(
            icon = Icons.Outlined.CheckCircle,
            title = "Approve this claim?",
            content = "Once you have approved this claim, you cannot approve other claims for this item.",
            confirmButton = {
                CustomButton(
                    text = "Approve",
                    type = ButtonType.FILLED,
                    onClick = {
                        // approve the claim
                        viewModel.approveClaim(object : ErrorCallback{
                            override fun onComplete(error: String) {
                                if (error.isNotEmpty()) {
                                    Toast.makeText(
                                        context,
                                        error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }

                                // open done activity
                                val intent: Intent = Intent(context, DoneActivity::class.java)
                                intent.putExtra(
                                    IntentExtraNames.INTENT_DONE_ACTIVITY_TITLE,
                                    "Claim approved!"
                                )
                                context.startActivity(intent)
                            }
                        })
                    }
                )
            },
            dismissButton = {
                CustomButton(
                    text = "Cancel",
                    type = ButtonType.OUTLINED,
                    onClick = {
                        viewModel.isAcceptClaimDialogShown.value = false
                    }
                )
            },
            isDialogShown = viewModel.isAcceptClaimDialogShown
        )
    }
    // else if the found item has already approved an item, it cannot approve another
    else if (viewModel.lostItemData.status == 1 && viewModel.foundItemData.status == 2) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.content_margin_half)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You cannot approve this item as you have already approved another item.",
                style = Typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.title_margin)
                )
            )
        }
    }
}


// function to load data, called when the activity is created
fun loadData(
    context: Context,
    viewModel: ViewClaimViewModel
) {
    // is loading initially
    viewModel.isLoading.value = true

    // load lost item data of the current user from the view model
    viewModel.loadDataWithClaim(object : ErrorCallback {
        override fun onComplete(error: String) {
            viewModel.isLoading.value = false

            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                return
            }
        }
    })
}