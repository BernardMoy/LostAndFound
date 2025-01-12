package com.example.lostandfound.CustomElements

import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.Data.User
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Preview(showBackground = true)
@Composable
fun LoginDialogPreview() {
    ComposeTheme {
        CustomInputDialog(
            icon = Icons.Outlined.Lock,
            title = "Security Question",
            content = "Question goes here! It can be very very long?",
            inputText = remember {
                mutableStateOf("")
            },
            confirmButton = {},
            dismissButton = {},
            inputPlaceholder = "Your answer...",
            isDialogShown = remember{
                mutableStateOf(true)
            },
            errorMessage = remember {
                mutableStateOf("")
            }
        )
    }
}

@Composable
fun CustomTextDialog(
    icon: ImageVector,
    title: String,
    content: String,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit) ? = null,   // optional secondary button
    isDialogShown: MutableState<Boolean>
){
    // Shown only when isDialogShown is true
    if (isDialogShown.value){
        AlertDialog(
            onDismissRequest = { isDialogShown.value = false },
            containerColor = MaterialTheme.colorScheme.background,
            icon = {
                Icon(imageVector = icon,
                    contentDescription = "Dialog icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(dimensionResource(R.dimen.image_button_size)))
            },
            title = {
                Text(text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = confirmButton,
            dismissButton = dismissButton
        )
    }
}

// a dialog to view and contact user
@Composable
fun CustomUserDialog(
    user: User,
    context: Context,  // used to convert string to uri
    onConfirmButtonClicked: () -> Unit,
    isDialogShown: MutableState<Boolean>
){
    // Shown only when isDialogShown is true
    if (isDialogShown.value){
        AlertDialog(
            onDismissRequest = { isDialogShown.value = false },
            containerColor = MaterialTheme.colorScheme.background,
            icon = {
                val avatar = ImageManager.stringToUri(context, user.avatar)

                Image(
                    painter = if (avatar != null) rememberAsyncImagePainter(model = ImageManager.stringToUri(context, user.avatar))
                            else painterResource(id = R.drawable.profile_icon),
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_button_size))
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = CircleShape
                        ),
                )
            },
            title = {
                Text(
                    text = user.firstName + ' ' + user.lastName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Contact this user?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                CustomButton(
                    text = "Message",
                    type = ButtonType.FILLED,
                    onClick = onConfirmButtonClicked
                )
            },
            dismissButton = {
                CustomButton(
                    text = "Cancel",
                    type = ButtonType.OUTLINED,
                    onClick = {
                        isDialogShown.value = false
                    }
                )
            }
        )
    }
}

@Composable
fun CustomInputDialog(
    icon: ImageVector,
    title: String,
    content: String,
    inputText: MutableState<String>,
    inputPlaceholder: String,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit) ? = null,   // optional secondary button
    isDialogShown: MutableState<Boolean>,
    errorMessage: MutableState<String>
){
    if (isDialogShown.value){
        AlertDialog(
            onDismissRequest = { isDialogShown.value = false },
            containerColor = MaterialTheme.colorScheme.background,
            icon = {
                Icon(imageVector = icon,
                    contentDescription = "Dialog icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(dimensionResource(R.dimen.image_button_size)))
            },
            title = {
                Text(text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
                ) {
                    Text(
                        text = content,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    // input field
                    CustomInputField(
                        placeholder = inputPlaceholder,
                        fieldContent = inputText.value,
                        isEditable = true,
                        isMultiLine = true,
                        onTextChanged = { s ->
                            inputText.value = s
                        }
                    )
                    
                    // error message
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        CustomErrorTextNoBox(text = errorMessage.value)
                    }
                }
            },
            confirmButton = confirmButton,
            dismissButton = dismissButton
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    selectedDate: MutableState<Long?>,
    isDialogShown: MutableState<Boolean>
){
    if (isDialogShown.value) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                // close the dialog
                isDialogShown.value = false
            },

            confirmButton = {
                CustomButton(
                    text = "Select",
                    type = ButtonType.FILLED,
                    onClick = {
                        // get the epoch time
                        val epoch = ((datePickerState.selectedDateMillis ?: 0L )/ 1000L)

                        // update the epoch value
                        selectedDate.value = epoch

                        // dismiss dialog
                        isDialogShown.value = false
                    },
                )
            },
            dismissButton = {
                CustomButton(text = "Cancel",
                    type = ButtonType.OUTLINED,
                    onClick = {
                        // dismiss dialog
                        isDialogShown.value = false
                    }
                )
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            // for the date picker above the calendar
            // the edit (pen button) is set to be disabled here
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = {
                    Text(
                        text = "Date of lost item",
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.title_margin))
                    )
                },
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    selectedHour: MutableState<Int?>,
    selectedMinute: MutableState<Int?>,
    isDialogShown: MutableState<Boolean>
){
    // set the initial time to the current time
    val timePickerState = rememberTimePickerState(
        initialHour = DateTimeManager.getCurrentHour(),
        initialMinute = DateTimeManager.getCurrentMinute(),
        is24Hour = true,
    )

    // show the dialog only when isDialogShown is true
    if (isDialogShown.value){
        AlertDialog(
            onDismissRequest = {
                isDialogShown.value = false },
            confirmButton = {
                // change the selected time
                CustomButton(
                    text = "Select",
                    type = ButtonType.FILLED,
                    onClick = {
                        // update the string variable
                        selectedHour.value = timePickerState.hour
                        selectedMinute.value = timePickerState.minute

                        // dismiss dialog
                        isDialogShown.value = false
                    },
                )
            },
            dismissButton = {
                CustomButton(text = "Cancel",
                    type = ButtonType.OUTLINED,
                    onClick = {
                        // dismiss dialog
                        isDialogShown.value = false
                    }
                )
            },
            text = {
                // content goes here
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.background,
                        clockDialColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        )
    }

}

// Dialog when user tries to access content that they need to log in first
@Composable
fun CustomLoginDialog(
    onDismissClicked: () -> Unit,
    onLoginClicked: (() -> Unit),
    isDialogShown: MutableState<Boolean>
){
    // Shown only when isDialogShown is true
    if (isDialogShown.value){
        AlertDialog(
            onDismissRequest = {
            // non dismissable
            },
            containerColor = MaterialTheme.colorScheme.background,
            icon = {
                Icon(imageVector = Icons.Outlined.Person,
                    contentDescription = "Login icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(dimensionResource(R.dimen.image_button_size)))
            },
            title = {
                Text(text = "Login required",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center // Centers the content inside the Box
                ) {
                    Text(
                        text = "Please login first to perform this action.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                // change the selected time
                CustomButton(
                    text = "Login",
                    type = ButtonType.FILLED,
                    onClick = {
                        onLoginClicked()
                    },
                )
            },
            dismissButton = {
                CustomButton(
                    text = "Cancel",
                    type = ButtonType.OUTLINED,
                    onClick = {
                        onDismissClicked()
                    }
                )
            }
        )
    }
}

@Composable
fun CustomPickLocationDialog(
    isDialogShown: MutableState<Boolean>,
    selectedLocation: MutableState<LatLng>,   // this is the state that will ONLY BE UPDATED WHEN THE DONE BUTTON IS CLICKED
){
    var currentLocation by remember {
        mutableStateOf(selectedLocation.value)
    }

    // Code is executed everytime the dialog becomes shown
    LaunchedEffect(isDialogShown.value) {
        if (isDialogShown.value) {
            // reset current location to the selected location
            currentLocation = selectedLocation.value
        }
    }

    if (isDialogShown.value){
        Dialog(
            onDismissRequest = {
                isDialogShown.value = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),  // remove horizontal padding
            content = {
                Box(
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(id = R.dimen.header_margin),
                            horizontal = dimensionResource(id = R.dimen.title_margin)
                        )
                        .fillMaxSize()
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)))
                        .background(color = MaterialTheme.colorScheme.background),

                ){
                    Column(
                        modifier = Modifier.padding(
                            vertical = dimensionResource(id = R.dimen.title_margin),
                            horizontal = dimensionResource(id = R.dimen.content_margin)
                        ),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "Select location",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "Tap on the map to select a location.",
                            style = Typography.bodyMedium,
                            color = Color.Gray
                        )

                        // Google maps composable
                        val markerState = remember {
                            MarkerState(position = currentLocation)
                        }
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
                        }
                        val uiSettings by remember {
                            mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
                        }
                        val properties by remember {
                            mutableStateOf(MapProperties(mapType = MapType.NORMAL))
                        }

                        // when a new location is selected
                        LaunchedEffect(currentLocation) {
                            // change the marker location
                            markerState.position = currentLocation

                            // change the camera location when a new location value is selected
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLng(currentLocation),
                                durationMs = 1500
                            )
                        }

                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            cameraPositionState = cameraPositionState,
                            uiSettings = uiSettings,
                            properties = properties,
                            onMapClick = { latlng ->
                                // update location with selected latlng
                                currentLocation = latlng

                            }
                        ) {
                            // markers goes here
                            // It will be updated when location is updated through tapping on the map
                            Marker(
                                state = markerState,
                                title = "Item location"
                            )
                        }

                        // buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            CustomButton(
                                text = "Use device location",
                                type = ButtonType.OUTLINED,
                                small = true,
                                onClick = {

                                }
                            )


                            CustomButton(
                                text = "Done",
                                type = ButtonType.FILLED,
                                onClick = {
                                    // change selected location
                                    selectedLocation.value = currentLocation
                                    isDialogShown.value = false
                                }
                            )
                        }
                    }

                    // close button at top end
                    IconButton(
                        onClick = {
                            // remove the image
                            isDialogShown.value = false
                        },
                        modifier = Modifier
                            .padding(
                                dimensionResource(id = R.dimen.content_margin_half)
                            )
                            .size(dimensionResource(id = R.dimen.image_button_size))
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close dialog",
                            tint = Color.White
                        )
                    }

                }

            }
        )
    }
}



@Composable
fun CustomViewLocationDialog(
    isDialogShown: MutableState<Boolean>,
    selectedLocation: LatLng,   // no need to be mutable here
){
    if (isDialogShown.value){
        Dialog(
            onDismissRequest = {
                isDialogShown.value = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),  // remove horizontal padding
            content = {
                Box(
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(id = R.dimen.header_margin),
                            horizontal = dimensionResource(id = R.dimen.title_margin)
                        )
                        .fillMaxSize()
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)))
                        .background(color = MaterialTheme.colorScheme.background),

                    ){
                    Column(
                        modifier = Modifier.padding(
                            vertical = dimensionResource(id = R.dimen.title_margin),
                            horizontal = dimensionResource(id = R.dimen.content_margin)
                        ),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "View location",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )


                        // Google maps composable
                        val markerState = remember {
                            MarkerState(position = selectedLocation)
                        }
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
                        }
                        val uiSettings by remember {
                            mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
                        }
                        val properties by remember {
                            mutableStateOf(MapProperties(mapType = MapType.NORMAL))
                        }


                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            cameraPositionState = cameraPositionState,
                            uiSettings = uiSettings,
                            properties = properties,
                            onMapClick = { latlng ->
                                // nothing happens
                            }
                        ) {
                            // markers goes here
                            // It will be updated when location is updated through tapping on the map
                            Marker(
                                state = markerState,
                                title = "Item location"
                            )
                        }
                    }

                    // close button at top end
                    IconButton(
                        onClick = {
                            // remove the image
                            isDialogShown.value = false
                        },
                        modifier = Modifier
                            .padding(
                                dimensionResource(id = R.dimen.content_margin_half)
                            )
                            .size(dimensionResource(id = R.dimen.image_button_size))
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close dialog",
                            tint = Color.White
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun CustomViewTwoLocationsDialog(
    context: Context,
    isDialogShown: MutableState<Boolean>,
    selectedLocation1: LatLng,   // no need to be mutable here
    selectedLocation2: LatLng
){
    if (isDialogShown.value){
        Dialog(
            onDismissRequest = {
                isDialogShown.value = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),  // remove horizontal padding
            content = {
                Box(
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(id = R.dimen.header_margin),
                            horizontal = dimensionResource(id = R.dimen.title_margin)
                        )
                        .fillMaxSize()
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)))
                        .background(color = MaterialTheme.colorScheme.background),

                    ){
                    Column(
                        modifier = Modifier.padding(
                            vertical = dimensionResource(id = R.dimen.title_margin),
                            horizontal = dimensionResource(id = R.dimen.content_margin)
                        ),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "View location",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )


                        // Google maps composable
                        val markerState1 = remember {
                            MarkerState(position = selectedLocation1)
                        }
                        val markerState2 = remember {
                            MarkerState(position = selectedLocation2)
                        }
                        val cameraPositionState1 = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(selectedLocation1, 15f)
                        }
                        val cameraPositionState2 = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(selectedLocation1, 15f)
                        }

                        val uiSettings by remember {
                            mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
                        }
                        val properties by remember {
                            mutableStateOf(MapProperties(mapType = MapType.NORMAL))
                        }


                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            cameraPositionState = cameraPositionState2, // set focus to found item location
                            uiSettings = uiSettings,
                            properties = properties,
                            onMapClick = { latlng ->
                                // nothing happens
                            }
                        ) {
                            // markers goes here
                            // It will be updated when location is updated through tapping on the map
                            MapMarker(
                                context = context,
                                state = markerState1,
                                title = "Lost item location",
                                iconResourceId = R.drawable.pin_lost
                            )
                            MapMarker(
                                context = context,
                                state = markerState2,
                                title = "Found item location",
                                iconResourceId = R.drawable.pin_found
                            )
                        }
                    }

                    // close button at top end
                    IconButton(
                        onClick = {
                            // remove the image
                            isDialogShown.value = false
                        },
                        modifier = Modifier
                            .padding(
                                dimensionResource(id = R.dimen.content_margin_half)
                            )
                            .size(dimensionResource(id = R.dimen.image_button_size))
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close dialog",
                            tint = Color.White
                        )
                    }
                }
            }
        )
    }
}

/*
https://stackoverflow.com/questions/70598043/how-to-use-custom-icon-of-google-maps-marker-in-compose
 */
@Composable
fun MapMarker(
    context: Context,
    state: MarkerState,
    title: String,
    @DrawableRes iconResourceId: Int
) {
    val icon = bitmapDescriptor(
        context, iconResourceId
    )
    Marker(
        state = state,
        title = title,
        icon = icon,
    )
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    // force the width of the drawable to be 48dp
    val displayMetrics = context.resources.displayMetrics
    val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48.toFloat(), displayMetrics).toInt()
    val height = drawable.intrinsicHeight*width/drawable.intrinsicWidth

    drawable.setBounds(0, 0, width, height)
    val bm = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}