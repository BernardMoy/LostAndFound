package com.example.lostandfound.ui.HowItWorks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomCard
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomPopupText
import com.example.lostandfound.R
import com.example.lostandfound.ui.ReportIssue.HowItWorksViewModel
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


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
                    BackToolbar(title = "How it works and FAQs", activity = activity)
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
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.title_margin))
    ){
        addItem(viewModel = viewModel)
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        searchItem(viewModel = viewModel)
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        trackItem(viewModel = viewModel)
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        approveItem(viewModel = viewModel)
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        FAQs(viewModel = viewModel)
    }

}

@Composable
fun addItem(viewModel: HowItWorksViewModel){
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ){
        Icon(
            imageVector = Icons.Outlined.AddCircle,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Add item",
            modifier = Modifier.size(dimensionResource(R.dimen.profile_image_size))
        )

        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = "Report a New Item",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "You can report a new lost or found item through the buttons at the home page.",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "You can see all your lost and found items in the tabs at the bottom bar:",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // show an image illustrating the bottom bar
            Image(
                painter = painterResource(R.drawable.bottom_fragment),
                contentDescription = "Bottom fragment",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
fun searchItem(viewModel: HowItWorksViewModel){
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ){
        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = "Search for your Lost Items",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "You can search for possible matching items for your lost item. ",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "You can search for items using the 'View comparison' button when viewing your lost item:",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // show the above button with no functionality
            CustomButton(
                text = "View Comparison",
                type = ButtonType.FILLED,
                onClick = {},
                small = true
            )
        }

        Icon(
            imageVector = Icons.Outlined.Search,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Search item",
            modifier = Modifier.size(dimensionResource(R.dimen.profile_image_size))
        )
    }
}

@Composable
fun trackItem(viewModel: HowItWorksViewModel){
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ){
        Icon(
            imageVector = Icons.Outlined.TrackChanges,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Track item",
            modifier = Modifier.size(dimensionResource(R.dimen.profile_image_size))
        )

        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = "Track your Items",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "You can track or untrack an item when viewing your lost item. ",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "You will receive notification if a new found item matches one of your tracked item.",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Tracking lost items are indicated by:",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // show the tracking logo
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ){
                Icon(
                    imageVector = Icons.Outlined.TrackChanges,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = "Status of item",
                    modifier = Modifier.width(16.dp)
                )

                Text(
                    text = "Tracking",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun approveItem(viewModel: HowItWorksViewModel){
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ){
        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = "Claim and Approve Claims",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "You can make a claim to a found item, where the other user can choose to approve your item.",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "You can make a claim by viewing an item in the search screen.",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Claim Item",
            modifier = Modifier.size(dimensionResource(R.dimen.profile_image_size))
        )
    }
}

@Composable
fun FAQs(viewModel: HowItWorksViewModel){
    Column (
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ){
        CustomGrayTitle(text = "Frequently Asked Questions")

        CustomPopupText(
            title = "Can I delete my posted items?",
            content = buildAnnotatedString {
                append(
                    "You can delete your lost item as long as you "
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                    append("haven't made a claim")
                }
                append(
                    " on an item. You can also delete your found item if it has "
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                    append("not been claimed")
                }
                append(
                    "."
                )
            }
        )

        CustomPopupText(
            title = "How do I contact a user?",
            content = buildAnnotatedString {
                append(
                    "You can contact a user in the "
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                    append("'User information section'")
                }
                append(
                    " when viewing an item. You will also find your chat history in the chat tab at the bottom bar at the home page."
                )
            }
        )

        CustomPopupText(
            title = "How many claims can I approve?",
            content = buildAnnotatedString {
                append(
                    "Unfortunately you can only approve "
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                    append("one claim.")
                }
            }
        )

        CustomPopupText(
            title = "Can I un-approve a claim or unclaim an item?",
            content = buildAnnotatedString {
                append("We are sorry, the app ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                    append("doesn't")
                }
                append(" support these features yet.")
            }
        )
    }
}



