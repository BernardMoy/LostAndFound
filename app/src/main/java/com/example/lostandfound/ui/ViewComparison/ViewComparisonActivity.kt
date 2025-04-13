package com.example.lostandfound.ui.ViewComparison

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.example.lostandfound.CustomElements.CustomInputDialog
import com.example.lostandfound.CustomElements.CustomUserDialog
import com.example.lostandfound.CustomElements.CustomViewTwoLocationsDialog
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.Data.lostStatusText
import com.example.lostandfound.Data.statusColor
import com.example.lostandfound.Data.stringToColor
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.Utility.LocationManager
import com.example.lostandfound.ui.Done.DoneActivity
import com.example.lostandfound.ui.ViewFound.ViewFoundActivity
import com.example.lostandfound.ui.ViewLost.ViewLostActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import kotlin.math.round


class ViewComparisonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: ViewComparisonViewModel by viewModels()

        // load the passed intent data into the view model
        val passedLostItem = intent.getParcelableExtra<LostItem>(IntentExtraNames.INTENT_LOST_ID)
        val passedFoundItem = intent.getParcelableExtra<FoundItem>(IntentExtraNames.INTENT_FOUND_ID)
        val passedClaimItem = intent.getParcelableExtra<Claim>(IntentExtraNames.INTENT_CLAIM_ITEM)
        val passedScoreData =
            intent.getParcelableExtra<ScoreData>(IntentExtraNames.INTENT_SCORE_DATA)
        if (passedLostItem != null) {
            viewModel.lostItemData = passedLostItem
        }
        if (passedFoundItem != null) {
            viewModel.foundItemData = passedFoundItem
        }
        if (passedClaimItem != null) {
            viewModel.claim = passedClaimItem
        }
        if (passedScoreData != null) {
            viewModel.scoreData = passedScoreData
        }

        setContent {
            ViewComparisonScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ViewComparisonScreen(activity = MockActivity(), viewModel = ViewComparisonViewModel())
}

@Composable
fun ViewComparisonScreen(activity: ComponentActivity, viewModel: ViewComparisonViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "View Comparison", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
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
fun MainContent(viewModel: ViewComparisonViewModel) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

        Column(
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.title_margin)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {
            OverallSimilarity(context = context, viewModel = viewModel)
            Column(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.title_margin)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                Reference(context = context, viewModel = viewModel)
                Status(viewModel = viewModel)
                ItemImage(viewModel = viewModel)
                ItemDetails(viewModel = viewModel)
                DescriptionData(context = context, viewModel = viewModel)
                LocationData(context = context, viewModel = viewModel)
                SecurityQuestion(viewModel = viewModel)
                UserData(context = context, viewModel = viewModel)
                ClaimButton(context = context, viewModel = viewModel)
            }

    }
}

@Composable
fun OverallSimilarity(
    context: Context,
    viewModel: ViewComparisonViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        Text(
            text = "Overall similarity: " + (round(viewModel.scoreData.getOverallSimilarity() * 1000) / 10).toString() + "%",
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    dimensionResource(R.dimen.title_margin)
                )
        )
    }
}

@Composable
fun Reference(
    context: Context,
    viewModel: ViewComparisonViewModel
) {
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
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, ViewLostActivity::class.java)
                        intent.putExtra(
                            IntentExtraNames.INTENT_LOST_ID,
                            viewModel.lostItemData
                        )
                        context.startActivity(intent)
                    }
                )
            },
            contentRight = {
                Text(
                    text = "Found item",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, ViewFoundActivity::class.java)
                        intent.putExtra(
                            IntentExtraNames.INTENT_FOUND_ID,
                            viewModel.foundItemData
                        )
                        context.startActivity(intent)
                    }
                )
            }
        )
        /*
        CustomComparisonField(
            centerLabel = {

            },
            contentLeft = {
                Text(
                    text = "#" + viewModel.lostItemData.itemID,
                    color = Color.Gray,
                    style = Typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            contentRight = {
                Text(
                    text = "#" + viewModel.foundItemData.itemID,
                    color = Color.Gray,
                    style = Typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        )

         */
    }
}

@Composable
fun Status(viewModel: ViewComparisonViewModel) {
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
fun ItemImage(viewModel: ViewComparisonViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomComparisonField(
            contentLeft = {
                val displayedLostImage =
                    if (viewModel.lostItemData.image.isEmpty()) ImageManager.PLACEHOLDER_IMAGE_STRING else viewModel.lostItemData.image
                // image of the item
                GlideImage(
                    model = Uri.parse(displayedLostImage),
                    contentDescription = "Lost item image",
                    modifier = Modifier
                        .weight(3f)
                        .padding(horizontal = dimensionResource(id = R.dimen.content_margin_half)),
                    alignment = Alignment.Center
                )
            },
            contentRight = {
                val displayedFoundImage =
                    if (viewModel.foundItemData.image.isEmpty()) ImageManager.PLACEHOLDER_IMAGE_STRING else viewModel.foundItemData.image
                GlideImage(
                    model = Uri.parse(displayedFoundImage),
                    contentDescription = "Found item image",
                    modifier = Modifier
                        .weight(3f)
                        .padding(horizontal = dimensionResource(id = R.dimen.content_margin_half)),
                    alignment = Alignment.Center
                )
            },
            centerLabel = {
                if (viewModel.scoreData.imageScore != null) {
                    Text(
                        text = "Image Similarity\n" + (round((viewModel.scoreData.imageScore!! / 3) * 1000) / 10).toString() + "%",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

            }
        )
    }
}

@Composable
fun ItemDetails(viewModel: ViewComparisonViewModel) {
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
        // Currently, display subcategory only
        CustomComparisonTextField(
            centerLabel = "Category",
            contentLeft = viewModel.lostItemData.subCategory,
            contentRight = viewModel.foundItemData.subCategory,
            icon = Icons.Outlined.Folder,
            isMatch = viewModel.scoreData.isCategoryCloseMatch()
        )
        HorizontalDivider(thickness = 1.dp)

        // color
        CustomComparisonField(
            centerLabel = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = "color",
                        tint = Color.Gray
                    )

                    Text(
                        text = "Color",
                        style = Typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // show the matching icon
                if (viewModel.scoreData.isColorCloseMatch()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            tint = colorResource(id = R.color.status2),
                            contentDescription = "Match",
                            modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                        )
                        Text(
                            text = "Matches",
                            style = Typography.bodyMedium,
                            color = colorResource(id = R.color.status2),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            contentLeft = {
                val colorText = viewModel.lostItemData.color.joinToString(", ")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                ) {
                    Text(
                        text = colorText,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                    ) {
                        // display colors in circles
                        for (colorString in viewModel.lostItemData.color) {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "Color",
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.content_margin))
                                    .border(
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.onBackground
                                        ),
                                        shape = CircleShape
                                    ),
                                tint = stringToColor[colorString] ?: Color.Gray
                            )
                        }
                    }
                }
            },
            contentRight = {
                val colorText = viewModel.foundItemData.color.joinToString(", ")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                ) {
                    Text(
                        text = colorText,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                    ) {
                        // display colors in circles
                        for (colorString in viewModel.foundItemData.color) {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "Color",
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.content_margin))
                                    .border(
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.onBackground
                                        ),
                                        shape = CircleShape
                                    ),
                                tint = stringToColor[colorString] ?: Color.Gray
                            )
                        }
                    }
                }
            }
        )

        HorizontalDivider(thickness = 1.dp)

        // brand (Optional)
        CustomComparisonTextField(
            centerLabel = "Brand",
            contentLeft = viewModel.lostItemData.brand.ifEmpty { "(Unknown)" },
            contentRight = viewModel.foundItemData.brand.ifEmpty { "(Unknown)" },
            icon = Icons.Outlined.Title,
            isMatch = viewModel.scoreData.isBrandCloseMatch()   // will be false if one is null
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

        // description (Optional)
        /*
        CustomComparisonTextField(
            centerLabel = "Description",
            contentLeft = viewModel.lostItemData.description.ifEmpty { "(Unknown)" },
            contentRight = viewModel.foundItemData.description.ifEmpty { "(Unknown)" },
            icon = Icons.Outlined.Description
        )
        HorizontalDivider(thickness = 1.dp)

         */
    }
}

@Composable
fun DescriptionData(
    context: Context,
    viewModel: ViewComparisonViewModel
) {
    Column {
        CustomGrayTitle(text = "Description of the Found Item")
        if (viewModel.foundItemData.description.isNotEmpty()) {
            CustomEditText(
                fieldLabel = "Description",
                fieldContent = viewModel.foundItemData.description,
                isEditable = false,
                leftIcon = Icons.Outlined.Description
            )
        } else {
            Text(
                text = "(Description is not provided)",
                color = Color.Gray,
                style = Typography.bodyMedium
            )
        }
    }
}


@Composable
fun LocationData(
    context: Context,
    viewModel: ViewComparisonViewModel
) {
    Column {
        CustomGrayTitle(text = "Location")

        // if both lost and found users did not provide a location, then display none
        if (viewModel.lostItemData.location != null || viewModel.foundItemData.location != null) {
            CustomActionText(
                text = "View location",
                onClick = {
                    viewModel.isLocationDialogShown.value = true
                },
            )
        } else {
            Text(
                text = "(Locations are not provided)",
                color = Color.Gray,
                style = Typography.bodyMedium
            )
        }

        // display the Matches logo
        if (viewModel.scoreData.isLocationCloseMatch()) {
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.content_margin_half)))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    tint = colorResource(id = R.color.status2),
                    contentDescription = "Close match",
                    modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                )
                Text(
                    text = "Close match",
                    style = Typography.bodyMedium,
                    color = colorResource(id = R.color.status2),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    CustomViewTwoLocationsDialog(
        context = context,
        isDialogShown = viewModel.isLocationDialogShown,
        selectedLocation1 = if (viewModel.lostItemData.location != null) LocationManager.pairToLatlng(
            viewModel.lostItemData.location!!
        ) else null,
        selectedLocation2 = if (viewModel.foundItemData.location != null) LocationManager.pairToLatlng(
            viewModel.foundItemData.location!!
        ) else null
    )
}

@Composable
fun SecurityQuestion(
    viewModel: ViewComparisonViewModel
) {
    // security question - whether it exists
    Column {
        CustomGrayTitle(text = "Security question")
        CustomEditText(
            fieldLabel = "Security question",
            fieldContent = if (viewModel.foundItemData.securityQuestion.isEmpty()) "No" else "Yes",
            isEditable = false,
            leftIcon = Icons.Outlined.Lock
        )
    }
}

@Composable
fun UserData(
    context: Context,
    viewModel: ViewComparisonViewModel
) {
    Column {
        CustomGrayTitle(text = "Contact user who found this item")

        // Name of the FOUND user as only lost users would see this screen
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CustomEditText(
                    fieldLabel = "User",
                    fieldContent = viewModel.foundItemData.user.firstName + ' ' + viewModel.foundItemData.user.lastName,
                    leftIcon = Icons.Outlined.AccountCircle,
                    isEditable = false
                )
            }

            // contact user button and dialog, when the user is not the current user
            if (viewModel.foundItemData.user.userID != UserManager.getUserID()) {
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

    CustomUserDialog(
        user = viewModel.foundItemData.user,
        context = context,
        isDialogShown = viewModel.isContactUserDialogShown
    )
}

@Composable
fun ClaimButton(
    context: Context,
    viewModel: ViewComparisonViewModel
) {
    // isloading state to display the loading animation
    var isLoading by remember { mutableStateOf(false) }

    // when isLoading changes, functions that uses the variable are re-composed
    if (isLoading) {
        CustomCenteredProgressbar()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
        horizontalArrangement = Arrangement.Center
    ) {
        // if the user is the owner of lost item and also the lost item has status = 0,
        // they have the power to claim the item

        // display messages if the user owns the lost item
        if (viewModel.lostItemData.user.userID == UserManager.getUserID()) {

            // if lost item status = 0 and the user is the owner of the lost item, they can claim
            // else if lost item status = 1 and the found item is the lost item's claimed item, display already claimed message
            // else, display they already claimed another item

            if (viewModel.lostItemData.status == 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomButton(
                        text = "Claim this Item",
                        type = ButtonType.FILLED,
                        enabled = !isLoading,  // if loading, disable the button
                        onClick = {
                            isLoading = true

                            // if there is a security question for the found item, prompt the user to enter it
                            if (viewModel.foundItemData.securityQuestion.isNotEmpty()) {
                                viewModel.isSecurityQuestionDialogShown.value = true
                                viewModel.securityQuestionInputError.value = ""

                            } else {
                                // create claim and close activity
                                claimItem(context = context, viewModel = viewModel)
                            }
                        },
                    )

                    Text(
                        text = "Once you have claimed an item, it cannot be deleted",
                        color = Color.Gray,
                        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin)),
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }


                // the dialog to prompt user to enter security question
                CustomInputDialog(
                    icon = Icons.Outlined.Lock,
                    title = "Security question",
                    content = viewModel.foundItemData.securityQuestion,
                    inputText = viewModel.securityQuestionAnswerFromUser,
                    isDialogShown = viewModel.isSecurityQuestionDialogShown,
                    inputPlaceholder = "Your answer...",
                    confirmButton = {
                        CustomButton(
                            text = "Claim",
                            type = ButtonType.FILLED,
                            onClick = {
                                // validate input
                                if (!viewModel.validateSecurityQuestionInput()) {
                                    return@CustomButton
                                }

                                // create claim and close activity
                                claimItem(context = context, viewModel = viewModel)
                            }
                        )
                    },
                    dismissButton = {
                        CustomButton(
                            text = "Cancel",
                            type = ButtonType.OUTLINED,
                            onClick = {
                                viewModel.isSecurityQuestionDialogShown.value = false
                                // stop loading
                                isLoading = false
                            }
                        )
                    },
                    errorMessage = viewModel.securityQuestionInputError,
                    testTag = "SecurityQuestionInput"

                )
            } else if (viewModel.claim.foundItemID == viewModel.foundItemData.itemID) {
                Text(
                    text = "You have already claimed this item.",
                    style = Typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.title_margin)),
                    textAlign = TextAlign.Center
                )

            } else {
                Text(
                    text = "You cannot claim this item as you have already claimed another item.",
                    style = Typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.title_margin)),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


// function to claim item
fun claimItem(
    context: Context,
    viewModel: ViewComparisonViewModel
) {
    // update statuses of the item
    viewModel.putClaimedItems(context = context, object : ErrorCallback {
        override fun onComplete(error: String) {
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                return
            }

            // start done activity
            val i: Intent = Intent(context, DoneActivity::class.java)
            i.putExtra(IntentExtraNames.INTENT_DONE_ACTIVITY_TITLE, "Claim Submitted")
            context.startActivity(i)

            // close current activity
            (context as Activity).finish()
        }
    })
}






