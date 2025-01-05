package com.example.lostandfound.Utility

import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager

interface Callback<T> {
    fun onComplete(result: T)
}


// given a lost item and a found item, determine whether they are matched
fun isMatch(
    lostItem: LostItem,
    foundItem: FoundItem

): Boolean{
    /* TODO implement this */
    return true
}


// given a lost item, return from db all the matching found items
fun matchItems(
    lostItem: LostItem,
    callback: Callback<List<FoundItem>>

){
    val matchingItemList: List<FoundItem> = listOf()

    // extract all found items from the database
    val firestoreManager = FirestoreManager()

}
