package com.example.lostandfound.ui.Notifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lostandfound.BackToolbar
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme

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
                    Tabs()

                    // main content goes here
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(id = R.dimen.title_margin))
                            .verticalScroll(rememberScrollState())   // make screen scrollable
                    ){
                        MainContent()
                    }
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

    // store the tab row in a column
    Column (
        modifier = Modifier.fillMaxWidth()
    ){
        TabRow(selectedTabIndex = selectedTabIndex) {
            // iterate over the list of tabNames by the index and their item
            tabNames.forEachIndexed{index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { selectedTabIndex = index },   // change the selected index when clicked
                    text = { Text(text = item) }
                )
            }
        }
    }
}

@Composable
fun MainContent() {
    Text(text = "text")
}