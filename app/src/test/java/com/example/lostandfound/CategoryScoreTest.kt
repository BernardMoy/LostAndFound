package com.example.lostandfound

import com.example.lostandfound.MatchingFunctions.getCategoryScore
import org.junit.Test

class CategoryScoreTest{
    @Test
    fun testGetCategoryScore(){
        assert(
            getCategoryScore("Personal items", "Wallet",
                "Personal items", "ID") == 0.0
        )

        assert(
            getCategoryScore("Personal items", "Wallet",
                "Personal items", "Wallet") == 3.0
        )

        assert(
            getCategoryScore("Personal items", "Wallet",
                "Electronics", "Wallet") == 0.0      // category need to be same first!
        )

        assert(
            getCategoryScore("Others", "ABC",
                "Others", "DEF") == 3.0   // as long as both are others, 3 is returned
        )
    }
}