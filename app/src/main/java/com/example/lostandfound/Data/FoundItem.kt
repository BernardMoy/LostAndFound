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
    val color: List<String> = listOf(),
    val brand: String = "",  // can be empty
    val dateTime: Long = 0L,  // in epoch
    val location: Pair<Double, Double>? = null, // store the latlng value as latitude and longitude
    val description: String = "",    // can be empty
    val securityQuestion: String = "",
    val securityQuestionAns: String = "",
    val status: Int = 0,

    // time posted info
    val timePosted: Long = 0L,

    // item image
    val image: String = "",  // store the item image as string

    // user info
    val userAvatar: String = "",
    val userFirstName: String = "",
    val userLastName: String = ""
) : Parcelable