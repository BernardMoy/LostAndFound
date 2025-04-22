package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Lost item class that is loaded when the item is fetched from the db, including a user object
@Parcelize
data class LostItem(
    // ids
    val itemID: String = "Unknown",

    // item information
    val itemName: String = "Unknown",
    val category: String = "Unknown",
    val subCategory: String = "Unknown",
    val color: List<String> = listOf(),
    val brand: String = "",  // can be empty
    val dateTime: Long = 0L,  // in epoch
    val location: Pair<Double, Double>? = null, // store the latlng value as latitude and longitude
    val description: String = "",    // can be empty
    val status: Int = 0,
    var isTracking: Boolean = false,

    // time posted info
    val timePosted: Long = 0L,

    // item image
    val image: String = "",  // store the item image as string

    // user info
    val user: User = User()
) : Parcelable
