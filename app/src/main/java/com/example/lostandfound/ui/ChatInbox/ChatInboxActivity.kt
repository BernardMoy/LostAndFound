package com.example.lostandfound.ui.ChatInbox

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.R
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.ui.ViewLost.ViewLostViewModel
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class ChatInboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: ChatInboxViewModel by viewModels()

        // load the passed user data (Recipient user) into view model
        val passedItem = intent.getParcelableExtra<User>(IntentExtraNames.INTENT_CHAT_USER)
        if (passedItem != null){
            viewModel.chatUser = passedItem
        }

        setContent {
            ChatInboxScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ChatInboxScreen(activity = MockActivity(), viewModel = viewModel())
}

@Composable
fun ChatInboxScreen(activity: ComponentActivity, viewModel: ChatInboxViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Chat Inbox", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
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
fun MainContent(viewModel: ChatInboxViewModel) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current


    Column{
        UserData(context = context, viewModel = viewModel)
        Messages(context = context, viewModel = viewModel)
        SendBar(context = context, viewModel = viewModel)
    }

}

// display brief user info on top
@Composable
fun UserData(
    context: Context,
    viewModel: ChatInboxViewModel
){
    // user avatar and user name
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.title_margin))
            .padding(vertical = dimensionResource(id = R.dimen.content_margin_half)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin)),
        verticalAlignment = Alignment.CenterVertically
    ){
        // user avatar
        val avatar = ImageManager.stringToUri(context, viewModel.chatUser.avatar)
        Image(
            painter = if (avatar != null) rememberAsyncImagePainter(
                model = ImageManager.stringToUri(context, viewModel.chatUser.avatar)
            ) else painterResource(id = R.drawable.profile_icon),
            contentDescription = "User avatar",
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_button_size))
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape
                ),
        )

        // user name
        Text(
            text = viewModel.chatUser.firstName + ' ' + viewModel.chatUser.lastName,
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

    HorizontalDivider(thickness = 1.dp)
}


// display messages
@Composable
fun Messages(
    context: Context,
    viewModel: ChatInboxViewModel
){

}

// display send message bar
@Composable
fun SendBar(
    context: Context,
    viewModel: ChatInboxViewModel
){

}



