package com.example.lostandfound.ui.ReportIssue

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomErrorText
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.CustomElements.CustomInputField
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.CustomElements.CustomTextDialog
import com.example.lostandfound.Data.SharedPreferencesNames
import com.example.lostandfound.R
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.ui.theme.ComposeTheme


class ReportIssueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReportIssueScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ReportIssueScreen(activity = MockActivity())
}

@Composable
fun ReportIssueScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            val isDialogShown = remember { mutableStateOf(false) }

            // open the dialog when the back button on device is pressed
            BackHandler {
                isDialogShown.value = true
            }

            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(
                        title = "Report an Issue",
                        activity = activity,
                        backButtonOnClick = {
                            // show the dialog
                            isDialogShown.value = true
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin))
                        .verticalScroll(rememberScrollState())   // make screen scrollable
                ) {
                    // content goes here
                    MainContent()
                }
            }

            // the dialog when the back button is pressed
            CustomTextDialog(
                icon = Icons.Outlined.Cancel,
                title = "Discard changes?",
                content = "All your changes will be lost.",
                confirmButton = {
                    CustomButton(
                        text = "Discard",
                        type = ButtonType.FILLED,
                        onClick = {
                            // dismiss the dialog
                            isDialogShown.value = false
                            // exit the activity
                            activity.finish()
                        }
                    )
                },
                dismissButton = {
                    CustomButton(text = "Cancel",
                        type = ButtonType.OUTLINED,
                        onClick = {
                            // dismiss the dialog
                            isDialogShown.value = false
                        }
                    )
                },
                isDialogShown = isDialogShown    // dialog is shown only when the value of isDialogShown is true
            )
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(viewModel: ReportIssueViewModel = viewModel()) {

    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // load data from shared prefs to view models
    val sp: SharedPreferences = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)
    viewModel.userFirstName.value = sp.getString(SharedPreferencesNames.USER_FIRSTNAME, "") ?: ""
    viewModel.userLastName.value = sp.getString(SharedPreferencesNames.USER_LASTNAME, "") ?: ""
    viewModel.userEmail.value = sp.getString(SharedPreferencesNames.USER_EMAIL, "") ?: ""

    Description(viewModel = viewModel)
    ReminderMessage(viewModel = viewModel)
    DoneButton(context = context, viewModel = viewModel)
}


@Composable
fun Description(viewModel: ReportIssueViewModel) {
    CustomGrayTitle(text = "Description")
    CustomInputField(
        fieldContent = viewModel.description.value,
        isEditable = true,
        isMultiLine = true,
        onTextChanged = { viewModel.onDescriptionChanged(it) },
        placeholder = "Can you briefly describe the problem occured?",
        isError = viewModel.descriptionError.value.isNotEmpty()
    )

    CustomErrorText(
        text = viewModel.descriptionError.value
    )
}

@Composable
fun ReminderMessage(
    viewModel: ReportIssueViewModel
) {
    Text(
        text = "Your user information will be visible when an admin reviews your reported issue.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                dimensionResource(id = R.dimen.header_margin),
            ),
        textAlign = TextAlign.Center   // center text
    )
}

@Composable
fun DoneButton(
    context: Context,
    viewModel: ReportIssueViewModel
) {
    if (viewModel.isLoading.value) {
        CustomProgressBar()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(id = R.dimen.title_margin)),
        contentAlignment = Alignment.Center
    ) {
        CustomButton(
            text = "Done",
            type = ButtonType.FILLED,
            enabled = !viewModel.isLoading.value,
            onClick = {
                // validate input
                if (!viewModel.validateInput()) {
                    return@CustomButton
                }


                viewModel.isLoading.value = true
                viewModel.onDoneButtonClicked(object : ErrorCallback {
                    override fun onComplete(error: String) {
                        // stop loading
                        viewModel.isLoading.value = false

                        if (error.isEmpty()) {
                            // if no errors, exit activity and display success message
                            Toast.makeText(context, "Issue reported!", Toast.LENGTH_SHORT)
                                .show()
                            (context as Activity).finish()

                        } else {
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        )
    }

}


