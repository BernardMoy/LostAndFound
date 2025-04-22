package com.example.lostandfound.ui.ItemComparison

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoneOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomInputField
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

/*
Test item comparison to test the score data returned
when given two item IDs for the lost item and found item
 */
class ItemComparisonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ItemComparisonScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ItemComparisonScreen(activity = MockActivity())
}

@Composable
fun ItemComparisonScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Item Comparison Test", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // includes the top tab bar and the main content
                    MainContent()
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: ItemComparisonViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // content goes here
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.title_margin))
    ) {
        IdInputs(context = context, viewModel = viewModel)
        CompareButton(context = context, viewModel = viewModel)
        DataReturned(context = context, viewModel = viewModel)
    }
}

@Composable
fun IdInputs(context: Context, viewModel: ItemComparisonViewModel) {
    CustomGrayTitle(text = "Lost item ID (Without #)")
    CustomInputField(
        fieldContent = viewModel.lostItemID.value,
        isEditable = true,
        onTextChanged = {
            viewModel.lostItemID.value = it
        },
        placeholder = "",
        isError = false,
        leadingIcon = Icons.Outlined.DoneOutline
    )

    CustomGrayTitle(text = "Found item ID (Without #)")
    CustomInputField(
        fieldContent = viewModel.foundItemID.value,
        isEditable = true,
        onTextChanged = {
            viewModel.foundItemID.value = it
        },
        placeholder = "",
        isError = false,
        leadingIcon = Icons.Outlined.DoneOutline
    )
}

@Composable
fun CompareButton(context: Context, viewModel: ItemComparisonViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.title_margin)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomButton(
            text = "Compare",
            type = ButtonType.FILLED,
            enabled = !viewModel.isLoading.value,
            onClick = {
                // load lost and found data
                if (viewModel.lostItemID.value.isEmpty() || viewModel.foundItemID.value.isEmpty()) {
                    Toast.makeText(context, "One of the IDs are null", Toast.LENGTH_SHORT)
                        .show()
                    return@CustomButton
                }

                viewModel.isLoading.value = true

                viewModel.loadLostItem(object : TestItemCallback {
                    override fun onComplete(success: Boolean) {
                        if (!success) {
                            Toast.makeText(context, "Load lost data failed", Toast.LENGTH_SHORT)
                                .show()
                            viewModel.isLoading.value = false
                            return
                        }

                        viewModel.loadFoundItem(object : TestItemCallback {
                            override fun onComplete(success: Boolean) {
                                if (!success) {
                                    Toast.makeText(
                                        context,
                                        "Load found data failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.isLoading.value = false
                                    return
                                }

                                // get the required score data!
                                viewModel.loadData(context = context, object : TestItemCallback {
                                    override fun onComplete(success: Boolean) {
                                        viewModel.isLoading.value = false
                                        return
                                    }

                                })

                            }

                        })
                    }

                })
            }
        )

        if (viewModel.isLoading.value) {
            CustomProgressBar()
        }
    }
}

@Composable
fun DataReturned(context: Context, viewModel: ItemComparisonViewModel) {
    Text(
        text = viewModel.displayedString.value,
        color = MaterialTheme.colorScheme.onBackground,
        style = Typography.bodyMedium
    )
}



