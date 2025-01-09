package com.example.lostandfound.Data

import android.net.Uri
import com.example.lostandfound.R

// a data class to store all elements needed for a claim preview.
// no similar class exist for lost and found items because they are using the LostItem and FoundItem classes
// to render their previews respectively.
data class ClaimPreview (
    val lostItemImage: String = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
    val lostItemName: String = "Name of item",
    val lostUserName: String = "user name",
    val claimTimestamp: Long = 0L,
    val isClaimApproved: Boolean = false
)