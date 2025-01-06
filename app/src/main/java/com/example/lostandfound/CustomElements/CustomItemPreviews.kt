package com.example.lostandfound.CustomElements

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.Data.lostStatusText
import com.example.lostandfound.Data.statusColor
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun Preview() {

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CustomLostItemPreview(
    data: LostItem,
    onDeleteButtonClicked: () -> Unit = {},
    onViewButtonClicked: () -> Unit = {},
    isOwner: Boolean = false
) {
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
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ){
                Text(
                    text = "Lost Entry",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "#" + data.itemID,
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }



            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                // the status of the item can be either 0 1 2 - cast them to int
                val status = data.status

                Icon(
                    imageVector = Icons.Filled.Circle,
                    tint = colorResource(
                        id = statusColor[status] ?: R.color.status0
                    ),
                    contentDescription = "Status of item",
                    modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                )

                Text(
                    text = "Status: " + lostStatusText[status],
                    style = Typography.bodyMedium,
                    color = colorResource(
                        id = statusColor[status] ?: R.color.status0
                    ),
                    fontWeight = FontWeight.Bold,
                    )

            }


            // the main content of the item goes here
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // image of the item is loaded using GlideImage
                GlideImage(
                    model = Uri.parse(data.image),  // parse the string stored back to uri
                    contentDescription = "Item image",
                    modifier = Modifier.weight(1F)
                )

                // other attributes of the item
                Column(
                    modifier = Modifier.weight(2F),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                ) {
                    // name
                    Text(
                        text = data.itemName,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )

                    // date and time
                    Text(
                        text = "Date: "
                                + DateTimeManager.dateTimeToString(data.dateTime),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // category
                    Text(
                        text = "Category: " + data.category + ", " + data.subCategory,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // time posted desc
                    Text(
                        text = "Posted " + DateTimeManager.getDescription(data.timePosted),
                        style = Typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // the two buttons of delete and view
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End

            ) {
                if (isOwner){
                    CustomButton(
                        text = "Delete",
                        type = ButtonType.WARNING,
                        onClick = {
                            onDeleteButtonClicked()
                        },
                        small = true
                    )

                }

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin)))

                CustomButton(
                    text = "View",
                    type = ButtonType.FILLED,
                    onClick = {
                        onViewButtonClicked()
                    },
                    small = true
                )

            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CustomFoundItemPreview(
    data: FoundItem,
    onDeleteButtonClicked: () -> Unit = {},
    onViewButtonClicked: () -> Unit = {},
    isOwner: Boolean = false  // is owner mode is set to false when viewing previews of items from other people
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(
                    dimensionResource(id = R.dimen.corner_radius_small)
                )
            )
            .padding(dimensionResource(id = R.dimen.title_margin))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                Text(
                    text = "Found Entry",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "#" + data.itemID,
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }



            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                // the status of the item can be either 0 1 2 - cast them to int
                val status = data.status

                Icon(
                    imageVector = Icons.Filled.Circle,
                    tint = colorResource(
                        id = statusColor[status] ?: R.color.status0
                    ),
                    contentDescription = "Status of item",
                    modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                )

                Text(
                    text = "Status: " + foundStatusText[status],
                    style = Typography.bodyMedium,
                    color = colorResource(
                        id = statusColor[status] ?: R.color.status0
                    ),
                    fontWeight = FontWeight.Bold,
                )

            }


            // the main content of the item goes here
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // image of the item is loaded using GlideImage
                GlideImage(
                    model = Uri.parse(data.image),
                    contentDescription = "Item image",
                    modifier = Modifier.weight(1F)
                )

                // other attributes of the item
                Column(
                    modifier = Modifier.weight(2F),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                ) {
                    // name
                    Text(
                        text = data.itemName,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )

                    // date and time
                    Text(
                        text = "Date: "
                                + DateTimeManager.dateTimeToString(data.dateTime
                        ),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // category
                    Text(
                        text = "Category: " + data.category
                                + ", " + data.subCategory,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // time posted desc
                    Text(
                        text = "Posted " + DateTimeManager.getDescription(data.timePosted),
                        style = Typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            // the two buttons of delete and view
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End

            ) {
                if (isOwner){
                    CustomButton(
                        text = "Delete",
                        type = ButtonType.WARNING,
                        onClick = {
                            onDeleteButtonClicked()
                        },
                        small = true
                    )
                }

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin)))

                CustomButton(
                    text = if (isOwner) "View" else "Claim",
                    type = ButtonType.FILLED,
                    onClick = {
                        onViewButtonClicked()
                    },
                    small = true
                )

            }
        }
    }
}