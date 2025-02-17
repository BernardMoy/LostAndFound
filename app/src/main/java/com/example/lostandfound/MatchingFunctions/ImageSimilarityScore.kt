package com.example.lostandfound.MatchingFunctions

import android.content.Context
import android.net.Uri
import com.example.lostandfound.TFLiteManager.ImageClassifier
import kotlin.math.pow

fun getImageScore(context: Context, lostImage: String, foundImage: String): Double {
    // get the contrastive loss distance
    val classifier: ImageClassifier = ImageClassifier(context)
    val distance = classifier.predict(
        Uri.parse(lostImage),
        Uri.parse(foundImage)
    )
    return getScoreFromDistance(distance.toDouble())
}

fun getScoreFromDistance(d: Double, N: Double = 3.0): Double {
    // output the score using the formula score = 6/(1+e^(N*distance))
    // where N is how steep the curve is
    // 6 is to make the score = 3 when the distance = 0
    // Designed by flipping horizontally the sigmoid function and than making the y intercept 3 -> Smooth relation
    val score = 6 / (1 + Math.E.pow(((N * d).toDouble())))

    return score
}