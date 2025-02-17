package com.example.lostandfound.MatchingFunctions

import com.example.lostandfound.Utility.LocationManager
import kotlin.math.cos
import kotlin.math.sqrt

fun getLocationScore(location1: Pair<Double, Double>, location2: Pair<Double, Double>) : Double {
    // assume the longest distance between two points of uni is 1.7km (see LocationManagerTest)
    // assume a linear relationship
    // where any distance greater than the threshold is considered score = 0

    val THRESHOLD = 0.5      // around the distance from CS to SU

    val distance = LocationManager.getDistanceBetweenLocations(location1, location2)
    if (distance > THRESHOLD){
        return 0.0
    }

    return -3*distance/THRESHOLD+3
}
