package com.example.lostandfound.MatchingFunctions

import kotlin.math.min

fun getColorScore(lostColors: List<String>, foundColors: List<String>): Double {
    // convert the lists to sets
    val lostSet = lostColors.toSet()
    val foundSet = foundColors.toSet()

    // the score is defined as the number of intersection / length of the smaller set
    // so that subsets would still give a full score of 1
    // as some people may recognise an object as red, while others red and blue.
    val numIntersection = lostSet.intersect(foundSet).size
    return numIntersection / min(lostSet.size, foundSet.size).toDouble()
}