package com.example.lostandfound.CustomElements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import com.example.lostandfound.R

@Composable
fun CustomActionRow(
    text: String,
    onClick: () -> Unit,     // by default it triggers the redirection when the text row itself is clicked, not just the icon
    leftIcon: ImageVector,
    rightIcon: ImageVector? = Icons.AutoMirrored.Default.KeyboardArrowRight,   // can be null
    tint: Color = MaterialTheme.colorScheme.onBackground     // tint for both text and the icon
) {
    Surface(
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.image_button_size))
            .padding(horizontal = dimensionResource(id = R.dimen.content_margin_half)),
        onClick = onClick
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(imageVector = leftIcon,
                contentDescription = "Action icon",
                tint = tint
            )

            Text(text = text,
                color = tint,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)  // fill up all middle space
                    .padding(
                        start = dimensionResource(id = R.dimen.content_margin),
                        top = dimensionResource(id = R.dimen.content_margin_half),
                        bottom = dimensionResource(id = R.dimen.content_margin_half)
                    )
            )

            if (rightIcon != null) {
                Icon(imageVector = rightIcon,
                    contentDescription = "Redirect",
                    tint = Color.Gray)
            }
        }
    }
}