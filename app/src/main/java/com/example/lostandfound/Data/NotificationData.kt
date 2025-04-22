package com.example.lostandfound.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.ui.graphics.vector.ImageVector

/*
Contains hardcoded notification title, content, and icon depending on their type
    A notification is identified by an id (0,1,2)
    as Image vectors cannot be stored in firebase
    the numbers are stored instead
 */
val notificationTitle: Map<Int, String> = mapOf(
    0 to "New matching item found!",
    1 to "Your claim has been approved!",
    2 to "Your claim was not approved",
    3 to "A user has claimed your found item"
)

val notificationContent: Map<Int, String> = mapOf(
    0 to "Click to view comparison against your lost item.",
    1 to "Click to view your claim.",
    2 to "Click to view your claim.",
    3 to "Click to view the claim from the user."
)

val notificationIcon: Map<Int, ImageVector> = mapOf(
    0 to Icons.Outlined.Search,
    1 to Icons.Outlined.SentimentSatisfied,
    2 to Icons.Outlined.SentimentDissatisfied,
    3 to Icons.Outlined.CheckCircle
)