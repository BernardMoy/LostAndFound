package com.example.lostandfound.ui.HowItWorks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomPopupText
import com.example.lostandfound.R
import com.example.lostandfound.ui.ReportIssue.HowItWorksViewModel
import com.example.lostandfound.ui.theme.ComposeTheme


class HowItWorksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HowItWorksScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    HowItWorksScreen(activity = MockActivity())
}

@Composable
fun HowItWorksScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "How it works", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(R.dimen.title_margin))
                        .verticalScroll(rememberScrollState())
                ) {
                    // includes the top tab bar and the main content
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MainContent(viewModel: HowItWorksViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // content
    Column(){
        FAQs(viewModel = viewModel)
    }

}

@Composable
fun FAQs(viewModel: HowItWorksViewModel){
    CustomPopupText(
        title = "Can I delete my posted items?",
        content = "You can delete your lost item as long as you haven't made a claim on an item. You can also delete your found item if it has not been claimed."
    )

    CustomPopupText(
        title = "How do I contact a user?",
        content = "You can contact a user in the 'User information' section when viewing an item. You will also find your chat history in the chat tab at the bottom bar at the home page."
    )

    CustomPopupText(
        title = "How many claims can I approve?",
        content = "Unfortunately you can only approve one claim."
    )

    CustomPopupText(
        title = "Can I un-approve a claim or unclaim an item?",
        content = "We are sorry, the app doesn't support these features yet."
    )
}



