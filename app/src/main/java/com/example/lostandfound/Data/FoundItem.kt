package com.example.lostandfound.Data

import android.os.Parcelable
import com.example.lostandfound.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoundItem(
    // ids
    val itemID: String = "Unknown",
    val userID: String = "Unknown",

    // item information
    val itemName: String = "Unknown",
    val category: String = "Unknown",
    val subCategory: String = "Unknown",
    val color: String = "Unknown",
    val brand: String = "",  // can be empty
    val dateTime: Long = 0L,  // in epoch
    val location: Pair<Double, Double> = Pair(52.37930763817003,-1.5614912710215834), // store the latlng value as latitude and longitude
    val description: String = "",    // can be empty
    val securityQuestion: String = "",
    val securityQuestionAns: String = "",
    val status: Int = 0,

    // time posted info
    val timePosted: Long = 0L,

    // item image
    val image: String = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image  // store the item image as string
) : Parcelable