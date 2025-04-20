package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getScoreFromDistance
import org.junit.Test
import kotlin.math.abs

class ImageSimilarityScoreTest {
    @Test
    fun testGetScoreFromDistance() {
        val score = getScoreFromDistance(0.8, N = 3.0)
        assert(abs(score - 0.16634) <= 1e-3)
    }
}