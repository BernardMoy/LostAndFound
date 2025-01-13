package com.example.lostandfound.ui.Chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomChatInboxPreview
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.ui.ChatInbox.ChatInboxViewModel
import com.example.lostandfound.ui.ChatInbox.FetchMessageCallback
import com.example.lostandfound.ui.EditProfile.EditProfileViewModel
import com.example.lostandfound.ui.Found.FoundFragmentViewModel
import com.example.lostandfound.ui.Lost.LostFragmentScreen
import com.example.lostandfound.ui.theme.ComposeTheme


class ChatFragment : Fragment() {
    // variable to keep track of whether the user is logged in
    val isLoggedIn = mutableStateOf(FirebaseUtility.isUserLoggedIn())

    // create the view model here
    val viewModel: ChatFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply{
            setContent {
                ComposeTheme{
                    // check if user is logged in
                    if (!isLoggedIn.value){
                        CustomCenterText(text = "Please login first to view this content.")
                    } else {
                        ChatFragmentScreen(viewModel = viewModel)
                    }
                }
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()

        // update the user's log in status
        isLoggedIn.value = FirebaseUtility.isUserLoggedIn()
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    ChatFragmentScreen(viewModel = viewModel())
}

@Composable
fun ChatFragmentScreen(viewModel: ChatFragmentViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.title_margin))
    ){
        MainContent(viewModel = viewModel)
    }
}

@Composable
fun MainContent(viewModel: ChatFragmentViewModel){
    val context = LocalContext.current

    // load the chat inboxes on create
    LaunchedEffect(Unit) {
        refreshChatInboxes(context = context, viewModel = viewModel)
    }

    if (viewModel.isLoading.value){
        CustomCenteredProgressbar()
    } else {
        ChatInboxes(context = context, viewModel = viewModel)
    }
}

@Composable
fun ChatInboxes(
    context: Context,
    viewModel: ChatFragmentViewModel
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        items(
            viewModel.chatInboxPreviewList
        ){ chatInboxPreview ->
            // display each preview
            CustomChatInboxPreview(
                context = context,
                chatInboxPreview = chatInboxPreview
            )
            HorizontalDivider(thickness = 1.dp)
        }
    }
}

// function to load data
fun refreshChatInboxes(
    context: Context,
    viewModel: ChatFragmentViewModel
){
    viewModel.isLoading.value = true

    viewModel.loadData(object: ChatInboxPreviewCallback{
        override fun onComplete(result: Boolean) {
            viewModel.isLoading.value = false

            if (!result){
                Toast.makeText(context, "Fetching chats failed", Toast.LENGTH_SHORT).show()
                return
            }

            // do nothing when successful

        }
    })
}


