package com.example.lostandfound.ui.Found

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.R
import com.example.lostandfound.ui.EditProfile.EditProfileViewModel


class FoundFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply{
            setContent {
                FoundFragmentScreen()
            }
        }
        return view
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    FoundFragmentScreen()
}

@Composable
fun FoundFragmentScreen() {
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
    Text(text = "Found fragment")
}