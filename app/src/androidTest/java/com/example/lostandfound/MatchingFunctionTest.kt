package com.example.lostandfound

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.MatchingFunctions.ScoreDataCallback
import com.example.lostandfound.MatchingFunctions.getMatchingScores
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.CountDownLatch

class MatchingFunctionTest {
    companion object {
        private var context: Context? = null

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            // get the context under test
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            context = appContext
        }
    }


    @Test
    fun testMatchingFunctionOutdated() {

        val dataLost = LostItem(
            itemID = "ABCDE",
            userID = "TYUIO",
            itemName = "test",
            category = "Electronics",
            subCategory = "Calculator",
            color = mutableListOf("Black", "Red"),
            brand = "testBrand",
            dateTime = 1737475452L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "testDesc",
            status = 0,
            isTracking = false,
            timePosted = 1737483828
        )

        val dataFound = FoundItem(
            itemID = "ABCDE",
            userID = "TYUIO",
            itemName = "test",
            category = "Electronics",
            subCategory = "Calculator",
            color = mutableListOf("Black", "Red"),
            brand = "testBrand",
            dateTime = 1736775452L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "testDesc",
            status = 0,
            securityQuestion = "testSecQ",
            securityQuestionAns = "testSecQAns",
            timePosted = 1737483828
        )

        // assert context is not null
        assertNotNull(context)

        // get score
        val latch: CountDownLatch = CountDownLatch(1)
        var thisScoreData: ScoreData = ScoreData()
        getMatchingScores(context!!, dataLost, dataFound, object : ScoreDataCallback {
            override fun onScoreCalculated(scoreData: ScoreData) {
                thisScoreData = scoreData
                latch.countDown()
            }
        })
        latch.await()
        assertEquals(0.0, thisScoreData.overallScore)
    }

    @Test
    fun testMatchingFunctionAllExists() {

    }

    @Test
    fun testMatchingFunctionOnlyCategoryAndCol() {

    }
}