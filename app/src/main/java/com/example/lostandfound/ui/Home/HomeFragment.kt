package com.example.lostandfound.ui.Home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomCard
import com.example.lostandfound.CustomElements.CustomLostItemPreviewSmall
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.CustomElements.CustomTooltipBox
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.AnimationManager
import com.example.lostandfound.Utility.AutoLoadingManager
import com.example.lostandfound.ui.HowItWorks.HowItWorksActivity
import com.example.lostandfound.ui.Login.LoginActivity
import com.example.lostandfound.ui.NewFound.NewFoundActivity
import com.example.lostandfound.ui.NewLost.NewLostActivity
import com.example.lostandfound.ui.ViewLost.ViewLostActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import kotlinx.coroutines.delay

/*
The landing page.
The latest lost item and statistics are only displayed when a user is logged in
Else it displays a card to prompt the user to login
 */
class HomeFragment : Fragment() {

    // create the view model here
    val viewModel: HomeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())

        view.apply {
            setContent {
                ComposeTheme {
                    HomeFragmentScreen(viewModel = viewModel)
                }
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        // update the user's log in status
        viewModel.isLoggedIn.value = UserManager.isUserLoggedIn()

        // refresh the data everytime the screen is reloaded
        if (AutoLoadingManager.autoLoadingEnabled.value) {
            loadRecentlyLostItem(requireContext(), viewModel)
            loadFoundItems(requireContext(), viewModel)
        }

        // check if display the welcome user dialog
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesNames.NAME_SHOW_WELCOME_MESSAGE,
            Context.MODE_PRIVATE
        )
        viewModel.isWelcomeDialogShown.value =
            sharedPreferences.getBoolean(SharedPreferencesNames.SHOW_WELCOME_MESSAGE_VALUE, false)

    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    ComposeTheme {
        HomeFragmentScreen(viewModel = viewModel())
    }
}

@Composable
fun HomeFragmentScreen(viewModel: HomeFragmentViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        MainContent(viewModel = viewModel)
    }
}

@Composable
fun MainContent(
    viewModel: HomeFragmentViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.verticalScroll(state = ScrollState(0))
    ) {
        ImageAndButton(context = context, viewModel = viewModel)
        HowItWorksPager(context = context, viewModel = viewModel)
        if (viewModel.isLoggedIn.value) {
            RecentlyLostItem(context = context, viewModel = viewModel)
            FoundItemData(context = context, viewModel = viewModel)
        } else {
            LargeLoginButton(context = context, viewModel = viewModel)
        }
    }

    // the welcome dialog
    val spUser =
        context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)
    val userName =
        spUser.getString(SharedPreferencesNames.USER_FIRSTNAME, "") + " " + spUser.getString(
            SharedPreferencesNames.USER_LASTNAME,
            ""
        )

    CustomTextDialog(
        icon = Icons.Outlined.WavingHand,
        title = "Welcome, $userName!",
        content = "Welcome, new user!\nDo you want to view the guide and FAQs on how the app work?",
        confirmButton = {
            CustomButton(
                text = "View How it Works",
                type = ButtonType.FILLED,
                onClick = {
                    // start how it works activity
                    val intent = Intent(context, HowItWorksActivity::class.java)
                    context.startActivity(intent)

                    // change the shared pref value to be false
                    val spShowWelcome = context.getSharedPreferences(
                        SharedPreferencesNames.NAME_SHOW_WELCOME_MESSAGE,
                        Context.MODE_PRIVATE
                    )
                    spShowWelcome.edit()
                        .putBoolean(SharedPreferencesNames.SHOW_WELCOME_MESSAGE_VALUE, false)
                        .apply()

                    // dismiss dialog
                    viewModel.isWelcomeDialogShown.value = false
                }
            )
        },
        dismissButton = {
            CustomButton(
                text = "Dismiss",
                type = ButtonType.OUTLINED,
                onClick = {
                    // change the shared pref value to be false
                    val spShowWelcome = context.getSharedPreferences(
                        SharedPreferencesNames.NAME_SHOW_WELCOME_MESSAGE,
                        Context.MODE_PRIVATE
                    )
                    spShowWelcome.edit()
                        .putBoolean(SharedPreferencesNames.SHOW_WELCOME_MESSAGE_VALUE, false)
                        .apply()

                    // dismiss dialog
                    viewModel.isWelcomeDialogShown.value = false
                }
            )
        },
        isDialogShown = viewModel.isWelcomeDialogShown
    )

}

@Composable
fun ImageAndButton(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    var titleVisible by remember {
        mutableStateOf(!viewModel.isInitialStartUp)  // if not initial start up, this is false, otherwise true
    }
    var lostButtonVisible by remember {
        mutableStateOf(!viewModel.isInitialStartUp)
    }
    var foundButtonVisible by remember {
        mutableStateOf(!viewModel.isInitialStartUp)
    }
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000) // fade in duration
    )
    val lostButtonAlpha by animateFloatAsState(
        targetValue = if (lostButtonVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    val foundButtonAlpha by animateFloatAsState(
        targetValue = if (foundButtonVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    // when launched during initial start up, make different elements visible with different delays
    LaunchedEffect(Unit) {
        if (viewModel.isInitialStartUp) {
            // mark as false
            viewModel.isInitialStartUp = false

            // make the title and buttons visible through alpha effect
            delay(300)
            titleVisible = true

            delay(500)
            lostButtonVisible = true

            delay(530)
            foundButtonVisible = true
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = R.drawable.homeuicompressed
            ),
            contentDescription = "Background image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f),
            contentScale = ContentScale.Crop  // use it to fill the screen
        )

        Column(
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly

        ) {
            // main title text
            Text(
                text = "Find your item using our intelligence",
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = if (AnimationManager.animationEnabled.value)
                    Modifier
                        .padding(top = dimensionResource(id = R.dimen.title_margin))
                        .alpha(alpha = titleAlpha)
                else Modifier.padding(top = dimensionResource(id = R.dimen.title_margin))
            )


            // the row displaying the new lost and new found buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = dimensionResource(id = R.dimen.content_margin)
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = if (AnimationManager.animationEnabled.value)
                        Modifier
                            .padding(top = dimensionResource(id = R.dimen.title_margin))
                            .alpha(alpha = lostButtonAlpha)
                    else Modifier.padding(top = dimensionResource(id = R.dimen.title_margin))
                ) {
                    CustomButton(
                        text = "I have lost",
                        testTag = "largeLostButton",
                        type = ButtonType.FILLED,
                        onClick = {
                            val intent = Intent(context, NewLostActivity::class.java)
                            context.startActivity(intent)
                        },
                    )
                }

                Box(
                    modifier = if (AnimationManager.animationEnabled.value)
                        Modifier
                            .padding(top = dimensionResource(id = R.dimen.title_margin))
                            .alpha(alpha = foundButtonAlpha)
                    else Modifier.padding(top = dimensionResource(id = R.dimen.title_margin))
                ) {
                    CustomButton(
                        text = "I have found",
                        testTag = "largeFoundButton",
                        type = ButtonType.TONAL,
                        onClick = {
                            val intent = Intent(context, NewFoundActivity::class.java)
                            context.startActivity(intent)
                        },
                    )
                }
            }
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
            CustomTooltipBox(text = "Next") {
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

@Composable
fun RecentlyLostItem(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    Column(
        modifier = Modifier.padding(
            dimensionResource(id = R.dimen.title_margin)
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your recently lost item",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            // the refresh button
            CustomTooltipBox(text = "Refresh") {
                IconButton(
                    modifier = Modifier.size(dimensionResource(R.dimen.image_button_size_small)),
                    onClick = {
                        loadRecentlyLostItem(context, viewModel)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Reload",
                        modifier = Modifier.size(dimensionResource(R.dimen.image_button_size_small)),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

        }


        // display the lost item in simple format
        if (viewModel.isLoadingLostItem.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.title_margin),
                        bottom = dimensionResource(id = R.dimen.content_margin)
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                CustomProgressBar()
            }

        } else if (viewModel.latestLostItem.value == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.title_margin),
                        bottom = dimensionResource(id = R.dimen.content_margin)
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "You have no recently lost items.",
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }

        } else {
            CustomLostItemPreviewSmall(
                data = viewModel.latestLostItem.value ?: LostItem(),
                onItemClicked = {
                    // launch view lost activity
                    val intent: Intent = Intent(context, ViewLostActivity::class.java)
                    intent.putExtra(
                        IntentExtraNames.INTENT_LOST_ITEM,
                        viewModel.latestLostItem.value
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}

@Composable
fun FoundItemData(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    Column(
        modifier = Modifier.padding(
            dimensionResource(id = R.dimen.title_margin)
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your found items",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            // the refresh button
            CustomTooltipBox(text = "Refresh") {
                IconButton(
                    modifier = Modifier.size(dimensionResource(R.dimen.image_button_size_small)),
                    onClick = {
                        loadFoundItems(context, viewModel)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Reload",
                        modifier = Modifier.size(dimensionResource(R.dimen.image_button_size_small)),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

        }

        if (viewModel.isLoadingFoundItem.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.title_margin),
                        bottom = dimensionResource(id = R.dimen.content_margin)
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                CustomProgressBar()
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)))  // add rounded corners
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                    )
                    .padding(dimensionResource(R.dimen.content_margin))

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // large icon
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .size(dimensionResource(id = R.dimen.image_button_size))
                    )

                    Row(
                        modifier = Modifier.weight(4f),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)

                        ) {
                            Text(
                                text = "No. of items found",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = viewModel.numberFound.value.toString(),
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "No. of claims approved",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = viewModel.numberClaimApproved.value.toString(),
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }

                    }
                }
            }
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}

@Composable
fun LargeLoginButton(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.title_margin))
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)))  // add rounded corners
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ),
            )
            .clickable {
                // start log in intent
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
            .padding(dimensionResource(R.dimen.content_margin))

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // large icon
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Login,
                contentDescription = "Icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .weight(1f)
                    .size(dimensionResource(id = R.dimen.image_button_size))
            )

            Column(
                modifier = Modifier.weight(4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // title text
                Text(
                    text = "Log in to get started",
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )

                // content text
                Text(
                    text = "Access all features using your university email.",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}

fun loadRecentlyLostItem(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    viewModel.isLoadingLostItem.value = true
    viewModel.loadLatestLostItem(object : Callback<Boolean> {
        override fun onComplete(result: Boolean) {
            viewModel.isLoadingLostItem.value = false

            if (!result) {
                Toast.makeText(context, "Fetching item data failed", Toast.LENGTH_SHORT).show()
                return
            }
        }
    })
}

fun loadFoundItems(
    context: Context,
    viewModel: HomeFragmentViewModel
) {
    viewModel.isLoadingFoundItem.value = true
    viewModel.loadFoundItemCount(object : Callback<Boolean> {
        override fun onComplete(result: Boolean) {
            viewModel.isLoadingFoundItem.value = false

            if (!result) {
                Toast.makeText(context, "Fetching item data failed", Toast.LENGTH_SHORT).show()
                return
            }

            viewModel.loadApprovedClaimsCount(object : Callback<Boolean> {
                override fun onComplete(result: Boolean) {
                    viewModel.isLoadingFoundItem.value = false

                    if (!result) {
                        Toast.makeText(context, "Fetching item data failed", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                }
            })
        }
    })
}
