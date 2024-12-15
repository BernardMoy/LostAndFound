package com.example.lostandfound.ui.Notifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lostandfound.BackToolbar
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.google.android.material.color.MaterialColors

class NotificationsActivity : ComponentActivity() { // Use ComponentActivity here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationsScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    NotificationsScreen(activity = MockActivity())
}

@Composable
fun NotificationsScreen(activity: ComponentActivity) {
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
                    Tabs()
                }
            }
        }
    }
}

@Composable
fun Tabs(){
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
                    onClick = { selectedTabIndex = index },   // change the selected index when clicked
                    text = { Text(text = item, color = tabColor) }
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
                    MatchingItems()

                } else {
                    // messages tab
                    Messages()
                }
            }
        }
    }
}

@Composable
fun MatchingItems(){
    Text(text = "Matching items")
}

@Composable
fun Messages() {
    Text(text = "Messages")
}