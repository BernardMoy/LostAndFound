package com.example.lostandfound.ui.Search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomFoundItemPreview
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.R
import com.example.lostandfound.ui.ViewFound.ViewFoundActivity
import com.example.lostandfound.ui.theme.ComposeTheme


class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: SearchViewModel by viewModels()

        // load the passed lost item
        val passedItem = intent.getParcelableExtra<LostItem>(IntentExtraNames.INTENT_LOST_ID)
        if (passedItem != null){
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
                    BackToolbar(title = "Search Items", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin))
                        .verticalScroll(rememberScrollState())
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
        viewModel.loadItems(object: Callback<Boolean> {
            override fun onComplete(result: Boolean) {
                viewModel.isLoading.value = false

                if (!result){
                    // display toast message for failed data retrieval
                    Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // display content
    if (viewModel.isLoading.value){
        CustomCenteredProgressbar()

    } else {
        MatchingItemsColumn(context = context, viewModel = viewModel)
    }
}


@Composable
fun MatchingItemsColumn(
    context: Context,
    viewModel: SearchViewModel
){
    // if there are no data, display message
    if (viewModel.matchedFoundItems.size == 0){
        Box(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.title_margin))
        ){
            CustomCenterText(text = "Unfortunately we did not find any matches for your item. Perhaps it has not been found yet?")
        }

    } else {
        // for each data, display it as a preview
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
        ){
            items(
                viewModel.matchedFoundItems

            ) { itemData ->
                CustomFoundItemPreview(
                    data = itemData,
                    onViewButtonClicked = {
                        // start view item activity
                        val intent = Intent(context, ViewFoundActivity::class.java)

                        // pass only the item id as the extra value
                        intent.putExtra(
                            IntentExtraNames.INTENT_FOUND_ID,
                            itemData
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}


