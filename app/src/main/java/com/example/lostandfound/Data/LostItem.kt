package com.example.lostandfound.Data

import android.net.Uri
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

/*
Currently a map is used for passing data between classes, as all other firebase data are stored in a map.
It is possible to convert to storing LostItem classes instead of the map by modifying FirestoreManager
 */
@Parcelize
data class LostItem(
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

    // time posted info
    val timePosted: Long,

    // item image
    val image: String  // store the item image as string
) : Parcelable