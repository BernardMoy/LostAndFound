package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getLocationScore
import org.junit.Test
import kotlin.math.abs

class LocationScoreTest {
    @Test
    fun testGetLocationScore() {
        val score = getLocationScore(
            Pair(52.38040320697202, -1.5605640104046208),
            Pair(52.383647967653, -1.5599945903312389)
        )

        // the distance should be 0.84
        println(score)
        assert(
            abs(0.84 - score) <= 1e-1
        )
    }
}