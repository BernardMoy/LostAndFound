package com.example.lostandfound.ui.Lost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.CustomCenterText
import com.example.lostandfound.CustomElements.CustomCenteredProgressbar
import com.example.lostandfound.CustomElements.CustomLostItemPreview
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.ui.EditProfile.EditProfileViewModel
import com.example.lostandfound.ui.ViewLost.ViewLostActivity
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
                        CustomCenterText(text = "Please login first to view this content.")
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        refreshData(context, viewModel)
    }

    // display content
    if (viewModel.isLoading.value){
        CustomCenteredProgressbar()

    } else {
        RefreshButton(context = context, viewModel = viewModel)
        LostItemsColumn(context = context, viewModel = viewModel)
    }
}

@Composable
fun RefreshButton(
    context: Context,
    viewModel: LostFragmentViewModel
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.content_margin)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ){
        // display the text at the start
        Text(
            text = "Your lost items",
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
fun LostItemsColumn(
    context: Context,
    viewModel: LostFragmentViewModel
){
    // if there are no data, display message
    if (viewModel.itemData.size == 0){
        Box(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.title_margin))
        ){
            CustomCenterText(text = "You have no lost items. Tap the '+' button to create one.")
        }

    } else {
        // for each data, display it as a preview
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
        ){
            items(viewModel.itemData) { itemMap ->
                CustomLostItemPreview(
                    data = itemMap,
                    onViewButtonClicked = {
                        // start view item activity
                        val intent = Intent(context, ViewLostActivity::class.java)

                        // pass only the item id as the extra value
                        intent.putExtra(
                            IntentExtraNames.INTENT_LOST_ID,
                            itemMap[FirebaseNames.LOSTFOUND_ID] as String
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

// function to refresh data, called everytime the function is created or refresh button clicked
fun refreshData(
    context: Context,
    viewModel: LostFragmentViewModel
){
    // is loading initially
    viewModel.isLoading.value = true

    // load lost item data of the current user from the view model
    viewModel.getAllData(object: Callback<Boolean>{
        override fun onComplete(result: Boolean) {
            viewModel.isLoading.value = false

            if (!result){
                // display toast message for failed data retrieval
                Toast.makeText(context, "Fetching data failed", Toast.LENGTH_SHORT).show()
            }
        }
    })
}