package com.example.lostandfound.ui.PermissionsTest

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography


class PermissionsTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PermissionsTestScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    PermissionsTestScreen(activity = MockActivity())
}

@Composable
fun PermissionsTestScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Permissions test", activity = activity)
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
fun MainContent(viewModel: PermissionsTestViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { viewModel.onImagePicked(it) }
    )

    TestImage(context, viewModel, launcher)
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    TestLocation(context, viewModel)
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    TestNotification(context, viewModel)

}


@Composable
fun TestImage(context: Context, viewModel: PermissionsTestViewModel, launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>){
    Column (
        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
    ){
        // box for storing the image
        if (viewModel.image1.value != null) {
            Box(
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
            ) {
                // display the image of the item only when it is not null
                val painter = rememberAsyncImagePainter(model = viewModel.image1.value)
                Image(
                    painter = painter,
                    contentDescription = "Item image",
                    contentScale = ContentScale.FillWidth,  // make the image fill width
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(id = R.dimen.content_margin)),
            contentAlignment = Alignment.Center
        ) {
            CustomActionText(
                text = "Get image from camera",
                onClick = {

                },
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(id = R.dimen.content_margin)),
            contentAlignment = Alignment.Center
        ) {
            CustomActionText(
                text = "Get image from gallery",
                onClick = {
                    // pick image from the gallery to modify the item image
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            )
        }
    }
}

@Composable
fun TestLocation(context: Context, viewModel: PermissionsTestViewModel){
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
    ){
        Text(
            text = "Lat: " + viewModel.currentLat.value.toString(),
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Lng: " + viewModel.currentLng.value.toString(),
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(id = R.dimen.content_margin)),
            contentAlignment = Alignment.Center
        ) {
            CustomActionText(
                text = "Get current location",
                onClick = {

                },
            )
        }
    }
}

@Composable
fun TestNotification(context: Context, viewModel: PermissionsTestViewModel){
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
    ){
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(id = R.dimen.content_margin)),
            contentAlignment = Alignment.Center
        ) {
            CustomActionText(
                text = "Send notification",
                onClick = {

                },
            )
        }
    }
}



