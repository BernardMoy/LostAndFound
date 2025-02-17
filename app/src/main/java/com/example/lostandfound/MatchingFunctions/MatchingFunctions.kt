package com.example.lostandfound.MatchingFunctions

import android.content.Context
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem

// given a lost item and a found item, determine whether they are matched
/*
    Preconditions are performed in the initial db query:
    1. Lost item user ID != Found item user ID
    2. Lost date - found date < 7 days (Within a week)
 */
fun isMatch(
    lostItem: LostItem,
    foundItem: FoundItem

): Boolean{
    /* TODO implement this */

    // currently, return true if their users are different
    return true
}

fun getMatchingScore(
    context: Context,     // used for tflite model classification
    lostItem: LostItem,
    foundItem: FoundItem
){

}



// only consider the image when it is not the placeholder image.
// As the lost and found OBJECTS may contain that image!

