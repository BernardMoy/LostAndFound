package com.example.lostandfound.CustomElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lostandfound.Data.ChatMessage
import com.example.lostandfound.Data.ChatMessagePreview
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun CustomCardPreview() {
    /*
    ComposeTheme {
        CustomCard(
            title = "Search",
            content = "Search for existing items",
            icon = Icons.Outlined.Search
        )
    }
     */

    ComposeTheme {
        Column (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ){
            /*
            CustomChatCard(
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
                timestamp = 1736188440L,
                isSentByCurrentUser = false,
                senderName = "Person1"
            )

            CustomChatCard(
                text = "e",
                timestamp = 2L,
                isSentByCurrentUser = true,
                senderName = "Person2"
            )

             */
        }
    }
}

@Composable
fun CustomCard(
    title: String,
    content: String,
    icon: ImageVector
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),

        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small)))  // add rounded corners
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                ),
            )
            .padding(dimensionResource(R.dimen.content_margin))

    ){
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            // large icon
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .weight(1f)
                    .size(dimensionResource(id = R.dimen.image_button_size))
            )

            Column (
                modifier = Modifier.weight(4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ){
                // title text
                Text(
                    text = title,
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )

                // content text
                Text(
                    text = content,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CustomChatCard(
    chatMessage: ChatMessage
){
    // if the sender user id is the current user id, it is sent by current user
    val isSentByCurrentUser = chatMessage.senderUserID == FirebaseUtility.getUserID()

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSentByCurrentUser) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
    ){
        // show the sender name
        /*
        Text(
            text = USERNAME HERE,
            style = Typography.bodyMedium,
            color = Color.Gray
        )

         */

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half)),
        ){
            // show the time posted
            if (isSentByCurrentUser){
                Text(
                    text = DateTimeManager.getChatTimeDescription(chatMessage.timestamp),
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // show the message
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RectangleShape,  // override the default shape

                // set the max width of the text message to be 2/3 of the row width
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.corner_radius_small),
                            topEnd = dimensionResource(id = R.dimen.corner_radius_small),
                            bottomStart = if (isSentByCurrentUser) dimensionResource(id = R.dimen.corner_radius_small) else 0.dp,
                            bottomEnd = if (isSentByCurrentUser) 0.dp else dimensionResource(id = R.dimen.corner_radius_small)
                        )
                    )
                    .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 2 / 3)
            ) {
                Column (
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.title_margin)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.title_margin))
                ){
                    // message text
                    Text(
                        text = chatMessage.text,
                        style = Typography.bodyMedium,
                        color = if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = if (isSentByCurrentUser) TextAlign.End else TextAlign.Start
                    )
                }
            }

            // show the time posted
            if (!isSentByCurrentUser){
                Text(
                    text = DateTimeManager.getChatTimeDescription(chatMessage.timestamp),
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}