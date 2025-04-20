package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getColorScore
import org.junit.Test

class ColorScoreTest {
    @Test
    fun testGetColorScoreSame() {
        assert(
            getColorScore(listOf("Red", "Green", "Blue"), listOf("Green", "Blue", "Red")) == 1.0
        )
    }

    @Test
    fun testGetColorScoreOneIsTheSubsetOfTheOther() {
        assert(
            getColorScore(listOf("Red", "Green", "Blue"), listOf("Red")) == 1.0
        )
    }

    @Test
    fun testGetColorScoreHavePenalty() {
        assert(
            getColorScore(listOf("Red", "Green", "Blue"), listOf("Yellow")) == 0.0
        )
        assert(
            getColorScore(listOf("Red", "Green", "Blue"), listOf("Red", "Green", "Yellow")) == 2.0/3.0
        )
        assert(
            getColorScore(listOf("Red", "Green", "Blue"), listOf("Red", "Purple", "Yellow")) == 1.0/3.0
        )
    }
}