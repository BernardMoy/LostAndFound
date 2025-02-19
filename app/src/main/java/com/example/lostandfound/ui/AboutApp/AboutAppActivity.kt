package com.example.lostandfound.ui.AboutApp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomActionRow
import com.example.lostandfound.CustomElements.CustomEditText
import com.example.lostandfound.CustomElements.CustomGrayTitle
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class AboutAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AboutAppScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    AboutAppScreen(activity = MockActivity())
}

@Composable
fun AboutAppScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "About the App", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = innerPadding)
                        .padding(vertical = dimensionResource(R.dimen.title_margin))
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
fun MainContent(viewModel: AboutAppViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // contents
    Column (

    ){
        AppInfo(viewModel = viewModel)
        AppVersion(viewModel = viewModel)
        PrivacyPolicy(context = context, viewModel = viewModel)
    }

}

// display the app icon and also app name
@Composable
fun AppInfo(viewModel: AboutAppViewModel){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ){
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            /*
            When changing the app icon, aside from changing that in mimmap,
            also change the app_icon.png (Used to display here) in the drawables folder
             */
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App icon",
                modifier = Modifier.size(dimensionResource(R.dimen.profile_image_size_large))
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "LOST AND FOUND",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                style = Typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun AppVersion(viewModel: AboutAppViewModel){
    Box(
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.content_margin))
    ){
        CustomGrayTitle("App version")
    }

    CustomEditText(
        fieldLabel = "App version",
        fieldContent = "1.0",
        leftIcon = Icons.Outlined.Settings,
        isEditable = false
    )
}

@Composable
fun PrivacyPolicy(context: Context, viewModel: AboutAppViewModel){
    CustomActionRow(
        text = "View privacy policy",
        onClick = {
            // to be implemented
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show()
        },
        leftIcon = Icons.Outlined.PrivacyTip,
    )
}

