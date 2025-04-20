package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getLocationScore
import com.example.lostandfound.Utility.LocationManager
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Test
import kotlin.math.abs

class LocationScoreTest {
    val locationPair = Pair(52.38040320697202, -1.5605640104046208)
    val locationLatLng = LatLng(52.38040320697202, -1.5605640104046208)
    val locationHashMap = hashMapOf(
        "latitude" to 52.38040320697202,
        "longitude" to -1.5605640104046208
    )
    @Test
    fun testGetLocationScore() {
        val score = getLocationScore(
            Pair(52.38040320697202, -1.5605640104046208),
            Pair(52.383647967653, -1.5599945903312389)
        )

        // the score should be 0.28
        assert(
            abs(0.28 - score) <= 1e-1
        )
    }

    @Test
    fun testGetLocationScoreTooLong(){
        val score = getLocationScore(
            Pair(52.38040320697202, -1.5605640104046208),
            Pair(51.383647967653, -2.99945903312389)
        )

        // 0 because larger than threshold
        Assert.assertEquals(0.0, score, 0.0001)
    }

    @Test
    fun testPairToLatLng(){
        Assert.assertEquals(locationLatLng, LocationManager.pairToLatlng(locationPair))
    }

    @Test
    fun testLatLngToPair(){
        Assert.assertEquals(locationPair, LocationManager.latLngToPair(locationLatLng))
    }

    @Test
    fun testLocationToPair(){
        Assert.assertEquals(locationPair, LocationManager.LocationToPair(locationHashMap))
    }
}