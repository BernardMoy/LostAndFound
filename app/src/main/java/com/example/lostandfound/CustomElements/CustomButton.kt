package com.example.lostandfound.CustomElements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lostandfound.R

// A button with type filled (Gradient), tonal, outline, error, with the option of being small
@Composable
fun CustomButton(
    text: String,
    type: ButtonType,
    onClick: () -> Unit,
    enabled: Boolean = true,
    small: Boolean = false,
    testTag: String = ""

) {
    // create different buttons depending on the type of button
    when (type) {
        // for primary main buttons
        ButtonType.FILLED -> {
            Button(
                onClick = onClick,
                colors = ButtonColors(
                    containerColor = Color.Transparent,     // color of container
                    contentColor = MaterialTheme.colorScheme.onPrimary,     // color of the text
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White,
                ),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)),  // make the button have rounded corners
                modifier = Modifier
                    .testTag(testTag)
                    .background(
                        // add the gradient to the background instead of the container color
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))   // also apply the shape to the background
                    )
            ) {
                CustomButtonContent(text = text, small = small)
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
                CustomButtonContent(text = text, small = small)
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
                modifier = Modifier
                    .testTag(testTag)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))
                    ),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))  // make the button have rounded corners
            ) {
                CustomButtonContent(text = text, small = small)
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
                modifier = Modifier.testTag(testTag),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))  // make the button have rounded corners
            ) {
                CustomButtonContent(text = text, small = small)
            }
        }

        // for buttons such ad delete
        ButtonType.WARNING -> {
            Button(
                onClick = onClick,
                colors = ButtonColors(
                    containerColor = Color.Transparent,     // color of container
                    contentColor = Color.White,     // color of the text
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White,
                ),
                enabled = enabled,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius)),  // make the button have rounded corners
                modifier = Modifier
                    .testTag(testTag)
                    .background(
                        // add the gradient to the background instead of the container color
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius))   // also apply the shape to the background
                    )
            ) {
                CustomButtonContent(text = text, small = small)
            }
        }
    }
}

// text of the button
// it has no color as it is overridden by the content color of the button
@Composable
fun CustomButtonContent(text: String, small: Boolean) {
    if (small) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.content_margin_half)
            )
        )

    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.content_margin),
                vertical = dimensionResource(id = R.dimen.content_margin_half)
            )
        )
    }

}

// an enum class representing the type of buttons
enum class ButtonType {
    FILLED, TONAL, OUTLINED, WHITE, WARNING
}

