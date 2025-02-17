package com.example.lostandfound.MatchingFunctions

import android.content.Context
import android.net.Uri
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.TFLiteManager.ImageClassifier
import kotlin.math.pow

fun getImageScore(context: Context, lostItem: LostItem, foundItem: FoundItem): Double{
    // get the contrastive loss distance
    val classifier: ImageClassifier = ImageClassifier(context)
    val distance = classifier.predict(
        Uri.parse(lostItem.image),
        Uri.parse(foundItem.image)
    )

    // output the score using the formula score = 6/(1+e^(N*distance))
    // where N is how steep the curve is
    // 6 is to make the score = 3 when the distance = 0
    val N = 2
    val score = 6/(1+ Math.E.pow(((N * distance).toDouble())))

    return score
}