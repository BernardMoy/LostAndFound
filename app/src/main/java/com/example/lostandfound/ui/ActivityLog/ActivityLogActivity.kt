package com.example.lostandfound.ui.ActivityLog

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.lostandfound.CustomElements.CustomActivityLogItemPreview
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme


class ActivityLogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ActivityLogScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ActivityLogScreen(activity = MockActivity())
}

@Composable
fun ActivityLogScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Activity Log", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin)) // make screen scrollable
                ) {
                    // content goes here
                    MainContent()
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: ActivityLogViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // fetch data
    LaunchedEffect(Unit) {
        loadData(context = context, viewModel = viewModel)
    }

    if (!inPreview && viewModel.isLoading.value) {
        CustomCenteredProgressbar()
    } else if (viewModel.itemData.size == 0) {
        CustomCenterText(text = "Your activity log is empty.")
    } else {
        ActivityLogItems(context = context, viewModel = viewModel)
    }

}

@Composable
fun ActivityLogItems(
    context: Context,
    viewModel: ActivityLogViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        items(viewModel.itemData) { activityLogItem ->
            CustomActivityLogItemPreview(activityLogItem = activityLogItem)
        }
    }
}


// function to load data on create
fun loadData(
    context: Context,
    viewModel: ActivityLogViewModel
) {
    viewModel.isLoading.value = true

    viewModel.fetchActivityLogItems(
        object : FetchActivityLogCallback {
            override fun onComplete(result: Boolean) {
                viewModel.isLoading.value = false
                if (!result) {
                    Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
                    return
                }
                // do nothing when successful
            }
        }
    )
}