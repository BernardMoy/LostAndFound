package com.example.lostandfound.Data

import android.net.Uri
import android.os.Parcelable
import com.example.lostandfound.R
import com.google.android.gms.maps.model.LatLng
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
    val color: String = "Unknown",
    val brand: String = "",  // can be empty
    val dateTime: Long = 0L,  // in epoch
    val location: Pair<Double, Double> = Pair(52.37930763817003,-1.5614912710215834), // store the latlng value as latitude and longitude
    val description: String = "",    // can be empty
    val status: Int = 0,

    // time posted info
    val timePosted: Long = 0L,

    // item image
    val image: String = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image  // store the item image as string
) : Parcelable
