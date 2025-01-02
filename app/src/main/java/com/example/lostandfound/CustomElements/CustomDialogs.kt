package com.example.lostandfound.CustomElements

import android.graphics.Paint.Align
import androidx.compose.foundation.background
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Preview(showBackground = true)
@Composable
fun LoginDialogPreview() {
    ComposeTheme {
        CustomLoginDialog(
            onLoginClicked = {},
            onDismissClicked = {},
            isDialogShown = remember {mutableStateOf(true)}
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
fun CustomGoogleMapsDialog(
    isDialogShown: MutableState<Boolean>,
    isEditable: Boolean, // if false, it is in view mode
    latitude: Double = 52.37930763817003,     // provide lat and long optionally, other it displays this default location
    longitude: Double = -1.5614912710215834
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
                        val location = LatLng(latitude, longitude)
                        val singaporeMarkerState = rememberMarkerState(position = location)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(location, 15f)
                        }
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(
                                state = singaporeMarkerState,
                                title = "Singapore",
                                snippet = "Marker in Singapore"
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

                                    // dismiss dialog
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