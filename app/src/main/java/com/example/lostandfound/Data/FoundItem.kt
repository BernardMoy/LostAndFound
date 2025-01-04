package com.example.lostandfound.Data

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class FoundItem(
    // ids
    val itemID: String,
    val userID: String,

    // item information
    val itemName: String,
    val category: String,
    val subCategory: String,
    val color: String,
    val brand: String,  // can be empty
    val dateTime: Long,  // in epoch
    val location: LatLng,
    val description: String,    // can be empty
    val status: Int,
    val securityQuestion: String,
    val securityQuestionAns: String,

    // time posted info
    val timePosted: Long,

    // item image
    val image: Uri
)