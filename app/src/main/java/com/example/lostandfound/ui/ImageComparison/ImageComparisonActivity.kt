package com.example.lostandfound.ui.ImageComparison

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.ButtonType
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.CustomElements.CustomButton
import com.example.lostandfound.CustomElements.CustomProgressBar
import com.example.lostandfound.R
import com.example.lostandfound.TFLiteManager.ImageClassifier
import com.example.lostandfound.TFLiteManager.PredictCallback
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

/*
Test activity to allow upload two images
then use the model to predict the distance between the two images
 */
class ImageComparisonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ImageComparisonScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    ImageComparisonScreen(activity = MockActivity())
}

@Composable
fun ImageComparisonScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    BackToolbar(title = "Image Comparison Test", activity = activity)
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
fun MainContent(viewModel: ImageComparisonViewModel = viewModel()) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    val launcher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { viewModel.onImage1Picked(it) }
    )

    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { viewModel.onImage2Picked(it) }
    )

    // content goes here
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.title_margin))
    ) {
        TwoImages(context = context, viewModel = viewModel, launcher1, launcher2)
        CompareButton(context = context, viewModel = viewModel)
        Distance(context = context, viewModel = viewModel)
        isMatch(context = context, viewModel = viewModel)
    }
}

@Composable
fun TwoImages(
    context: Context,
    viewModel: ImageComparisonViewModel,
    launcher1: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    launcher2: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.content_margin))

    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
            // the clickable text to add new image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
                contentAlignment = Alignment.Center
            ) {
                CustomActionText(
                    text = "Add Image",
                    onClick = {
                        // pick image from the gallery to modify the item image
                        launcher1.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // box for storing the image
            if (viewModel.image2.value != null) {
                Box(
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
                ) {
                    // display the image of the item only when it is not null
                    val painter = rememberAsyncImagePainter(model = viewModel.image2.value)
                    Image(
                        painter = painter,
                        contentDescription = "Item image",
                        contentScale = ContentScale.FillWidth,  // make the image fill width
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            // the clickable text to add new image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
                contentAlignment = Alignment.Center
            ) {
                CustomActionText(
                    text = "Add Image",
                    onClick = {
                        // pick image from the gallery to modify the item image
                        launcher2.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun CompareButton(context: Context, viewModel: ImageComparisonViewModel) {
    CustomButton(
        text = "Compare",
        type = ButtonType.FILLED,
        enabled = !viewModel.isLoading.value,
        onClick = {
            // create image classifier instance
            if (viewModel.image1.value == null || viewModel.image2.value == null) {
                Toast.makeText(context, "One of the images are null", Toast.LENGTH_SHORT).show()
                return@CustomButton
            }

            viewModel.isLoading.value = true
            val imageClassifier = ImageClassifier(context)

            // predict the distance
            imageClassifier.predict(
                viewModel.image1.value!!,
                viewModel.image2.value!!,
                object : PredictCallback {
                    override fun onComplete(distance: Float) {
                        viewModel.isLoading.value = false
                        viewModel.distance.value = distance

                        // after the prediction is complete, close the model
                        imageClassifier.close()
                    }
                })

        }
    )

    if (viewModel.isLoading.value) {
        CustomProgressBar()
    }
}

@Composable
fun Distance(context: Context, viewModel: ImageComparisonViewModel) {
    Text(
        text = "Distance: " + viewModel.distance.value.toString(),
        style = Typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun isMatch(context: Context, viewModel: ImageComparisonViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ) {
        Icon(
            imageVector = if (viewModel.distance.value <= 0.5) Icons.Filled.RadioButtonChecked
            else Icons.Filled.Cancel,
            tint = if (viewModel.distance.value <= 0.5) colorResource(id = R.color.status2)
            else colorResource(id = R.color.status0),
            contentDescription = if (viewModel.distance.value <= 0.5) "Matches" else "Does not match",
            modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
        )
        Text(
            text = if (viewModel.distance.value <= 0.5) "Matches" else "Does not match",
            style = Typography.bodyMedium,
            color = if (viewModel.distance.value <= 0.5) colorResource(id = R.color.status2)
            else colorResource(id = R.color.status0),
            fontWeight = FontWeight.Bold,
        )
    }
}




