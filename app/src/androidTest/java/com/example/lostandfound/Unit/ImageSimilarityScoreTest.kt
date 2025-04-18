package com.example.lostandfound.Unit

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lostandfound.MatchingFunctions.ImageScoreCallback
import com.example.lostandfound.MatchingFunctions.getImageScore
import com.example.lostandfound.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ImageSimilarityScoreTest {
    @Test
    fun testGetImageScore() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // read the lost and found calc images
        val lostPath = "android.resource://" + context.packageName + "/" + R.drawable.calc_lost
        val foundPath = "android.resource://" + context.packageName + "/" + R.drawable.calc_found

        // get the image matching scores
        val latch = CountDownLatch(1)
        var thisScore = 0.0
        getImageScore(context, lostPath, foundPath, object : ImageScoreCallback {
            override fun onScoreCalculated(score: Double) {
                thisScore = score
                latch.countDown()
            }
        })
        latch.await(60, TimeUnit.SECONDS)

        // assert the score is within expected range
        Assert.assertEquals(2.7, thisScore, 0.1)
    }
}