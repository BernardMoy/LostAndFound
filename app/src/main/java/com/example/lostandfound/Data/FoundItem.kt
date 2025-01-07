package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val location: Pair<Double, Double>, // store the latlng value as latitude and longitude
    val description: String,    // can be empty
    val securityQuestion: String,
    val securityQuestionAns: String,

    // time posted info
    val timePosted: Long,

    // item image
    val image: String  // store the item image as string
) : Parcelable