package com.example.lostandfound.ui.PermissionsTest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.CustomElements.BackToolbar
import com.example.lostandfound.CustomElements.CustomActionText
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.File


class PermissionsTestActivity : ComponentActivity() {

    val viewModel: PermissionsTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PermissionsTestScreen(activity = this, viewModel = viewModel)
        }
    }
}

// mock activity for previews
// class MockActivity : PermissionsTestActivity()
/*
@Preview(showBackground = true)
@Composable
fun Preview() {
    // PermissionsTestScreen(activity = this)
}
 */

@Composable
fun PermissionsTestScreen(activity: PermissionsTestActivity, viewModel: PermissionsTestViewModel) {
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
                        .verticalScroll(rememberScrollState())
                ) {
                    // includes the top tab bar and the main content
                    MainContent(activity = activity, viewModel = viewModel)
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
// get the view model in the function parameter
@Composable
fun MainContent(
    activity: PermissionsTestActivity,   // for calling the permissions functions
    viewModel: PermissionsTestViewModel
) {
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // launcher for getting image from GALLERY
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
fun TestImage(
    context: Context,
    viewModel: PermissionsTestViewModel,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {

    // stores the file of the image taken by camera
    val cameraImageFile = remember {
        File(context.externalCacheDir, "capture.jpg")
    }

    // stores the image uri of the image taken by the camera
    val cameraImageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        cameraImageFile
    )

    // launcher to take image from camera
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.image.value =
                    cameraImageUri   // Update the image uri with the taken photo
            } else {
                Toast.makeText(context, "Failed to take image", Toast.LENGTH_SHORT).show()
            }
        }

    // launcher to request camera permission
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { success ->
            if (success) {
                // launch camera if permission is granted
                cameraLauncher.launch(cameraImageUri)
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }



    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
    ) {
        // box for storing the image
        if (viewModel.image.value != null) {
            Box(
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.content_margin))
            ) {
                // display the image of the item only when it is not null
                val painter = rememberAsyncImagePainter(model = viewModel.image.value)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
            contentAlignment = Alignment.Center
        ) {
            CustomActionText(
                text = "Get image from camera",
                onClick = {
                    // request camera permission
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        // if camera permission is granted, take picture
                        cameraLauncher.launch(cameraImageUri)
                    } else {
                        // else, request the camera permission
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
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
fun TestLocation(context: Context, viewModel: PermissionsTestViewModel) {
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionMaps ->
        val areGranted = permissionMaps.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            // if permissions are granted, get current user location
            getCurrentLocation(fusedLocationClient, context, viewModel)
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
    ) {
        Text(
            text = "Lat: " + viewModel.currentLocation.value.latitude.toString(),
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Lng: " + viewModel.currentLocation.value.longitude.toString(),
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
            contentAlignment = Alignment.Center
        ) {
            CustomActionText(
                text = "Get current location",
                onClick = {
                    // check if permissions are granted
                    if (locationPermissions.all {
                            // location permissions are granted
                            ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_GRANTED
                        }) {
                        // get device location
                        getCurrentLocation(fusedLocationClient, context, viewModel)
                    } else {
                        // location permissions are not yet granted
                        launcherMultiplePermissions.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            )
        }
    }
}

// get last location in latlng. Only called when the location permission is granted
// This is to distinguish between getting location failed and permission not granted
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    viewModel: PermissionsTestViewModel,
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            // Set the location in viewmodel
            viewModel.currentLocation.value = LatLng(it.latitude, it.longitude)
        }
    }.addOnFailureListener { exception ->
        // Display failed toast message
        Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun TestNotification(context: Context, viewModel: PermissionsTestViewModel) {
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.title_margin))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.content_margin)),
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



