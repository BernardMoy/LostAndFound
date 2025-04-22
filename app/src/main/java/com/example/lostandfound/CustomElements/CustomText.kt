package com.example.lostandfound.CustomElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.Typography

// contains custom styled text, and an action text that is underlined that the user can click on
@Composable
fun CustomGrayTitle(
    text: String
) {
    Text(
        text = text,
        style = Typography.bodyMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = dimensionResource(id = R.dimen.content_margin),
                top = dimensionResource(id = R.dimen.title_margin)
            )
    )
}

@Composable
fun CustomActionText(
    text: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Text(
        // make the text clickable
        text = text,
        style = Typography.bodyMedium,
        color = color,
        fontWeight = FontWeight.Bold,  // make text bold
        textDecoration = TextDecoration.Underline,  // make text underline
        modifier = Modifier
            .clickable { onClick() }

    )
}

// a center gray text that takes up the whole screen
@Composable
fun CustomCenterText(
    text: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = text,
            style = Typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}