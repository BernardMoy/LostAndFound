package com.example.lostandfound.MatchingFunctions

import android.content.Context
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData


interface ScoreDataCallback {
    fun onScoreCalculated(scoreData: ScoreData)
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
val WEIGHT_IMAGE = 2              // Low: 1 is the default here
val WEIGHT_CATEGORY = 3          // Higher: Less likely to make mistakes
val WEIGHT_COLOR = 3 // Higher: Less likely to make mistakes with the intelligent algorithm
val WEIGHT_BRAND = 2 // Medium: The same brand may have different representations (Future work)
val WEIGHT_LOCATION = 2         // Medium: The item might be brought elsewhere

val SCORE_THRESHOLD = 1.5  // items will be considered matching if their score is larger than this threshold

/*
Given a lost item and found item, return the map containing an overall score
and also scores that are being considered.

SCORES THAT ARE NOT BEING CONSIDERED WILL NOT APPEAR IN THE MAP (IMAGE, BRAND, LOCATION)

Preconditions are performed in the initial db query:
    1. Lost item user ID != Found item user ID                <-- Implemented in the firebase query
    2. Lost date - found date <= 7 days (Within a week)       <-- Implemented here as firebase don't support this
 */
fun getMatchingScores(
    context: Context,     // used for tflite model classification
    lostItem: LostItem,
    foundItem: FoundItem,
    scoreDataCallback: ScoreDataCallback
) {
    // instantiate result
    val result = ScoreData()

    var sum = 0.0
    var weights = 0.0

    // calculate category
    val scoreCategory = getCategoryScore(
        lostItem.category,
        lostItem.subCategory,
        foundItem.category,
        foundItem.subCategory
    )
    sum += scoreCategory * WEIGHT_CATEGORY
    weights += WEIGHT_CATEGORY
    result.categoryScore = scoreCategory

    // calculate color
    val scoreColor = getColorScore(lostItem.color, foundItem.color)
    sum += scoreColor * WEIGHT_COLOR
    weights += WEIGHT_COLOR
    result.colorScore = scoreColor

    // check if calculate brand
    if (lostItem.brand.isNotEmpty() && foundItem.brand.isNotEmpty()) {
        val scoreBrand = getBrandScore(lostItem.brand, foundItem.brand)
        sum += scoreBrand * WEIGHT_BRAND
        weights += WEIGHT_BRAND
        result.brandScore = scoreBrand
    }

    // check if calculate location
    if (lostItem.location != null && foundItem.location != null) {
        val scoreLocation = getLocationScore(lostItem.location, foundItem.location)
        sum += scoreLocation * WEIGHT_LOCATION
        weights += WEIGHT_LOCATION
        result.locationScore = scoreLocation
    }

    // check if calculate image
    if (lostItem.image.isNotEmpty() && foundItem.image.isNotEmpty()) {
        getImageScore(context, lostItem.image, foundItem.image, object : ImageScoreCallback {
            override fun onScoreCalculated(score: Double) {
                sum += score * WEIGHT_IMAGE
                weights += WEIGHT_IMAGE
                result.imageScore = score

                // also add the OVERALL SCORE AT THE END
                result.overallScore = sum / weights
                scoreDataCallback.onScoreCalculated(result)
            }
        })

    } else {
        // also add the OVERALL SCORE AT THE END
        result.overallScore = sum / weights
        scoreDataCallback.onScoreCalculated(result)
    }
}