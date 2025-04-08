package com.example.lostandfound.Data

import android.os.Parcelable
import com.example.lostandfound.R
import kotlinx.parcelize.Parcelize

/*
Currently a map is used for passing data between classes, as all other firebase data are stored in a map.
It is possible to convert to storing LostItem classes instead of the map by modifying FirestoreManager
 */
@Parcelize
data class LostItem(
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
    val status: Int = 0,
    var isTracking: Boolean = false,

    // time posted info
    val timePosted: Long = 0L,

    // item image
    val image: String = "",  // store the item image as string

    // user info
    val userAvatar: String = "",
    val userFirstName: String = "",
    val userLastName: String = ""
) : Parcelable
