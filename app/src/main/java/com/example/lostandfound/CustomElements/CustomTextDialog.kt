package com.example.lostandfound.CustomElements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.window.Dialog
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