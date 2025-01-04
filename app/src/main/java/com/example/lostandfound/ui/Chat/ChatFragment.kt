package com.example.lostandfound.ui.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.ui.EditProfile.EditProfileViewModel
import com.example.lostandfound.ui.Lost.LostFragmentScreen
import com.example.lostandfound.ui.theme.ComposeTheme


class ChatFragment : Fragment() {
    // variable to keep track of whether the user is logged in
    val isLoggedIn = mutableStateOf(FirebaseUtility.isUserLoggedIn())

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
                        ChatFragmentScreen()
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
    ChatFragmentScreen()
}

@Composable
fun ChatFragmentScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.title_margin))
    ){
        MainContent()
    }
}

@Composable
fun MainContent(viewModel: EditProfileViewModel = viewModel()){
    Text(text = "Chat fragment")
}