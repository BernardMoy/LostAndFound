package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getCategoryScore
import org.junit.Test

class CategoryScoreTest{
    @Test
    fun testGetCategoryScore(){
        assert(
            getCategoryScore("Personal items", "Wallet",
                "Personal items", "ID") == 1.5
        )

        assert(
            getCategoryScore("Personal items", "Wallet",
                "Personal items", "Wallet") == 3.0
        )

        assert(
            getCategoryScore("Personal items", "Wallet",
                "Electronics", "Wallet") == 0.0      // category need to be same first!
        )
    }
}