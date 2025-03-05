package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.ui.NewLost.NewLostScreen
import com.example.lostandfound.ui.Search.SearchActivity
import com.example.lostandfound.ui.Search.SearchScreen
import com.example.lostandfound.ui.Search.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class SearchActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userID: String? = null
        private var dataLost: LostItem? = null

    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SearchActivity>()


    @Before
    fun setUp() {
        // create and sign in test user
        val email = "test@warwick"
        val password = "1234ABCde"

        val latch: CountDownLatch = CountDownLatch(1)
        auth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth!!.currentUser
                if (user != null) {
                    userID = user.uid
                }
                latch.countDown()
            } else {
                fail("Failed while creating a user")
                latch.countDown()
            }
        }
        latch.await()  // wait for user creation

        // after user creation, the user is logged in
        // assert current user is not null
        assertNotNull(auth!!.currentUser)


        // create item details
        dataLost = LostItem(
            itemID = "2e9j8qijwqiie",
            userID = userID ?: "",    // use the current user ID
            itemName = "TestItem",
            category = "TestCat",
            subCategory = "TestSubCat",
            color = listOf("Black", "Red"),
            brand = "TestBrand",
            dateTime = 1738819980L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "TestDesc",
            isTracking = false,
            timePosted = 1739941511L
        )
    }

    @Test
    fun testSearch() {

        val intent = Intent(ApplicationProvider.getApplicationContext(), SearchActivity::class.java).apply {
            putExtra(
                IntentExtraNames.INTENT_LOST_ID,
                dataLost
            )
        }

        ActivityScenario.launch<SearchActivity>(intent)
        composeTestRule.waitForIdle()

        // WAIT FOR 5S for the results to load
        Thread.sleep(5000)

        // assert the correct intent has been passed
        assertEquals(dataLost, intent.getParcelableExtra(
            IntentExtraNames.INTENT_LOST_ID
        ))

        // assert the search result exists



    }

    @After
    fun tearDown() {
        // clear all data in firestore

        // logout the user
    }
}