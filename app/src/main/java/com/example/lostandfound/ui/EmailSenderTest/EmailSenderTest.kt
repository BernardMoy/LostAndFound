package com.example.lostandfound.ui.EmailSenderTest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.lostandfound.CustomElements.CustomInputField
import com.example.lostandfound.R
import com.example.lostandfound.ui.AboutApp.EmailSenderTestViewModel
import com.example.lostandfound.ui.theme.ComposeTheme


class EmailSenderTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmailSenderTestScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    EmailSenderTestScreen(activity = MockActivity())
}

@Composable
fun EmailSenderTestScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Email Sender Test", activity = activity)
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
fun MainContent(viewModel: EmailSenderTestViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.title_margin)),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
    ){
        InputEmail(viewModel = viewModel)
        SendButton(context = context, viewModel = viewModel)
    }

}

@Composable
fun InputEmail(viewModel: EmailSenderTestViewModel){
    CustomInputField(
        placeholder = "Your email here...",
        isEditable = true,
        fieldContent = viewModel.email.value,
        onTextChanged = { t ->
            viewModel.email.value = t
        },
        leadingIcon = Icons.Outlined.Email
    )
}

@Composable
fun SendButton(context:Context, viewModel: EmailSenderTestViewModel){
    CustomButton(
        text = "Send email",
        type = ButtonType.FILLED,
        onClick = {

        }
    )
}


