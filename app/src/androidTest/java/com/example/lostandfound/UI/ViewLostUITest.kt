package com.example.lostandfound.UI

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.ViewLost.ViewLostActivity
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

class ViewLostUITest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userID: String? = null
        private var userID2: String = "e299e2e20e2"

        private var dataLost0: LostItem? = null  // status 0
        private var dataLost1: LostItem? = null
        private var dataLost2: LostItem? = null
        private var dataLostOtherUser: LostItem? = null

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
        dataLost0 = LostItem(
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
            timePosted = 1739941511L,
            status = 0
        )

        // also create lost item of status 1 and 2
        dataLost1 = LostItem(
            itemID = "2e9j8qijwqiie5",
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
            timePosted = 1739941511L,
            status = 1
        )

        dataLost2 = LostItem(
            itemID = "2e9j8qijwqiie3",
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
            timePosted = 1739941511L,
            status = 2
        )

        dataLostOtherUser = LostItem(
            itemID = "2e9j8qijwqiie2",
            userID = userID2,    // use the current user ID
            itemName = "TestItem",
            category = "TestCat",
            subCategory = "TestSubCat",
            color = listOf("Black", "Red"),
            brand = "TestBrand",
            dateTime = 1738819980L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "TestDesc",
            isTracking = false,
            timePosted = 1739941511L,
            status = 0
        )


        // upload the user to firebase firestore
        val dataLostUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName2",
            FirebaseNames.USERS_LASTNAME to "testLastName2"
        )

        // document the found user id and add it
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userID.toString())
            .set(dataLostUser)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

        // upload another user
        val dataLostUser2 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName3",
            FirebaseNames.USERS_LASTNAME to "testLastName3"
        )

        // document the found user id and add it
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userID2)
            .set(dataLostUser2)
        Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    /*
    Unable to isolate tests, as this will require isolating the intent
    but this cannot be done because need to wait until the lost item is created
    and directly isolating them will lead to the application interface being relaunched
    multiple times
     */
    @Test
    fun testCorrectItemDetails() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewLostActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost0
                )
            }

        ActivityScenario.launch<ViewLostActivity>(intent)
        composeTestRule.waitForIdle()

        // assert the correct intent has been passed
        assertEquals(
            dataLost0, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )

        // assert the correct item details are posted
        Thread.sleep(2000)

        // assert that the unknown image is displayed
        composeTestRule.onNodeWithContentDescription("No image provided").assertIsDisplayed()

        // assert the correct item details are posted
        composeTestRule.onNodeWithText("Reference: #2e9j8qijwqiie").assertExists()  // ref
        composeTestRule.onNodeWithText("This item is not being tracked")
            .assertExists() // is tracking
        composeTestRule.onNodeWithText("Status: " + foundStatusText[0]) // status
        composeTestRule.onNodeWithTag("ViewLostName").performScrollTo()
            .assertTextContains("TestItem")
        composeTestRule.onNodeWithTag("ViewLostCategory").performScrollTo()
            .assertTextContains("TestCat, TestSubCat")
        composeTestRule.onNodeWithTag("ViewLostDateTime").performScrollTo()
            .assertTextContains("06 Feb 2025 05:33")
        composeTestRule.onNodeWithTag("ViewLostColor").performScrollTo()
            .assertTextContains("Black, Red")
        composeTestRule.onNodeWithTag("ViewLostBrand").performScrollTo()
            .assertTextContains("TestBrand")
        composeTestRule.onNodeWithTag("ViewLostDescription").performScrollTo()
            .assertTextContains("TestDesc")
        composeTestRule.onNodeWithTag("ViewLostUser").performScrollTo()
            .assertTextContains("testFirstName2 testLastName2 (You)")
        composeTestRule.onNodeWithTag("ViewLostTimePosted").performScrollTo()
            .assertTextContains("19 Feb 2025 05:05")
    }


    /*
    Test whether certain buttons and messages appear under some conditions.
     */
    @Test
    fun testOwnerAndStatus0() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewLostActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost0
                )
            }
        ActivityScenario.launch<ViewLostActivity>(intent)
        composeTestRule.waitForIdle()
        // assert the correct intent has been passed
        assertEquals(
            dataLost0, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        // assert the correct item details are posted
        Thread.sleep(2000)

        // assert the delete button exists
        composeTestRule.onNodeWithText("Delete item").assertExists()
        composeTestRule.onNodeWithText("View matching items").assertExists()
        composeTestRule.onNodeWithText("View claim").assertDoesNotExist()
    }

    @Test
    fun testOwnerAndStatus1() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewLostActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost1
                )
            }
        ActivityScenario.launch<ViewLostActivity>(intent)
        composeTestRule.waitForIdle()
        // assert the correct intent has been passed
        assertEquals(
            dataLost1, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        // assert the correct item details are posted
        Thread.sleep(2000)

        // assert the delete button exists
        composeTestRule.onNodeWithText("View claim").assertExists()
        composeTestRule.onNodeWithText("View matching items").assertExists()
        composeTestRule.onNodeWithText("Delete item").assertDoesNotExist()
    }

    @Test
    fun testOwnerAndStatus2() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewLostActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost2
                )
            }
        ActivityScenario.launch<ViewLostActivity>(intent)
        composeTestRule.waitForIdle()
        // assert the correct intent has been passed
        assertEquals(
            dataLost2, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        // assert the correct item details are posted
        Thread.sleep(2000)

        // assert the delete button exists
        composeTestRule.onNodeWithText("View claim").assertExists()
        composeTestRule.onNodeWithText("View matching items").assertDoesNotExist()
        composeTestRule.onNodeWithText("Delete item").assertDoesNotExist()
    }

    @Test
    fun testNotOwner() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewLostActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLostOtherUser
                )
            }
        ActivityScenario.launch<ViewLostActivity>(intent)
        composeTestRule.waitForIdle()
        // assert the correct intent has been passed
        assertEquals(
            dataLostOtherUser, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        // assert the correct item details are posted
        Thread.sleep(2000)

        // assert the delete button exists
        composeTestRule.onNodeWithText("View claim").assertDoesNotExist()
        composeTestRule.onNodeWithText("View matching items").assertDoesNotExist()
        composeTestRule.onNodeWithText("Delete item").assertDoesNotExist()
    }

    @After
    fun tearDown() {
        deleteCollection(FirebaseNames.COLLECTION_USERS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}