package com.example.lostandfound.ui.Search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomFoundItemPreview
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.ui.ViewClaim.ViewClaimActivity
import com.example.lostandfound.ui.ViewComparison.ViewComparisonActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import kotlin.math.round


class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: SearchViewModel by viewModels()

        // load the passed lost item
        val passedItem = intent.getParcelableExtra<LostItem>(IntentExtraNames.INTENT_LOST_ID)
        if (passedItem != null) {
            viewModel.lostItem = passedItem
        }

        setContent {
            SearchScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    SearchScreen(activity = MockActivity(), viewModel = viewModel())
}

@Composable
fun SearchScreen(activity: ComponentActivity, viewModel: SearchViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(
                        title = "Search Items",
                        activity = activity,
                        icon = Icons.Outlined.Close
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin))

                ) {
                    // includes the top tab bar and the main content
                    MainContent(viewModel = viewModel)
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: SearchViewModel) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    LaunchedEffect(Unit) {
        // is loading initially
        viewModel.isLoading.value = true

        // load lost item data of the current user from the view model
        viewModel.loadItems(
            context,
            object : Callback<Boolean> {
                override fun onComplete(result: Boolean) {
                    viewModel.isLoading.value = false

                    if (!result) {
                        // display toast message for failed data retrieval
                        Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    // display content
    if (!inPreview && viewModel.isLoading.value) {
        CustomCenteredProgressbar()

    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {
            if (viewModel.lostItem.user.userID == FirebaseUtility.getUserID()) {
                TopDescription(context = context, viewModel = viewModel)
            }
            MatchingItemsColumn(context = context, viewModel = viewModel)
        }
    }
}

@Composable
fun TopDescription(
    context: Context,
    viewModel: SearchViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.title_margin)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "We have found "
                    + viewModel.matchedFoundItems.size.toString() + " items that matches your lost item.",
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.content_margin_half)))

        // display the "We will track it for you" text if the item is currently not tracking
        if (!viewModel.lostItem.isTracking) {
            Text(
                text = "Don't see your item? ",
                style = Typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Click here and we will track it for you. ",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    viewModel.onWeWillTrackClicked(
                        object : Callback<Boolean> {
                            override fun onComplete(result: Boolean) {
                                if (!result) {
                                    Toast.makeText(
                                        context,
                                        "Failed to update track status",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }

                                // exit activity after doing this
                                Toast.makeText(
                                    context,
                                    "Your lost item is now being tracked!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                (context as Activity).finish()
                            }
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun MatchingItemsColumn(
    context: Context,
    viewModel: SearchViewModel
) {
    // if there are no data, display message
    if (viewModel.matchedFoundItems.size == 0) {
        Box(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.title_margin))
        ) {
            CustomCenterText(text = "Unfortunately we did not find any matches for your item. Perhaps it has not been found yet?")
        }

    } else {
        // for each data, display it as a preview
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
        ) {
            items(
                viewModel.matchedFoundItems

            ) { foundItemAndScore ->

                // get the found item data and score data
                val foundItemData = foundItemAndScore.first
                val scoreData = foundItemAndScore.second

                // determine if the image, details and location are considered "Close match"
                val imageCloseMatch = scoreData.isImageCloseMatch()
                val detailsCloseMatch = scoreData.isDetailsCloseMatch()
                val locationCloseMatch = scoreData.isLocationCloseMatch()

                // if lost item status = 0, then the user can claim item
                // else if lost item status = 1 and the found item is claimed, then the user can view claim
                // else, the user can only view the item
                var displayedButtonText = "View"
                if (viewModel.lostItem.user.userID == FirebaseUtility.getUserID()) {
                    if (viewModel.lostItem.status == 0) {
                        displayedButtonText = "View"

                    } else if (viewModel.claimedItem.foundItemID == foundItemData.itemID) {
                        displayedButtonText = "View claim"

                    }
                }

                CustomFoundItemPreview(
                    data = foundItemData,
                    onViewButtonClicked = {
                        // if the displayed button is view claim, redirect to view claim activity
                        if (displayedButtonText == "View claim") {
                            val intent = Intent(context, ViewClaimActivity::class.java)

                            // pass the claim of lost item
                            intent.putExtra(
                                IntentExtraNames.INTENT_CLAIM_ITEM,
                                viewModel.claimedItem
                            )
                            context.startActivity(intent)

                        } else {
                            // redirect to view comparison activity otherwise
                            val intent = Intent(context, ViewComparisonActivity::class.java)

                            // pass both the lost item and found item
                            intent.putExtra(
                                IntentExtraNames.INTENT_LOST_ID,
                                viewModel.lostItem
                            )
                            intent.putExtra(
                                IntentExtraNames.INTENT_FOUND_ID,
                                foundItemData
                            )

                            // also pass the claim item of the lost item
                            intent.putExtra(
                                IntentExtraNames.INTENT_CLAIM_ITEM,
                                viewModel.claimedItem
                            )

                            // pass the score data as well
                            intent.putExtra(
                                IntentExtraNames.INTENT_SCORE_DATA,
                                scoreData
                            )

                            Log.d("ITEM INFO", scoreData.toString())
                            context.startActivity(intent)
                        }
                    },
                    viewButtonText = displayedButtonText,
                    isImageCloseMatch = imageCloseMatch,
                    isDetailsCloseMatch = detailsCloseMatch,
                    isLocationCloseMatch = locationCloseMatch,
                    percentageSimilarity = (round((scoreData.overallScore / 3) * 1000) / 10).toString()
                )
            }
        }
    }
}


