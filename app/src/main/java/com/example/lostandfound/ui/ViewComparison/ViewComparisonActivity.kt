package com.example.lostandfound.ui.ViewComparison

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomViewLocationDialog
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
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
                    BackToolbar(title = "View lost item", activity = activity)
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
            /*
            Reference(viewModel = viewModel)
            Status(viewModel = viewModel)
            ItemImage(viewModel = viewModel)
            ItemDetails(viewModel = viewModel)
            LocationData(viewModel = viewModel)
            UserData(viewModel = viewModel)


             */
        }
    }
}
/*
@Composable
fun Reference(viewModel: ViewComparisonViewModel){
    Row(
        modifier = Modifier.fillMaxWidth()
    ){
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
fun Status(viewModel: ViewComparisonViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Status: " + lostStatusText[viewModel.itemData.status],
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
fun ItemImage(viewModel: ViewComparisonViewModel){
    Row(
        modifier = Modifier.fillMaxWidth()
    ){
        // image of the item
        GlideImage(
            model = Uri.parse(viewModel.itemData.image),
            contentDescription = "Item image",
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.Center
        )
    }
}

@Composable
fun ItemDetails(viewModel: ViewComparisonViewModel){
    Column(
    ){
        CustomGrayTitle(text = "Item details")

        // Name of item
        CustomEditText(
            fieldLabel = "Item name",
            fieldContent = viewModel.itemData.itemName,
            leftIcon = Icons.Outlined.Edit,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)

        // category and subcategory
        CustomEditText(fieldLabel = "Category",
            fieldContent = viewModel.itemData.category + ", " + viewModel.itemData.subCategory,
            leftIcon = Icons.Outlined.Folder,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)

        // date and time
        CustomEditText(fieldLabel = "Date and time",
            fieldContent = DateTimeManager.dateTimeToString(viewModel.itemData.dateTime),
            leftIcon = Icons.Outlined.CalendarMonth,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)

        // color
        CustomEditText(fieldLabel = "Color",
            fieldContent = viewModel.itemData.color,
            leftIcon = Icons.Outlined.Palette,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)

        // brand (Optional)
        CustomEditText(fieldLabel = "Brand",
            fieldContent = if (viewModel.itemData.brand.isNotEmpty()) viewModel.itemData.brand else "Not provided",
            leftIcon = Icons.Outlined.Title,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)

        // description (Optional)
        CustomEditText(fieldLabel = "Description",
            fieldContent = if (viewModel.itemData.description.isNotEmpty()) viewModel.itemData.description else "Not provided",
            leftIcon = Icons.Outlined.Description,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
fun LocationData(
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

    CustomViewLocationDialog(
        isDialogShown = viewModel.isLocationDialogShown,
        selectedLocation = LocationManager.pairToLatlng(
            viewModel.itemData.location
        )
    )
}

@Composable
fun UserData(
    viewModel: ViewComparisonViewModel
){
    Column(
    ) {
        CustomGrayTitle(text = "User information")

        // Name of user
        CustomEditText(
            fieldLabel = "User",
            fieldContent = viewModel.userName,
            leftIcon = Icons.Outlined.AccountCircle,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)

        // category and subcategory
        CustomEditText(
            fieldLabel = "Time posted",
            fieldContent = DateTimeManager.dateTimeToString(viewModel.itemData.timePosted),
            leftIcon = Icons.Outlined.CalendarMonth,
            isEditable = false
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

 */

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





