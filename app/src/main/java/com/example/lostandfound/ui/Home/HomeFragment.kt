package com.example.lostandfound.ui.Home

import android.content.Context
import android.content.Intent
import android.graphics.Paint.Align
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomCard
import com.example.lostandfound.R
import com.example.lostandfound.ui.HowItWorks.HowItWorksActivity
import com.example.lostandfound.ui.NewFound.NewFoundActivity
import com.example.lostandfound.ui.NewLost.NewLostActivity
import com.example.lostandfound.ui.theme.ComposeTheme


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply {
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

    ) {
        MainContent()
    }
}

@Composable
fun MainContent(
    viewModel: HomeFragmentViewModel = viewModel()
) {
    val context = LocalContext.current

    Column {
        ImageAndButton(context = context, viewModel = viewModel)
        HowItWorksPager(context = context, viewModel = viewModel)
    }
}

@Composable
fun ImageAndButton(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = R.drawable.placeholder_image),
            contentDescription = "Background image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop  // use it to fill the screen
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = dimensionResource(
                        id = R.dimen.header_margin
                    )
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomButton(
                text = "I have lost",
                type = ButtonType.FILLED,
                onClick = {
                    val intent = Intent(context, NewLostActivity::class.java)
                    context.startActivity(intent)
                },
            )

            CustomButton(
                text = "I have found",
                type = ButtonType.TONAL,
                onClick = {
                    val intent = Intent(context, NewFoundActivity::class.java)
                    context.startActivity(intent)
                },
            )
        }
    }
}

@Composable
fun HowItWorksPager(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    // pager state
    val pagerSize = 4
    val pagerState = rememberPagerState(pageCount = { pagerSize })

    // this trigger value is set to true when the next button is clicked
    val trigger = remember {
        mutableStateOf(false)
    }

    // when the trigger value is checked, reset it and switch to next page
    LaunchedEffect(trigger.value) {
        if (trigger.value) {
            val nextPage = (pagerState.currentPage + 1) % pagerSize
            pagerState.animateScrollToPage(nextPage)  // this has to be called inside launchedEffect
            trigger.value = false
        }
    }

    Column(
        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
    ) {
        // 4 dots and the -> button
        Row(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.title_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // the 4 dots
            Row(
                modifier = Modifier.weight(1f),  // fill all remaining space
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                // for loop for each page
                for (i in 0 until pagerSize) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "Page",
                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin)),
                        tint = if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary
                        else Color.Gray
                    )
                }
            }

            // the next button
            IconButton(
                onClick = {
                    // trigger to scroll to next page
                    trigger.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = "View next",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }


        // the custom card for displaying how things works with 4 pages
        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.title_margin)
                )
            ) {
                when (page) {
                    0 -> {
                        CustomCard(
                            icon = Icons.Outlined.AddCircle,
                            title = "Add a lost item",
                            content = "You can report a lost item with the '+' button below."
                        )
                    }

                    1 -> {
                        CustomCard(
                            icon = Icons.Outlined.Search,
                            title = "Search",
                            content = "Search for existing items to look for matching ones."
                        )
                    }

                    2 -> {
                        CustomCard(
                            icon = Icons.Outlined.TrackChanges,
                            title = "Track new items",
                            content = "If not found, new found items will be automatically compared with your item."
                        )
                    }

                    3 -> {
                        CustomCard(
                            icon = Icons.Outlined.CheckCircle,
                            title = "Claim and approval",
                            content = "You can submit a claim and wait for the found user's approval."
                        )
                    }
                }
            }
        }

        // the action text to go to view how it works activity
        Box(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.title_margin),
                    vertical = dimensionResource(id = R.dimen.content_margin)
                )
                .align(Alignment.End)
        ) {
            CustomActionText(
                text = "View how it works",
                onClick = {
                    val intent = Intent(context, HowItWorksActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}