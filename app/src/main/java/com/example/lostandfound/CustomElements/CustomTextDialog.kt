package com.example.lostandfound.CustomElements

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog

@Composable
fun CustomTextDialog(
    icon: ImageVector,
    title: String,
    content: String,
    primaryButton: @Composable (() -> Unit),
    secondaryButton: @Composable (() -> Unit) ? = null,   // optional secondary button
    isDialogShown: MutableState<Boolean>
){
    // Shown only when isDialogShown is true
    if (isDialogShown.value){
        Dialog(onDismissRequest = {
            isDialogShown.value = false;
        }) {
            // dialog content
            Text(text = "Dialog content")
        }
    }
}