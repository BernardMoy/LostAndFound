package com.example.lostandfound.ui.EditProfile

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lostandfound.R
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lostandfound.BackToolbar
import com.example.lostandfound.ButtonType
import com.example.lostandfound.CustomButton
import com.example.lostandfound.CustomDataField
import com.example.lostandfound.CustomErrortext
import com.example.lostandfound.FirebaseAuthManager
import com.example.lostandfound.SharedPreferencesNames
import com.example.lostandfound.ui.theme.ComposeTheme


class EditProfileActivity : ComponentActivity() {
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
                    BackToolbar(title = "Edit Profile", activity = activity)
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
                    MainContent()
                }
            }
        }
    }
}

// content includes avatar, edit fields, reminder message and save button
@Composable
fun MainContent(){
    // get the local context
    val context = LocalContext.current

    // boolean to determine if it is being rendered in preview
    val inPreview = LocalInspectionMode.current

    // get the viewmodel
    val viewModel = EditProfileViewModel()

    // box for avatar
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


    // for the firstname and lastname input field
    // get the first and last name from shared preferences
    val sp = context.getSharedPreferences(SharedPreferencesNames.NAME_USERS, Context.MODE_PRIVATE)

    var firstName by remember { mutableStateOf(
        sp.getString(SharedPreferencesNames.USER_FIRSTNAME, "") ?: ""
        )
    }
    var lastName by remember { mutableStateOf(
        sp.getString(SharedPreferencesNames.USER_LASTNAME, "") ?: ""
        )
    }

    val email = sp.getString(SharedPreferencesNames.USER_EMAIL, "") ?: ""

    Column {
        // first name field
        CustomDataField(fieldLabel = "First name",
            fieldContent = firstName,
            leftIcon = Icons.Default.AccountCircle,
            isEditable = true,
            onTextChanged = {s -> firstName = s}
        )

        HorizontalDivider(thickness = 1.dp)

        // last name field
        CustomDataField(fieldLabel = "Last name",
            fieldContent = lastName,
            leftIcon = Icons.Default.AccountCircle,
            isEditable = true,
            onTextChanged = {s -> lastName = s}
        )

        HorizontalDivider(thickness = 1.dp)
    }


    // error
    CustomErrortext("e")



    // reminder message
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


    // box for save button
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){  
        CustomButton(
            text = "Save Profile",
            type = ButtonType.FILLED,
            onClick = {
            // handle save logic here
            // update the data from the firestore database
            // but only when not in preview to avoid firebase errors
            if (inPreview) return@CustomButton

            // update user data
            val firebaseAuthManager = FirebaseAuthManager(context)

            // last lambda argument can be out of parenthesis
            firebaseAuthManager.updateUser(email, firstName, lastName
            ) { error ->
                if (error.isEmpty()) {
                    // no errors

                    // display success toast message
                    Toast.makeText(context, "Profile successfully updated", Toast.LENGTH_SHORT)
                        .show()

                    // exit activity
                    (context as? Activity)?.finish()  // safe cast and safe call

                } else {
                    // display fail toast message
                    Toast.makeText(context, "Profile update failed", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}




