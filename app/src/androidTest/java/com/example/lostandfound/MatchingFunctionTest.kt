package com.example.lostandfound

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.MatchingFunctions.getMatchingScores
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
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
    fun testMatchingFunctionOutdated(){

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


        val dataFound = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test",
            FirebaseNames.LOSTFOUND_USER to "Rwowo",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1736775452L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.FOUND_SECURITY_Q to "testSecQ",
            FirebaseNames.FOUND_SECURITY_Q_ANS to "testSecQAns",
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        // get score
        val latch: CountDownLatch = CountDownLatch(1)
        getMatchingScores(context, dataLost, dataFound)
    }

    @Test
    fun testMatchingFunctionAllExists(){

    }

    @Test
    fun testMatchingFunctionOnlyCategoryAndCol(){

    }
}