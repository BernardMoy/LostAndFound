package com.example.lostandfound.ui.EditProfile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EditProfileActivity() {
    Surface {
        Text(text = "Hello Compose")
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileActivityPreview() {
    EditProfileActivity()
}