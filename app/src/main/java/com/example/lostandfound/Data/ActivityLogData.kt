package com.example.lostandfound.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.ui.graphics.vector.ImageVector

val activityLogTitles: Map<Int, String> = mapOf(
    0 to "You posted a new lost item",
    1 to "You posted a new found item",
    2 to "You marked a lost item as tracked",
    3 to "You marked a lost item as untracked",
    4 to "You have made a claim on your lost item",
    5 to "You have approved a claim",
    6 to "You have deleted a lost item",
    7 to "You have deleted a found item"
)

// activity log content:
/*
    0: show the lost name(id)
    1: show the found name(id)
    2: show the lost name(id)
    3: show the lost name(id)
    4: show the lost name(id)  (Must be done by lost user)
    5: show the found name(id)  (Must be done by found user)
    6: show the lost item name(id)
    7: show the found item name(id)
 */

val activityLogIcons: Map<Int, ImageVector> = mapOf(
    0 to Icons.Outlined.AddCircleOutline,
    1 to Icons.Outlined.AddCircleOutline,
    2 to Icons.Outlined.TrackChanges,
    3 to Icons.Outlined.TrackChanges,
    4 to Icons.Outlined.AddCircleOutline,
    5 to Icons.Outlined.CheckCircle,
    6 to Icons.Outlined.DeleteOutline,
    7 to Icons.Outlined.DeleteOutline
)