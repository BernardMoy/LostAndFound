package com.example.lostandfound.ui.Lost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.CustomLostItemPreview
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.ui.EditProfile.EditProfileViewModel
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class LostFragment : Fragment() {

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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = "Please login first to view this content.",
                                style = Typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        LostFragmentScreen()
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
    ComposeTheme {
        LostFragmentScreen()
    }
}

@Composable
fun LostFragmentScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.title_margin))
    ){
        MainContent()
    }
}

@Composable
fun MainContent(viewModel: LostFragmentViewModel = viewModel()){
    // load lost item data of the current user from the view model
    viewModel.getAllData()

    // for each data, display it as a preview
    val itemData by viewModel.itemData.observeAsState(emptyList())
    viewModel.itemData.forEach{ itemMap ->
        CustomLostItemPreview(data = itemMap)
    }
}