package com.example.lostandfound

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

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
                    tint = colorResource(id = R.color.onBackground),
                    contentDescription = "Back")
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}