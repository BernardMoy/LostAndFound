package com.example.lostandfound.ui.ViewClaimList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomClaimPreview
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.ClaimPreview
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.R
import com.example.lostandfound.ui.ViewClaim.ViewClaimActivity
import com.example.lostandfound.ui.ViewLost.ViewLostActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

class ViewClaimListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: ViewClaimListViewModel by viewModels()

        // load the passed intent data into the view model
        // found item is passed here instead of a list of claim items
        // to allow the refresh button to work in this activity
        val passedFoundItem = intent.getParcelableExtra<FoundItem>(IntentExtraNames.INTENT_FOUND_ID)
        if (passedFoundItem != null) {
            viewModel.foundItem = passedFoundItem
        }

        setContent {
            ViewClaimListScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ViewClaimListScreen(activity = MockActivity(), viewModel = ViewClaimListViewModel())
}

@Composable
fun ViewClaimListScreen(activity: ComponentActivity, viewModel: ViewClaimListViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "View Claims", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin))
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
fun MainContent(viewModel: ViewClaimListViewModel) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    LaunchedEffect(Unit) {
        refreshData(context, viewModel)
    }

    // display content
    if (!inPreview && viewModel.isLoading.value) {
        CustomCenteredProgressbar()

    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {
            RefreshButton(context = context, viewModel = viewModel)
            ClaimPreviewsColumn(context = context, viewModel = viewModel)
        }
    }
}

@Composable
fun ClaimPreviewsColumn(
    context: Context,
    viewModel: ViewClaimListViewModel
) {
    // it wont have no data
    // for each data, display it as a preview
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
    ) {
        items(
            viewModel.claimPreviewList

        ) { claimPreview ->
            // load claim preview
            CustomClaimPreview(
                claimPreview = claimPreview,
                onViewButtonClicked = {
                    // get the claim item from preview
                    val claimItem: Claim = claimPreview.claimItem

                    // start view claim activity
                    val intent = Intent(context, ViewClaimActivity::class.java)

                    // pass the claim item object to the activity
                    intent.putExtra(
                        IntentExtraNames.INTENT_CLAIM_ITEM,
                        claimItem
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}


@Composable
fun RefreshButton(
    context: Context,
    viewModel: ViewClaimListViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.content_margin)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // display the text at the start
        Text(
            text = "Claims to your found item",
            style = Typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f) // make the refresh button go to the end by taking up all available space

        )
        IconButton(
            onClick = {
                // refresh the list - manually (by now)
                refreshData(context, viewModel)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Reload",
                modifier = Modifier.size(dimensionResource(id = R.dimen.image_button_size)),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// method to refresh claim preview data from the given found item
fun refreshData(
    context: Context,
    viewModel: ViewClaimListViewModel
) {
    // set loading true
    viewModel.isLoading.value = true

    // load lost item data of the current user from the view model
    viewModel.getAllData(object : Callback<Boolean> {
        override fun onComplete(result: Boolean) {
            viewModel.isLoading.value = false

            if (!result) {
                // display toast message for failed data retrieval
                Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
            }
        }
    })
}

