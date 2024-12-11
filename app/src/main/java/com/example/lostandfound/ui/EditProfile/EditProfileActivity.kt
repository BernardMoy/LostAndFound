package com.example.lostandfound.ui.EditProfile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview


class EditProfileActivity : ComponentActivity() { // Use ComponentActivity here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface {
                IntroText()
            }
        }
    }
}

@Composable
fun IntroText() {
    Text(text = "Hello Compose")
}

@Preview(showBackground = true)
@Composable
fun EditProfileActivityPreview() {
    IntroText()
}