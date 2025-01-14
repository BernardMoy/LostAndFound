package com.example.lostandfound.ui.Notifications

import android.content.Context
import android.os.Bundle
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.Data.statusColor
import com.example.lostandfound.R
import com.example.lostandfound.ui.Profile.ProfileViewModel
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

class NotificationsActivity : ComponentActivity() { // Use ComponentActivity here

    val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationsScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    NotificationsScreen(activity = MockActivity(), viewModel = viewModel())
}

@Composable
fun NotificationsScreen(activity: ComponentActivity, viewModel: NotificationsViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Notifications", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                ) {
                    // includes the top tab bar and the main content
                    Tabs(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Tabs(viewModel: NotificationsViewModel){
    val context: Context = LocalContext.current

    val tabNames = listOf("Matching items", "Messages")

    // a variable to store the selected tab index (0 or 1)
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    // variable to store pager state
    var pagerState = rememberPagerState {
        tabNames.size    // set the initial amount of pages
    }

    // make the page content change with the tab selected
    // if the selected tab index changes, then scroll to the corresponding page
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    // if the pagerstate.currentPage changes, then change the selected index
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        // avoid updating the page index during scrolling
        // applies when there are more than 2 tabs
        if (!pagerState.isScrollInProgress){
            selectedTabIndex = pagerState.currentPage
        }
    }

    // store the tab row in a column
    Column (
        modifier = Modifier.fillMaxWidth()
    ){
        // displays the tab row
        TabRow(selectedTabIndex = selectedTabIndex) {
            // iterate over the list of tabNames by the index and their item
            tabNames.forEachIndexed{index, item ->

                val isSelected = index == selectedTabIndex

                // display the selected tab as primary color
                val tabColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray

                // properties of a tab
                Tab(
                    selected = isSelected,
                    onClick = {
                        selectedTabIndex = index  // change the selected index when clicked

                        // depending on the index, turn off its unread button
                        if (index == 0){
                            viewModel.isMatchingItemsUnread.value = false
                        }
                        if (index == 1){
                            viewModel.isMessagesUnread.value = false
                        }

                              },
                    text = {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                        ){
                            Text(
                                text = item,
                                color = tabColor,
                                style = Typography.bodyMedium,
                            )

                            // the unread red dot
                            if (index == 0){
                                if (viewModel.isMatchingItemsUnread.value){
                                    Icon(
                                        imageVector = Icons.Filled.Circle,
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = "Unread",
                                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                                    )
                                }
                            }

                            if (index == 1){
                                if (viewModel.isMessagesUnread.value){
                                    Icon(
                                        imageVector = Icons.Filled.Circle,
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = "Unread",
                                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                                    )
                                }
                            }

                        }
                    }
                )
            }
        }

        // displays the content below tab row
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Top

        ) { index ->
            Box(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.title_margin))
                    .verticalScroll(rememberScrollState()),
            ){
                if (index == 0){
                    // matching items tab
                    MatchingItems(context = context, viewModel = viewModel)

                } else {
                    // messages tab
                    Messages(context = context, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MatchingItems(
    context: Context,
    viewModel: NotificationsViewModel
){
    Text(text = "Matching items")

}

@Composable
fun Messages(
    context: Context,
    viewModel: NotificationsViewModel
) {
    Text(text = "Messages")

}