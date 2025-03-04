package com.example.lostandfound.ui.Found

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomFoundItemPreview
import com.example.lostandfound.CustomElements.CustomSearchField
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.Utility.AnimationManager
import com.example.lostandfound.Utility.AutoLoadingManager
import com.example.lostandfound.ui.ViewFound.ViewFoundActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import java.util.Locale


class FoundFragment : Fragment() {

    // variable to keep track of whether the user is logged in
    val isLoggedIn = mutableStateOf(FirebaseUtility.isUserLoggedIn())

    // create the view model here
    val viewModel: FoundFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                ComposeTheme {
                    // check if user is logged in
                    if (!isLoggedIn.value) {
                        CustomCenterText(text = "Please login first to view this content.")
                    } else {
                        FoundFragmentScreen(viewmodel = viewModel)
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

        // refresh the data everytime the screen is reloaded
        if (AutoLoadingManager.autoLoadingEnabled.value) {
            refreshData(context = requireContext(), viewModel = viewModel)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    ComposeTheme {
        FoundFragmentScreen(viewmodel = viewModel())
    }
}

@Composable
fun FoundFragmentScreen(viewmodel: FoundFragmentViewModel, isTesting: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.title_margin))
    ) {
        MainContent(viewModel = viewmodel, isTesting = isTesting)
    }
}

@Composable
fun MainContent(viewModel: FoundFragmentViewModel, isTesting: Boolean) {
    val context = LocalContext.current

    /*
    It is now done in the onResume() method instead
    LaunchedEffect(Unit) {
        refreshData(context, viewModel)
    }
     */

    if (isTesting) {
        LaunchedEffect(Unit) {
            refreshData(context, viewModel)
        }
    }

    // display content
    if (viewModel.isLoading.value) {
        CustomCenteredProgressbar()

    } else {
        RefreshButton(context = context, viewModel = viewModel)
        FoundItemsColumn(context = context, viewModel = viewModel)
    }
}

@Composable
fun RefreshButton(
    context: Context,
    viewModel: FoundFragmentViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.content_margin)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // display the text at the start
        Text(
            text = "Your found items",
            style = Typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f) // make the refresh button go to the end by taking up all available space

        )
        IconButton(
            onClick = {
                // refresh the list - manually (by now)
                refreshData(context, viewModel)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Reload",
                modifier = Modifier.size(dimensionResource(id = R.dimen.image_button_size)),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun FoundItemsColumn(
    context: Context,
    viewModel: FoundFragmentViewModel
) {
    // if there are no data, display message
    if (viewModel.itemData.size == 0) {
        Box(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.title_margin))
        ) {
            CustomCenterText(text = "You have no found items. Tap the '+' button to create one.")
        }

    } else {
        // search bar
        CustomSearchField(
            placeholder = "Search by name or reference #...",
            fieldContent = viewModel.searchWord
        )
        Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin)))


        // for each data, display it as a preview
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
        ) {
            items(
                viewModel.itemData.filter { item ->
                    // filter items based on the item names or ids
                    // by default it shows all items if the search word is empty
                    item.itemName.lowercase(Locale.UK).contains(
                        viewModel.searchWord.value.lowercase(
                            Locale.UK
                        )
                    )
                            || item.itemID.startsWith(viewModel.searchWord.value.removePrefix("#"))
                }

            ) { itemData ->

                val visibleState = remember {
                    MutableTransitionState(false).apply { targetState = true }
                }

                // display each preview with animation, also display the same animation when reloaded
                if (AnimationManager.animationEnabled.value) {
                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        CustomFoundItemPreview(
                            data = itemData,
                            onViewButtonClicked = {
                                // start view item activity
                                val intent = Intent(context, ViewFoundActivity::class.java)

                                // pass only the item id as the extra value
                                intent.putExtra(
                                    IntentExtraNames.INTENT_FOUND_ID,
                                    itemData
                                )
                                context.startActivity(intent)
                            },
                            viewButtonText = "View"
                        )
                    }

                } else {
                    CustomFoundItemPreview(
                        data = itemData,
                        onViewButtonClicked = {
                            // start view item activity
                            val intent = Intent(context, ViewFoundActivity::class.java)

                            // pass only the item id as the extra value
                            intent.putExtra(
                                IntentExtraNames.INTENT_FOUND_ID,
                                itemData
                            )
                            context.startActivity(intent)
                        },
                        viewButtonText = "View"
                    )
                }
            }
        }
    }
}

// function to refresh data, called everytime the function is created or refresh button clicked
fun refreshData(
    context: Context,
    viewModel: FoundFragmentViewModel
) {
    // is loading initially
    viewModel.isLoading.value = true

    // load lost item data of the current user from the view model
    viewModel.getAllData(object : Callback<Boolean> {
        override fun onComplete(result: Boolean) {
            viewModel.isLoading.value = false

            if (!result) {
                // display toast message for failed data retrieval
                Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
            }
        }
    })
}