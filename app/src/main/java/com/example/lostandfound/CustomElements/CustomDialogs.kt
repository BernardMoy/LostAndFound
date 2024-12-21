package com.example.lostandfound.CustomElements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import com.example.lostandfound.DateTimeManager
import com.example.lostandfound.R

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
                ) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center) // center the text
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
    selectedDate: MutableState<String>,
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

                        // get the string representation of the date
                        val manager = DateTimeManager(epoch)
                        val dateString = manager.formattedDate

                        // update the string variable
                        selectedDate.value = dateString

                        // dismiss dialog
                        isDialogShown.value = false
                    }
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
            ),
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
                }
            )
        }
    }
}


@Composable
fun CustomTimePickerDialog(

){

}