package com.example.lostandfound

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun backToolbar(
    title: String,
    activity: ComponentActivity
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
            IconButton(onClick = {
                // implement the logic of back button here
                activity.finish()

            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back")
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