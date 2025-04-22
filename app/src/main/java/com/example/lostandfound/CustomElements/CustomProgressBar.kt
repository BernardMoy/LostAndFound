package com.example.lostandfound.CustomElements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.lostandfound.R

// regular progress bar and a progress bar that covers the entire screen
@Composable
fun CustomProgressBar() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.image_button_size))
                .padding(dimensionResource(id = R.dimen.content_margin_half)),
            color = MaterialTheme.colorScheme.background,
            trackColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// progress bar that takes up the entire screen
@Composable
fun CustomCenteredProgressbar() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.image_button_size))
                .padding(dimensionResource(id = R.dimen.content_margin_half)),
            color = MaterialTheme.colorScheme.background,
            trackColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}