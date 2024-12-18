package com.example.lostandfound.CustomElements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lostandfound.R

@Composable
fun CustomButton(
    text: String,
    type: ButtonType,
    onClick: () -> Unit,
    enabled: Boolean = true

) {
    // create different buttons depending on the type of button
    when(type){
        // for primary main buttons
        ButtonType.FILLED -> {
            Button(
                onClick = onClick,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,     // color of container
                    contentColor = MaterialTheme.colorScheme.onPrimary,     // color of the text
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))  // make the button have rounded corners
            ) {
                CustomButtonContent(text = text)
            }
        }

        // for secondary main buttons
        ButtonType.TONAL -> {
            Button(
                onClick = onClick,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,     // color of container
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,     // color of the text
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))  // make the button have rounded corners
            ) {
                CustomButtonContent(text = text)
            }
        }

        // for less important, back / cancel buttons
        ButtonType.OUTLINED -> {
            Button(
                onClick = onClick,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,     // color of container
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,     // color of the text
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))
                ),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))  // make the button have rounded corners
            ) {
                CustomButtonContent(text = text)
            }
        }

        // for buttons on backgrounds with colors
        ButtonType.WHITE -> {
            Button(
                onClick = onClick,
                colors = ButtonColors(
                    containerColor = Color.White,     // color of container
                    contentColor = MaterialTheme.colorScheme.primary,     // color of the text
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))  // make the button have rounded corners
            ) {
                CustomButtonContent(text = text)
            }
        }
    }
}

// text of the button
// it has no color as it is overridden by the content color of the button
@Composable
fun CustomButtonContent(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            dimensionResource(id = R.dimen.content_margin),
            dimensionResource(id = R.dimen.content_margin_half),
            dimensionResource(id = R.dimen.content_margin),
            dimensionResource(id = R.dimen.content_margin_half),
        )
    )
}

// an enum class representing the type of buttons
enum class ButtonType{
    FILLED, TONAL, OUTLINED, WHITE
}

