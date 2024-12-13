package com.example.lostandfound.ui.EditProfile

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lostandfound.R
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lostandfound.CustomDataField
import com.example.lostandfound.SharedPreferencesNames
import com.example.lostandfound.backToolbar
import com.example.lostandfound.ui.theme.ComposeTheme


class EditProfileActivity : ComponentActivity() { // Use ComponentActivity here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditProfileScreen(activity = this)
        }
    }
}

// mock activity for previews
class MockActivity : ComponentActivity()

@Preview(showBackground = true)
@Composable
fun Preview() {
    EditProfileScreen(activity = MockActivity())
}

@Composable
fun EditProfileScreen(activity: ComponentActivity) {
    ComposeTheme {
        Surface {
            Scaffold(
                // top toolbar
                topBar = {
                    backToolbar(title = "Edit Profile", activity = activity)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                        .padding(dimensionResource(id = R.dimen.title_margin))
                        .verticalScroll(rememberScrollState())   // make screen scrollable
                ) {
                    // content goes here
                    Avatar()
                    EditFields()
                    ReminderMessage()
                    SaveButton()
                }
            }
        }
    }
}

@Composable
fun Avatar(){
    // get local context
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, dimensionResource(id = R.dimen.title_margin)),
        contentAlignment = Alignment.Center     // align the avatar to center
    ){
        // box for storing the image and the add button
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.profile_image_size_large))
        ){
            Image(painter = painterResource(id = R.drawable.profile_icon),
                contentDescription = "Your avatar",
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.profile_image_size_large))
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            IconButton(onClick = {
                // change avatar button
                Toast.makeText(context, "Add button clicked", Toast.LENGTH_SHORT).show()
            },
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.image_button_size))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Change avatar",
                    tint = Color.White)
            }
        }
    }
}

@Composable
fun EditFields() {
    // get the first and last name from shared preferences
    val context = LocalContext.current
    val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)
    val firstName = sp.getString(SharedPreferencesNames.USER_FIRSTNAME, "") ?: ""
    val lastName = sp.getString(SharedPreferencesNames.USER_LASTNAME, "") ?: ""

    Column {
        // first name field
        CustomDataField(fieldLabel = "First name",
            fieldContent = firstName,
            isEditable = true,
            leftIcon = Icons.Default.AccountCircle
        )

        HorizontalDivider(thickness = 1.dp)

        // last name field
        CustomDataField(fieldLabel = "Last name",
            fieldContent = lastName,
            isEditable = true,
            leftIcon = Icons.Default.AccountCircle
        )

        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
fun ReminderMessage(){
    Text(
        text = "Only your name and avatar would be visible to others.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                dimensionResource(id = R.dimen.header_margin),
            ),
        textAlign = TextAlign.Center   // center text
    )
}

@Composable
fun SaveButton() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Button(onClick = {
            // handle save logic here

            // display success toast message
            Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_SHORT).show()

            // exit activity
            (context as? Activity)?.finish()  // safe cast and safe call

        }) {
            Text(text = "Save Profile",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.content_margin),
                    dimensionResource(id = R.dimen.content_margin_half),
                    dimensionResource(id = R.dimen.content_margin),
                    dimensionResource(id = R.dimen.content_margin_half),
                )
            )
        }
    }
}

