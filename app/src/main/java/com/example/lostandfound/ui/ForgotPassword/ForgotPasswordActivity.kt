package com.example.lostandfound.ui.ForgotPassword

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomErrorText
import com.example.lostandfound.CustomElements.CustomInputField
import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

/*
When the user enters an email, firebase auth sends a password reset email
 */
class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ForgotPasswordScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ForgotPasswordScreen(activity = MockActivity())
}

@Composable
fun ForgotPasswordScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
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
fun MainContent(viewModel: ForgotPasswordViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    start = dimensionResource(id = R.dimen.header_margin),
                    end = dimensionResource(id = R.dimen.header_margin),
                    bottom = dimensionResource(id = R.dimen.centering_margin)
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {
            Title(context = context, viewModel = viewModel)
            InputFields(context = context, viewModel = viewModel)
            Button(context = context, viewModel = viewModel)
        }
    }
}

@Composable
fun Title(
    context: Context,
    viewModel: ForgotPasswordViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Forgot password",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Please enter your university email used to register your account.",
            style = Typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun InputFields(
    context: Context,
    viewModel: ForgotPasswordViewModel
) {
    CustomInputField(
        fieldContent = viewModel.email.value,
        isEditable = true,
        leadingIcon = Icons.Outlined.Email,
        onTextChanged = { viewModel.onEmailChanged(it) },
        placeholder = "Email address (@warwick.ac.uk)",
        isError = viewModel.error.value.isNotEmpty()
    )

    CustomErrorText(
        text = viewModel.error.value
    )
}

@Composable
fun Button(
    context: Context,
    viewModel: ForgotPasswordViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomButton(
            text = "Done",
            type = ButtonType.FILLED,
            onClick = {
                if (!viewModel.validateEmail()) {
                    return@CustomButton
                }

                // send password reset email
                val firebaseAuthManager: FirebaseAuthManager = FirebaseAuthManager(context)
                firebaseAuthManager.sendPasswordResetEmail(
                    viewModel.email.value,
                    object : ErrorCallback {
                        override fun onComplete(error: String) {
                            if (error.isNotEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Error sending password reset email",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }

                            // sent email successful
                            Toast.makeText(
                                context,
                                "A password reset email has been sent.",
                                Toast.LENGTH_SHORT
                            ).show()

                            // exit activity
                            (context as Activity).finish()
                        }
                    }
                )
            }
        )
    }
}


