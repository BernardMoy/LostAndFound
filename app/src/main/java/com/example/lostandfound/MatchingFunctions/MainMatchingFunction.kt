package com.example.lostandfound.MatchingFunctions

import android.content.Context
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem

// given a lost item and a found item, determine whether they are matched
/*
    Preconditions are performed in the initial db query:
    1. Lost item user ID != Found item user ID
    2. Lost date - found date <= 7 days (Within a week)
 */
fun isMatch(
    context: Context,
    lostItem: LostItem,
    foundItem: FoundItem

): Boolean{
    // return true if the weighted sum >= 1.5 (Each weight is from 0 to 3)
    return getMatchingScore(context = context, lostItem = lostItem, foundItem = foundItem) >= 1.5
}

/*
The following attributes will be considered (From top to bottom of the app UI)

Image (If both are not empty string)
Category and subcategory (Give a 1.5 score if any one of them is Others)
Color (NOT NULLABLE)
Brand (If both are not empty string)
Location (If both are not NULL)

In the worst case only the category and color will contribute to the final score.
 */

// each of the score will have a range between 0 to 3, where 1.5 is the midpoint
// the overall score is the weighted mean of score of each attribute
val WEIGHT_IMAGE = 1              // Low: 1 is the default here
val WEIGHT_CATEGORY = 2.5           // Higher: Less likely to make mistakes
val WEIGHT_COLOR = 2            // Higher: Less likely to make mistakes with the intelligent algorithm
val WEIGHT_BRAND = 1.5            // Medium: The same brand may have different representations (Future work)
val WEIGHT_LOCATION = 1.5         // Medium:

fun getMatchingScore(
    context: Context,     // used for tflite model classification
    lostItem: LostItem,
    foundItem: FoundItem
): Double {
    var sum = 0.0
    var weights = 0.0

    // check if calculate image
    if (lostItem.image.isNotEmpty() && foundItem.image.isNotEmpty()){
        sum += getImageScore(context, lostItem.image, foundItem.image)* WEIGHT_IMAGE
        weights += WEIGHT_IMAGE
    }

    // calculate category
    sum += getCategoryScore(lostItem.category, lostItem.subCategory, foundItem.category, foundItem.subCategory)* WEIGHT_CATEGORY
    weights += WEIGHT_CATEGORY

    // calculate color
    sum += getColorScore(lostItem.color, foundItem.color)* WEIGHT_COLOR
    weights += WEIGHT_COLOR

    // check if calculate brand
    if (lostItem.brand.isNotEmpty() && foundItem.brand.isNotEmpty()){
        sum += getBrandScore(lostItem.brand, foundItem.brand)* WEIGHT_BRAND
        weights += WEIGHT_BRAND
    }

    // check if calculate location
    if (lostItem.location != null && foundItem.location != null){
        sum += getLocationScore(lostItem.location, foundItem.location)* WEIGHT_LOCATION
        weights += WEIGHT_LOCATION
    }

    // return the final weighted sum
    return sum / weights
}

