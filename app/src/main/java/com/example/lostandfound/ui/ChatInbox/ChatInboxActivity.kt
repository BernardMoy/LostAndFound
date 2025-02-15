package com.example.lostandfound.ui.ChatInbox

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomChatCard
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.R
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class ChatInboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the view model here
        val viewModel: ChatInboxViewModel by viewModels()

        // load the passed user data (Recipient user) into view model
        val passedItem = intent.getParcelableExtra<User>(IntentExtraNames.INTENT_CHAT_USER)
        if (passedItem != null) {
            viewModel.chatUser = passedItem
        }


        // load messages on create
        loadMessages(context = this, viewModel = viewModel)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInboxScreen(activity: ComponentActivity, viewModel: ChatInboxViewModel) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    TopAppBar(
                        title = {
                            UserData(context = activity, viewModel = viewModel)
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                // default behaviour is to exit the activity
                                activity.finish()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
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


    Column {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (viewModel.isLoading.value) {
                CustomCenteredProgressbar()

            } else {
                Messages(context = context, viewModel = viewModel)
            }
        }
        SendBar(context = context, viewModel = viewModel)
    }

}

// display user info in title
@Composable
fun UserData(
    context: Context,
    viewModel: ChatInboxViewModel
) {
    // user avatar and user name
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.title_margin))
            .padding(vertical = dimensionResource(id = R.dimen.content_margin_half)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin)),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
    }
}


// display messages
@Composable
fun Messages(
    context: Context,
    viewModel: ChatInboxViewModel
) {
    // liststate to allow the list to scroll to bottom
    val listState = rememberLazyListState()

    // make it scroll to bottom when triggered
    LaunchedEffect(viewModel.triggerScrollToBottom.value) {
        if (viewModel.triggerScrollToBottom.value) {
            listState.animateScrollToItem(viewModel.chatMessageList.size - 1)
            viewModel.triggerScrollToBottom.value = false
        }
    }

    // function to scroll to bottom immediately, for initial load
    LaunchedEffect(viewModel.triggerScrollToBottomInstantly.value) {
        if (viewModel.triggerScrollToBottomInstantly.value && viewModel.chatMessageList.size > 0) {
            listState.scrollToItem(viewModel.chatMessageList.size - 1)
            viewModel.triggerScrollToBottomInstantly.value = false
        }
    }

    // trigger when the last new message is NOT on the screen, showing the new messages button
    LaunchedEffect(viewModel.chatMessageList.size) {
        // get the latest visible element
        val latestVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

        // if the latest visible index is the new message index -2, then force scroll to bottom
        // because we can assume the user is not viewing past messages
        if (latestVisibleIndex == viewModel.chatMessageList.size - 2) {
            // force scroll
            viewModel.triggerScrollToBottom.value = true

        }
        // if the latest visible index is less than the new message 's index, show the button
        else if (latestVisibleIndex < viewModel.chatMessageList.size - 2) {
            viewModel.isNewMessageButtonShown.value = true  // else, show button

        }
    }


    LazyColumn(
        // it is assigned all the remaining height from the MainContent() composable
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.title_margin),
                vertical = dimensionResource(id = R.dimen.content_margin)
            ),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        // for each preview in the view model, display it
        items(viewModel.chatMessageList) { chatMessage ->
            CustomChatCard(chatMessage = chatMessage)

        }
    }


    // the button to show new messages, which is overlapping
    if (!viewModel.isInitialLoad.value && viewModel.isNewMessageButtonShown.value) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomButton(
                text = "New messages",
                type = ButtonType.TONAL,
                onClick = {
                    viewModel.triggerScrollToBottom.value = true

                    // hide the button
                    viewModel.isNewMessageButtonShown.value = false
                },
                small = true,
            )
        }
    }
}


// display send message bar
@Composable
fun SendBar(
    context: Context,
    viewModel: ChatInboxViewModel
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.content_margin))
    ) {
        // type your message box
        OutlinedTextField(
            value = viewModel.typedText.value,
            placeholder = {
                Text(
                    text = "Type your message...",
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            },
            onValueChange = { newText ->
                viewModel.typedText.value = newText
            },
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = dimensionResource(id = R.dimen.content_margin)
                ),
            colors = TextFieldDefaults.colors(
                // for enabled (Editable) text
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,

                // for disabled (non editable) text
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,

                // for the color of the border
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline,
            )
        )

        // send button
        IconButton(
            onClick = {
                // first validate message
                if (!viewModel.validateMessage()) {
                    return@IconButton
                }

                viewModel.sendMessage(object : SendMessageCallback {
                    override fun onComplete(result: Boolean) {
                        if (!result) {
                            Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }

                        // do nothing when send message successful
                    }
                })
            },
            modifier = Modifier  // add the background circle
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    ),
                    shape = CircleShape
                )
                .padding(3.dp)  // content margin half half
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = "Send message",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


fun loadMessages(
    context: Context,
    viewModel: ChatInboxViewModel
) {
    viewModel.isLoading.value = true
    viewModel.fetchMessagePreviews(object : FetchMessageCallback {
        override fun onComplete(result: Boolean) {
            viewModel.isLoading.value = false

            if (!result) {
                Toast.makeText(context, "Fetching messages failed", Toast.LENGTH_SHORT).show()
                return
            }

            // initially, scroll to bottom. This will only be triggered once
            if (viewModel.isInitialLoad.value) {
                viewModel.triggerScrollToBottomInstantly.value = true

                // turn off is initial load here
                viewModel.isInitialLoad.value = false
            }
        }
    })
}




