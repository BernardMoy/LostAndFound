package com.example.lostandfound.ui.ViewReportedIssues

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
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
import com.example.lostandfound.CustomElements.CustomReportIssuePreview
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme

/*
Admin screen to view all user reported issues
 */
class ViewReportedIssuesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ViewReportedIssuesScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ViewReportedIssuesScreen(activity = MockActivity())
}

@Composable
fun ViewReportedIssuesScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "View Reported Issues", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .padding(vertical = dimensionResource(R.dimen.title_margin))

                ) {
                    // includes the top tab bar and the main content
                    MainContent()
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: ViewReportedIssuesViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // load data
    LaunchedEffect(Unit) {
        viewModel.loadData(object : ViewReportedIssuesViewModel.LoadReportedIssuesCallback {
            override fun onComplete(success: Boolean) {
                if (!success) {
                    Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    // contents
    if (viewModel.isLoading.value) {
        CustomCenteredProgressbar()
    } else if (viewModel.reportedIssueList.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin)),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.title_margin))
        ) {
            items(
                viewModel.reportedIssueList
            ) { issue ->
                CustomReportIssuePreview(issue)
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.content_margin)))
                HorizontalDivider()
            }
        }
    } else {
        CustomCenterText("There are no reported issues.")
    }
}
