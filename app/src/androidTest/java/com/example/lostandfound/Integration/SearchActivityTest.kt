package com.example.lostandfound.Integration

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.Search.SearchActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SearchActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userID: String? = null
        private var dataLost: LostItem? = null

    }

    @get:Rule
    val composeTestRule = createComposeRule()


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

        // post a very similar found item
        val dataFound1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "298heh29",
            FirebaseNames.LOSTFOUND_USER to "sauqsiqis",  // different user id
            FirebaseNames.LOSTFOUND_CATEGORY to "TestCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "TestSubCat",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "TestBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941513L
        )

        // Post the items
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound1)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    @Test
    fun testSearch() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), SearchActivity::class.java).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost
                )
            }

        ActivityScenario.launch<SearchActivity>(intent)
        composeTestRule.waitForIdle()

        // assert the correct intent has been passed
        assertEquals(
            dataLost, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )

        // assert the search result exists
        Thread.sleep(4000)
        composeTestRule.onNodeWithText("298heh29").assertExists()  // the found item name
    }

    @After
    fun tearDown() {
        deleteCollection(FirebaseNames.COLLECTION_FOUND_ITEMS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}