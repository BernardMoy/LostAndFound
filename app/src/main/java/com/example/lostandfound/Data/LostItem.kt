package com.example.lostandfound.Data

/*
Currently a map is used for passing data between classes, as all other firebase data are stored in a map.
It is possible to convert to storing LostItem classes instead of the map by modifying FirestoreManager
 */
data class LostItem(
    val itemID: String,
    val userID: String,
    val itemName: String,
    val category: String,
    val subCategory: String,
    val color: String,
    val dateTime: String,
    val brand: String?,
    val description: String?,
    val status: Int

    // also the item image which is not stored here
)