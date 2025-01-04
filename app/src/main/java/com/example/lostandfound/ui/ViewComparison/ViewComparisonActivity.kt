package com.example.lostandfound.ui.ViewComparison

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.HorizontalDivider
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
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomComparisonField
import com.example.lostandfound.CustomElements.CustomComparisonTextField
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomViewLocationDialog
import com.example.lostandfound.CustomElements.CustomViewTwoLocationsDialog
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.Data.lostStatusText
import com.example.lostandfound.Data.statusColor
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.LocationManager
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class ViewComparisonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: ViewComparisonViewModel by viewModels()

        // load the passed intent data into the view model
        val passedLostItem = intent.getParcelableExtra<LostItem>(IntentExtraNames.INTENT_LOST_ID)
        val passedFoundItem = intent.getParcelableExtra<FoundItem>(IntentExtraNames.INTENT_LOST_ID)
        if (passedLostItem != null){
            viewModel.lostItemData = passedLostItem
        }
        if (passedFoundItem != null){
            viewModel.foundItemData = passedFoundItem
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
fun MainContent(viewModel: ViewComparisonViewModel) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    LaunchedEffect(Unit) {
        loadData(context, viewModel)
    }

    // display content
    if (viewModel.isLoading.value) {
        CustomCenteredProgressbar()

    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ){
            Reference(viewModel = viewModel)
            Status(viewModel = viewModel)
            ItemImage(viewModel = viewModel)
            ItemDetails(viewModel = viewModel)
            LocationData(context = context, viewModel = viewModel)
            UserData(viewModel = viewModel)

        }
    }
}

@Composable
fun Reference(viewModel: ViewComparisonViewModel){
    Row(
        modifier = Modifier.fillMaxWidth()
    ){
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
                    fontWeight = FontWeight.Bold
                )
            },
            contentRight = {
                Text(
                    text = foundStatusText[viewModel.foundItemData.status] ?: "",
                    style = Typography.bodyMedium,
                    color = colorResource(
                        id = statusColor[viewModel.foundItemData.status] ?: R.color.status0
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemImage(viewModel: ViewComparisonViewModel){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
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
            centerLabel = {
                Text(
                    text = "Similarity\n" + "20%",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

@Composable
fun ItemDetails(viewModel: ViewComparisonViewModel){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ){
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
        CustomComparisonTextField(
            centerLabel = "Category",
            contentLeft = viewModel.lostItemData.color,
            contentRight = viewModel.foundItemData.color,
            icon = Icons.Outlined.Palette
        )
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
    viewModel: ViewComparisonViewModel
){
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
    viewModel: ViewComparisonViewModel
){
    Column(
    ) {
        CustomGrayTitle(text = "Contact user")

        // Name of the FOUND user as only lost users would see this screen
        CustomEditText(
            fieldLabel = "User",
            fieldContent = viewModel.foundUserName,
            leftIcon = Icons.Outlined.AccountCircle,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)
    }
}


// function to load data, called when the activity is created
fun loadData(
    context: Context,
    viewModel: ViewComparisonViewModel
){
    // is loading initially
    viewModel.isLoading.value = true

    // load lost item data of the current user from the view model
    viewModel.getUserName(object : com.example.lostandfound.ui.ViewComparison.Callback<Boolean>{
        override fun onComplete(result: Boolean) {
            viewModel.isLoading.value = false

            if (!result){
                // display toast message for failed data retrieval
                Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
            }
        }
    })
}





