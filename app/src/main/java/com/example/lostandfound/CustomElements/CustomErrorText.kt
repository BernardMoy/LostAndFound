package com.example.lostandfound.CustomElements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lostandfound.R

@Composable
fun CustomErrorText(
    text: String
) {
    // this is only displayed when the error field is not empty
    if (text.isNotEmpty()){
        // surface is a block for displaying content
        Box(
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.title_margin))
        ) {
            Surface(
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
                color = MaterialTheme.colorScheme.errorContainer,
                /*
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small))
                    )

                 */

            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = dimensionResource(id = R.dimen.content_margin),
                            horizontal = dimensionResource(id = R.dimen.title_margin)
                        ),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}