package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.areWordsSimilar
import org.junit.Test

class WordSimilarityTest {
    @Test
    fun testSameWord() {
        assert(
            areWordsSimilar("apple", "Apple")
        )
    }

    @Test
    fun testDifferentWords() {
        assert(
            !areWordsSimilar("apple", "apkpe")
        )
        assert(
            !areWordsSimilar("apple", "appoes")
        )
    }

    @Test
    fun testDifferentWords2() {
        assert(
            !areWordsSimilar("apple", "appleapple")
        )
    }

    @Test
    fun testInversion() {
        assert(
            areWordsSimilar("apple", "appel")
        )
        assert(
            areWordsSimilar("Tense", "tnese     ")
        )
        assert(
            !areWordsSimilar("abc", "cba")
        )
    }

    @Test
    fun testDeletion() {
        assert(
            areWordsSimilar("apple", "pple")
        )
        assert(
            areWordsSimilar("apple", "aple")
        )
    }

    @Test
    fun testAddition() {
        assert(
            areWordsSimilar("apple", "apples")
        )
    }

    @Test
    fun testSubstitution() {
        assert(
            areWordsSimilar("apple", "appme")
        )
        assert(
            areWordsSimilar("apple", "applw")
        )
        assert(
            !areWordsSimilar("apple", "applws")
        )
    }

    @Test
    fun testTwoMoreDifference() {
        assert(
            !areWordsSimilar("apple", "aooke")
        )
    }
}