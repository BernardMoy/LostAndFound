package com.example.lostandfound.CustomElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.Typography

@Composable
fun CustomActionRow(
    text: String,
    onClick: () -> Unit,     // by default it triggers the redirection when the text row itself is clicked, not just the icon
    leftIcon: ImageVector,
    rightIcon: ImageVector? = Icons.AutoMirrored.Default.KeyboardArrowRight,   // can be null
    tint: Color = MaterialTheme.colorScheme.onBackground     // tint for both text and the icon
) {
    TextField(
        value = text,
        onValueChange = {
            // value wont change
        },
        textStyle = Typography.bodyMedium,
        leadingIcon = {
            Icon(
                imageVector = leftIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        trailingIcon = {
            if (rightIcon != null) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 0.dp, vertical = dimensionResource(id = R.dimen.content_margin_half)
            ),

        enabled = false,
        isError = false,
        colors = TextFieldDefaults.colors(
            // for enabled (Editable) text
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,

            // for disabled (non-editable) text
            disabledContainerColor = Color.Transparent,
            disabledTextColor = tint,
            disabledLabelColor = Color.Gray,

            // remove the default bottom line
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}