package com.example.lostandfound.CustomElements

import android.graphics.Color
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.R
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun Preview() {
    val data = mapOf(
        FirebaseNames.LOSTFOUND_USER to 3
    )
    ComposeTheme {
        CustomLostItemPreview(data = data)
    }
}

@Composable
fun CustomLostItemPreview(
    data: Map<String, Any>,
    onDeleteButtonClicked: () -> Unit = {},
    onViewButtonClicked: () -> Unit = {}
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = androidx.compose.ui.graphics.Color.Gray,
                shape = RoundedCornerShape(
                    dimensionResource(id = R.dimen.corner_radius_small)
                )
            )
            .padding(dimensionResource(id = R.dimen.title_margin))
    ){
        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ){
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ){
                Icon(
                    imageVector = Icons.Filled.Circle,
                    tint = colorResource(id = R.color.status0),
                    contentDescription = "Status of item",
                    modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                )
                Text(
                    text = "Lost Entry #44",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = "Status: Not found",
                style = Typography.bodyMedium,
                color = colorResource(id = R.color.status0),
                fontWeight = FontWeight.Bold,
            )

            // the main content of the item goes here
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ){
                // placeholder image
                val targetImage = Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image);
                
                // image of the item
                Image(
                    painter = rememberAsyncImagePainter(model = targetImage),
                    contentDescription = "Item image",
                    modifier = Modifier
                        .weight(1F)
                )

                // other attributes of the item
                Column (
                    modifier = Modifier.weight(2F),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                ){
                    // name
                    Text(
                        text = "Item name",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                    
                    // date and time
                    Text(
                        text = "Date: ",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // category
                    Text(
                        text = "Category: ",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // the two buttons of delete and view
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End

            ){
                CustomButton(
                    text = "Delete",
                    type = ButtonType.WARNING,
                    onClick = {
                        onDeleteButtonClicked()
                    },
                    small = true
                )

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin)))

                CustomButton(
                    text = "View",
                    type = ButtonType.FILLED,
                    onClick = {
                        onDeleteButtonClicked()
                    },
                    small = true
                )

            }
        }
    }
}