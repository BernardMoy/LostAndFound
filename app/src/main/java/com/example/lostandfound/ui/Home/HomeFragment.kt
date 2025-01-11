package com.example.lostandfound.ui.Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomCard
import com.example.lostandfound.R
import com.example.lostandfound.ui.EditProfile.EditProfileViewModel
import com.example.lostandfound.ui.ViewComparison.ViewComparisonActivity
import com.example.lostandfound.ui.theme.ComposeTheme


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply{
            setContent {
                ComposeTheme {
                    HomeFragmentScreen()
                }
            }
        }
        return view
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    ComposeTheme {
        HomeFragmentScreen()
    }
}

@Composable
fun HomeFragmentScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = dimensionResource(id = R.dimen.title_margin))
    ){
        MainContent()
    }
}

@Composable
fun MainContent(
    viewModel: HomeFragmentViewModel = viewModel()
){
    val context = LocalContext.current

    HowItWorksPager(context = context, viewModel = viewModel)
}

@Composable
fun HowItWorksPager(
    context: Context,
    viewModel: HomeFragmentViewModel
){
    Column (
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ){
        // the custom card for displaying how things works with 4 pages
        val pagerState = rememberPagerState(pageCount = {4})
        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.title_margin)
                )
            ){
                CustomCard(text = "Page")
            }
        }

        HorizontalDivider(thickness = 1.dp)
    }
}