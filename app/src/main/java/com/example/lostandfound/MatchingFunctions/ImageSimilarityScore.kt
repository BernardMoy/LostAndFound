package com.example.lostandfound.MatchingFunctions

import android.content.Context
import android.net.Uri
import com.example.lostandfound.TFLiteManager.ImageClassifier
import com.example.lostandfound.TFLiteManager.PredictCallback
import kotlin.math.pow

interface ImageScoreCallback {
    fun onScoreCalculated(score: Double)
}

fun getImageScore(
    context: Context,
    lostImage: String,
    foundImage: String,
    callback: ImageScoreCallback
) {
    // get the contrastive loss distance
    val classifier = ImageClassifier(context)
    classifier.predict(
        Uri.parse(lostImage),
        Uri.parse(foundImage),
        object : PredictCallback {
            override fun onComplete(distance: Float) {
                // turn the distance into score
                val score = getScoreFromDistance(distance.toDouble())

                // return as callback
                callback.onScoreCalculated(score)
            }
        }
    )

}

fun getScoreFromDistance(d: Double, N: Double = 2.2): Double {
    // output the score using the formula score = 6/(1+e^(N*distance))
    // where N is how steep the curve is
    // 6 is to make the score = 3 when the distance = 0
    // Designed by flipping horizontally the sigmoid function and than making the y intercept 3 -> Smooth relation

    // The function would ideally give score = 1.5 when distance = 0.5
    val score = 6 / (1 + Math.E.pow(N * d))

    return score
}