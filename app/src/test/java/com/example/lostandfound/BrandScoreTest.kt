package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getBrandScore
import org.junit.Test

class BrandScoreTest {
    @Test
    fun testGetBrandScoreValid() {
        assert(
            getBrandScore("Tesco", "tseco") == 3.0
        )
    }

    @Test
    fun testGetBrandScoreInvalid() {
        assert(
            getBrandScore("Tesco", "Tsecw") == 0.0
        )
    }
}