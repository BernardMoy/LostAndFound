package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getBrandScore
import com.example.lostandfound.MatchingFunctions.getColorScore
import com.example.lostandfound.MatchingFunctions.getScoreFromDistance
import org.junit.Test
import kotlin.math.abs

class BrandScoreTest {
    @Test
    fun testGetBrandScoreValid(){
        assert(
            getBrandScore("Tesco", "tseco") == 3.0
        )
    }

    @Test
    fun testGetBrandScoreInvalid(){
        assert(
            getBrandScore("Tesco", "Tsecw") == 0.0
        )
    }
}