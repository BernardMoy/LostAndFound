package com.example.lostandfound.CustomElements

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lostandfound.Data.ActivityLogItem
import com.example.lostandfound.Data.ChatInboxPreview
import com.example.lostandfound.Data.ClaimPreview
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ReportedIssue
import com.example.lostandfound.Data.ReportedUser
import com.example.lostandfound.Data.activityLogIcons
import com.example.lostandfound.Data.activityLogTitles
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.Data.lostStatusText
import com.example.lostandfound.Data.notificationContent
import com.example.lostandfound.Data.notificationIcon
import com.example.lostandfound.Data.notificationTitle
import com.example.lostandfound.Data.statusColor
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager.Callback
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.Utility.ImageManager
import com.example.lostandfound.ui.ChatInbox.ChatInboxActivity
import com.example.lostandfound.ui.theme.ComposeTheme
import com.example.lostandfound.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun Preview() {
    ComposeTheme {
        /*
        CustomChatInboxPreview(chatInboxPreview = ChatInboxPreview(
            recipientUser = User(
                userID = "sdads",
                firstName = "FirstName",
                lastName = "LastName",
                email = "EEEEE"
            ),
            lastMessage = "Very long message. VEry laasiidsisdiiasjaijosjoaisijasjidjaiosdojiasijoasjiojaiosdjioasdijoasijoioasd",
            lastMessageTimestamp = 829198219L
        ))

         */


        /*
        CustomNotificationItemPreview(
            0, "",238983298L, false
        )

         */

        /*
        CustomActivityLogItemPreview(activityLogItem = ActivityLogItem())

         */

        CustomLostItemPreviewSmall(data = LostItem())


    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CustomLostItemPreview(
    data: LostItem,
    onViewButtonClicked: () -> Unit = {},
) {
    val isOwner: Boolean = UserManager.getUserID() == data.user.userID

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
                    text = "Lost Entry",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                /*
                Text(
                    text = "#" + data.itemID,
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )

                 */

                // tracking data is only shown for lost items with status = 0 or 1
                if (data.status == 0 || data.status == 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            tint = if (data.isTracking) MaterialTheme.colorScheme.error else Color.Gray,
                            contentDescription = "Status of item",
                            modifier = Modifier.width(16.dp)
                        )

                        Text(
                            text = if (data.isTracking) "Tracking" else "Not tracking",
                            style = Typography.bodyMedium,
                            color = if (data.isTracking) MaterialTheme.colorScheme.error else Color.Gray,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }



            Row {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        tint = colorResource(
                            id = statusColor[data.status] ?: R.color.status0
                        ),
                        contentDescription = "Status of item",
                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                    )

                    Text(
                        text = lostStatusText[data.status] ?: "Unknown status",
                        style = Typography.bodyMedium,
                        color = colorResource(
                            id = statusColor[data.status] ?: R.color.status0
                        ),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }


            // the main content of the item goes here
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // if image string is empty, replace it with the default image
                val displayedImage =
                    if (data.image.isEmpty()) ImageManager.PLACEHOLDER_IMAGE_STRING else data.image

                // image of the item is loaded using GlideImage
                GlideImage(
                    model = Uri.parse(displayedImage),  // parse the string stored back to uri
                    contentDescription = "Item image",
                    modifier = Modifier.weight(1F)
                ) {
                    it.override(ImageManager.PREVIEW_IMAGE_SIZE, ImageManager.PREVIEW_IMAGE_SIZE)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                }

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
                        text = "Category: " + data.subCategory,
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
                /*
                if (isOwner && data.status == 0) {
                    CustomButton(
                        text = "Delete",
                        type = ButtonType.WARNING,
                        onClick = {
                            onDeleteButtonClicked()
                        },
                        small = true
                    )
                }

                 */

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
fun CustomLostItemPreviewSmall(
    data: LostItem,
    onItemClicked: () -> Unit = {}
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
            .clickable {
                onItemClicked()
            }
            .padding(dimensionResource(id = R.dimen.title_margin))

    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
        ) {

            // the main content of the item goes here
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // if image string is empty, replace it with the default image
                val displayedImage =
                    if (data.image.isEmpty()) ImageManager.PLACEHOLDER_IMAGE_STRING else data.image

                // image of the item is loaded using GlideImage
                GlideImage(
                    model = Uri.parse(displayedImage),  // parse the string stored back to uri
                    contentDescription = "Item image",
                    modifier = Modifier.weight(1F)
                ) {
                    it.override(ImageManager.PREVIEW_IMAGE_SIZE, ImageManager.PREVIEW_IMAGE_SIZE)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                }


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


                    // number of matches found
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Posted " + DateTimeManager.getDescription(data.timePosted),
                            style = Typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "View",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CustomFoundItemPreview(
    data: FoundItem,
    viewButtonText: String,
    onViewButtonClicked: () -> Unit = {},
    isImageCloseMatch: Boolean = false,    // may be true when viewing the found items from SEARCHING them.
    isDetailsCloseMatch: Boolean = false,
    isLocationCloseMatch: Boolean = false,
    percentageSimilarity: String = ""
) {
    val isOwner: Boolean = UserManager.getUserID() == data.user.userID

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
                    modifier = Modifier.weight(1f)
                )
                /*
                Text(
                    text = "#" + data.itemID,
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )

                 */

                if (percentageSimilarity.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        tint = colorResource(id = R.color.status2),
                        contentDescription = "Similarity",
                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                    )
                    Text(
                        text = "$percentageSimilarity% Similar",
                        style = Typography.bodyMedium,
                        color = colorResource(R.color.status2),
                        fontWeight = FontWeight.Bold
                    )
                }
            }



            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    tint = colorResource(
                        id = statusColor[data.status] ?: R.color.status0
                    ),
                    contentDescription = "Status of item",
                    modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                )

                Text(
                    text = foundStatusText[data.status] ?: "Unknown status",
                    style = Typography.bodyMedium,
                    color = colorResource(
                        id = statusColor[data.status] ?: R.color.status0
                    ),
                    fontWeight = FontWeight.Bold,
                )

            }


            // the main content of the item goes here
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // if image string is empty, replace it with the default image
                val displayedImage =
                    if (data.image.isEmpty()) ImageManager.PLACEHOLDER_IMAGE_STRING else data.image

                // image of the item is loaded using GlideImage
                GlideImage(
                    model = Uri.parse(displayedImage),
                    contentDescription = "Item image",
                    modifier = Modifier.weight(1F)
                ) {
                    it.override(ImageManager.PREVIEW_IMAGE_SIZE, ImageManager.PREVIEW_IMAGE_SIZE)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                }

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
                                + DateTimeManager.dateTimeToString(
                            data.dateTime
                        ),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // category
                    Text(
                        text = "Category: " + data.subCategory,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // time posted desc
                    Text(
                        text = "Posted " + DateTimeManager.getDescription(data.timePosted),
                        style = Typography.bodyMedium,
                        color = Color.Gray
                    )

                    // if image is close match, display this
                    if (isImageCloseMatch) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RadioButtonChecked,
                                tint = colorResource(id = R.color.status2),
                                contentDescription = "Close match",
                                modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                            )
                            Text(
                                text = "Close image match",
                                style = Typography.bodyMedium,
                                color = colorResource(id = R.color.status2),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    // if details are close match, display this
                    if (isDetailsCloseMatch) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RadioButtonChecked,
                                tint = colorResource(id = R.color.status2),
                                contentDescription = "Close match",
                                modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                            )
                            Text(
                                text = "Close details match",
                                style = Typography.bodyMedium,
                                color = colorResource(id = R.color.status2),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    // if location is close match, display this
                    if (isLocationCloseMatch) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RadioButtonChecked,
                                tint = colorResource(id = R.color.status2),
                                contentDescription = "Close match",
                                modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                            )
                            Text(
                                text = "Close location",
                                style = Typography.bodyMedium,
                                color = colorResource(id = R.color.status2),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // the two buttons of delete and view
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End

            ) {
                /*
                if (isOwner && data.status == 0) {
                    CustomButton(
                        text = "Delete",
                        type = ButtonType.WARNING,
                        onClick = {
                            onDeleteButtonClicked()
                        },
                        small = true
                    )
                }

                 */

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin)))

                CustomButton(
                    text = viewButtonText,
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
fun CustomClaimPreview(
    claimPreview: ClaimPreview,
    onViewButtonClicked: () -> Unit = {}
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
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
            ) {
                // if image string is empty, replace it with the default image
                val displayedImage =
                    if (claimPreview.lostItemImage.isEmpty()) ImageManager.PLACEHOLDER_IMAGE_STRING else claimPreview.lostItemImage

                // image of the item is loaded using GlideImage
                GlideImage(
                    model = Uri.parse(displayedImage),
                    contentDescription = "Item image",
                    modifier = Modifier.weight(1F)
                ) {
                    it.override(ImageManager.PREVIEW_IMAGE_SIZE, ImageManager.PREVIEW_IMAGE_SIZE)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                }

                // other attributes of the item
                Column(
                    modifier = Modifier.weight(2F),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                ) {
                    // name
                    Text(
                        text = claimPreview.lostItemName,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )

                    // user name and claim time
                    Text(
                        text = "Claim from " + claimPreview.lostUserName,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    // claim time
                    Text(
                        text = "Claimed on " + DateTimeManager.dateTimeToString(claimPreview.claimItem.timestamp),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    // whether is approved
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
                    ) {
                        val color =
                            if (claimPreview.claimItem.isApproved) colorResource(id = R.color.status2) else colorResource(
                                id = R.color.status0
                            )

                        Icon(
                            imageVector = Icons.Filled.Circle,
                            tint = color,
                            contentDescription = "Status of claim",
                            modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                        )
                        Text(
                            text = if (claimPreview.claimItem.isApproved) "You have approved this claim." else "You have not approved this claim.",
                            style = Typography.bodyMedium,
                            color = color,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    // done button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
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
    }
}

@Composable
fun CustomChatInboxPreview(
    context: Context,
    chatInboxPreview: ChatInboxPreview,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // launch chat activity
                val intent: Intent = Intent(context, ChatInboxActivity::class.java)
                intent.putExtra(
                    IntentExtraNames.INTENT_CHAT_USER,
                    chatInboxPreview.recipientUser  // put the user
                )
                context.startActivity(intent)
            }
            .padding(
                vertical = dimensionResource(id = R.dimen.content_margin),
                horizontal = dimensionResource(R.dimen.title_margin)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin))
    ) {
        // avatar
        Image(
            painter = if (chatInboxPreview.recipientUser.avatar.isNotEmpty()) rememberAsyncImagePainter(
                model = ImageManager.stringToUri(context, chatInboxPreview.recipientUser.avatar)
            )
            else painterResource(id = R.drawable.profile_icon),
            contentDescription = "User avatar",
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_button_size))
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape
                ),
        )

        // user name and last message
        Column(
            modifier = Modifier.weight(1f),  // fill remaining space
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
        ) {
            // user name and time
            Row {
                Text(
                    text = chatInboxPreview.recipientUser.firstName + ' ' + chatInboxPreview.recipientUser.lastName,
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)  // fill max width
                )

                Text(
                    text = DateTimeManager.getChatTimeDescription(chatInboxPreview.lastMessageTimestamp),
                    style = Typography.bodyMedium,
                    color = Color.Gray,
                )
            }


            // last message and red dot

            // determine if the last message is sent by the current user
            val isLastMessageSentByCurrentUser =
                chatInboxPreview.lastMessageSenderUserID == UserManager.getUserID()

            // trim the message
            var previewMessage =
                if (chatInboxPreview.lastMessageContent.length > 50) chatInboxPreview.lastMessageContent.substring(
                    0,
                    50
                ) + "..."
                else chatInboxPreview.lastMessageContent

            // replace all linebreaks of the message
            previewMessage = previewMessage.replace("\n", " ")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin_half))
            ) {
                Text(
                    text = if (isLastMessageSentByCurrentUser) "You: $previewMessage" else previewMessage,
                    style = Typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)  // fill remaining space to push dot to end
                )

                // display dot if the last message is unread and the last message is not sent by the current user
                if (!isLastMessageSentByCurrentUser && !chatInboxPreview.lastMessageIsRead) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = "Unread",
                        modifier = Modifier.width(dimensionResource(id = R.dimen.content_margin))
                    )
                }
            }
        }
    }
}

@Composable
fun CustomNotificationItemPreview(
    type: Int,
    notificationID: String,
    timestamp: Long,
    isRead: Boolean,
    onClick: () -> Unit = {},  // to be implemented in notifications activity (To use that context)
    testTag: String = ""
) {
    // a isRead variable that would react to state changes
    val isReadState = remember {
        mutableStateOf(isRead)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // if is read is false here, mark it as true in the db
                // the next time it reloads, the isRead will become true (Which this part will then be skipped)
                val firestoreManager = FirestoreManager()
                firestoreManager.update(
                    FirebaseNames.COLLECTION_NOTIFICATIONS,
                    notificationID,
                    FirebaseNames.NOTIFICATION_IS_READ,
                    true,
                    object : Callback<Boolean> {
                        override fun onComplete(result: Boolean) {
                            if (result) {
                                // mark it as read and reflect its changes
                                isReadState.value = true

                                // perform on click after modifying the db
                                onClick()
                            }
                        }
                    }
                )
            }
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(
                    dimensionResource(id = R.dimen.corner_radius_small)
                )
            )
            .padding(dimensionResource(id = R.dimen.content_margin))
            .testTag(testTag)


    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
            verticalAlignment = Alignment.Top

        ) {
            Icon(
                imageVector = notificationIcon[type] ?: Icons.Outlined.QuestionMark,
                contentDescription = "Notification icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(dimensionResource(id = R.dimen.image_button_size))
            )

            Column {
                Row {
                    Text(
                        text = notificationTitle[type] ?: "",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )

                    // the red dot
                    if (!isReadState.value) {
                        Icon(
                            imageVector = Icons.Filled.Circle,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Unread",
                            modifier = Modifier
                                .width(dimensionResource(id = R.dimen.content_margin))
                                .testTag("red_dot_$notificationID")  // test if the dot exists
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.content_margin_half)))

                Text(
                    text = notificationContent[type] ?: "",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.content_margin)))


                // show the time of the notification
                Row {
                    Text(
                        text = DateTimeManager.dateTimeToString(timestamp),
                        style = Typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "View",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp),
                    )
                }

            }

        }
    }

}


@Composable
fun CustomActivityLogItemPreview(
    activityLogItem: ActivityLogItem,
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
            .padding(dimensionResource(id = R.dimen.content_margin))
            .testTag("activity_log_${activityLogItem.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.content_margin)),
            verticalAlignment = Alignment.Top

        ) {
            Icon(
                imageVector = activityLogIcons[activityLogItem.type] ?: Icons.Outlined.QuestionMark,
                contentDescription = "Icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(dimensionResource(id = R.dimen.image_button_size))
            )

            Column {
                Row {
                    Text(
                        text = activityLogTitles[activityLogItem.type] ?: "",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.content_margin_half)))

                Text(
                    text = activityLogItem.content,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.content_margin)))


                // show the time of the notification
                Row {
                    Text(
                        text = DateTimeManager.dateTimeToString(activityLogItem.timestamp),
                        style = Typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomReportIssuePreview(
    reportedIssue: ReportedIssue
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ) {
        // the user name and email
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "From",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Icon(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = "Person",
                modifier = Modifier.width(dimensionResource(id = R.dimen.title_margin)),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reportedIssue.firstName + " " + reportedIssue.lastName,
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = reportedIssue.email,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // the description
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Description",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = reportedIssue.description,
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

        }
    }
}


@Composable
fun CustomReportUserPreview(
    reportedUser: ReportedUser
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin))
    ) {
        // the user name and email
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "From",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Icon(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = "Person",
                modifier = Modifier.width(dimensionResource(id = R.dimen.title_margin)),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reportedUser.fromFirstName + " " + reportedUser.fromLastName,
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = reportedUser.fromEmail,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // the reported user
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Reported",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Icon(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = "Person",
                modifier = Modifier.width(dimensionResource(id = R.dimen.title_margin)),
                tint = MaterialTheme.colorScheme.error
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reportedUser.toFirstName + " " + reportedUser.toLastName,
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    text = reportedUser.toEmail,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // the description
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Reason",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = reportedUser.description,
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // the view activity log button
        CustomButton(
            text = "View user's activity log",
            type = ButtonType.TONAL,
            small = true,
            onClick = {

            }
        )
    }
}