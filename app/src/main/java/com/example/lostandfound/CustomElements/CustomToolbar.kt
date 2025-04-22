package com.example.lostandfound.CustomElements

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.lostandfound.R

// stores the toolbar present in every activity, with a back button by default
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackToolbar(
    title: String,
    activity: ComponentActivity,
    backButtonOnClick: (() -> Unit)? = null,   // this is a procedure that executes: It does not return a value
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = dimensionResource(id = R.dimen.title_font_size).value.sp,
                fontWeight = FontWeight.Bold
            )
        },

        navigationIcon = {
            if (backButtonOnClick != null) {
                IconButton(onClick = {
                    // call the on click button that takes in the current activity
                    backButtonOnClick()
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Back"
                    )
                }

            } else {
                IconButton(onClick = {
                    // default behaviour is to exit the activity
                    activity.finish()
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Back"
                    )
                }
            }
        },

        colors = TopAppBarColors(
            containerColor = Color.Transparent,     // make the toolbar background transparent
            titleContentColor = MaterialTheme.colorScheme.onBackground,    // title text color
            actionIconContentColor = MaterialTheme.colorScheme.primary,    // action buttons color
            scrolledContainerColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.primary   // back button color
        ),

        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackToolbarColored(
    title: String,
    activity: ComponentActivity,
    backButtonOnClick: (() -> Unit)? = null   // this is a procedure that executes: It does not return a value
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = dimensionResource(id = R.dimen.title_font_size).value.sp,
                fontWeight = FontWeight.Bold
            )
        },

        navigationIcon = {
            if (backButtonOnClick != null) {
                IconButton(onClick = {
                    // call the on click button that takes in the current activity
                    backButtonOnClick()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

            } else {
                IconButton(onClick = {
                    // default behaviour is to exit the activity
                    activity.finish()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },

        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,     // make the toolbar background transparent
            titleContentColor = MaterialTheme.colorScheme.onPrimary,    // title text color
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,    // action buttons color
            scrolledContainerColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary   // back button color
        ),

        modifier = Modifier.fillMaxWidth()
    )
}