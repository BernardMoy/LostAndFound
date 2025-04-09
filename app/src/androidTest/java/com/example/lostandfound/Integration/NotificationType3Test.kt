package com.example.lostandfound.Integration

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.ViewComparison.ViewComparisonActivity
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

class NotificationType3Test : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userID: String? = null     // the current logged in (Lost) user
        private var dataLost: LostItem? = null
        private var dataFound: FoundItem? = null
        private var dataScore: ScoreData? = null
        private var dataClaim: Claim? = null   // these are the intents required

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
            user = User(
                userID = userID ?: ""
            ),    // use the current user ID
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
        dataFound = FoundItem(
            itemID = "2e9j8erwrwrw",
            user = User(
                userID = "aakkaka"
            ),    // different uid
            itemName = "TestItem",
            category = "TestCat",
            subCategory = "TestSubCat",
            color = listOf("Black", "Red"),
            brand = "TestBrand",
            dateTime = 1738819980L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "TestDesc",
            timePosted = 1739941511L
        )

        // keep dataClaim null

        // create a 100% score data
        dataScore = ScoreData(
            categoryScore = 3.0,
            colorScore = 3.0,
            brandScore = 3.0,
            locationScore = 3.0,
            overallScore = 3.0
        )

        // post an user entry of the found user in firestore. this is because it is required in fetching data
        val dataFoundUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to "testEmail",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName",
            FirebaseNames.USERS_LASTNAME to "testLastName"
        )

        // document the found user id and add it
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document("duwindwmw")
            .set(dataFoundUser)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

        // add the lost item and found item
        val dataLost1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "TestItem",
            FirebaseNames.LOSTFOUND_USER to userID.toString(),
            FirebaseNames.LOSTFOUND_CATEGORY to "TestCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "TestSubCat",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "TestBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "TestDesc",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        // Post the items
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .document("2e9j8qijwqiie").set(dataLost1)
        Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

        val dataFound1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "TestItem",
            FirebaseNames.LOSTFOUND_USER to "duwindwmw",
            FirebaseNames.LOSTFOUND_CATEGORY to "TestCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "TestSubCat",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "TestBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "TestDesc",
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L,
            FirebaseNames.FOUND_SECURITY_Q to "TestSecQ",
            FirebaseNames.FOUND_SECURITY_Q_ANS to "TestSecQAns"
        )

        // Post the items
        val task3 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
            .document("2e9j8erwrwrw").set(dataFound1)
        Tasks.await(task3, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    /*
    Test that a notification is sent to the found user when the current user claimed the item
     */
    @Test
    fun testNotificationClaimedItem() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewComparisonActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost
                )
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ID,
                    dataFound
                )
                putExtra(
                    IntentExtraNames.INTENT_CLAIM_ITEM,
                    dataClaim
                )
                putExtra(
                    IntentExtraNames.INTENT_SCORE_DATA,
                    dataScore
                )
            }

        ActivityScenario.launch<ViewComparisonActivity>(intent)
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // assert the correct intent has been passed
        assertEquals(
            dataLost, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        assertEquals(
            dataFound, intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ID
            )
        )

        // try clicking on the claim button
        composeTestRule.onNodeWithText("Claim this Item").performScrollTo().performClick()
        Thread.sleep(2000)


        // assert a notification entry exist in the database
        val latch = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
            .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, "duwindwmw")
            .whereEqualTo(FirebaseNames.NOTIFICATION_TYPE, 3)
            .whereNotEqualTo(FirebaseNames.NOTIFICATION_CLAIM_ID, null)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // assert only one item exists
                assertEquals(1, querySnapshot.size())

                // countdown
                latch.countDown()
            }
            .addOnFailureListener { e ->
                fail("Failed during db query")
                latch.countDown()
            }

        latch.await(60, TimeUnit.SECONDS)
    }

    @After
    fun tearDown() {
        deleteCollection(FirebaseNames.COLLECTION_LOST_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_FOUND_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_NOTIFICATIONS)
        deleteCollection(FirebaseNames.COLLECTION_USERS)
        deleteCollection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}