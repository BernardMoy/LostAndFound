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
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.foundStatusText
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.ViewFound.ViewFoundActivity
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

class ViewFoundUITest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userID: String? = null
        private var dataFound: FoundItem? = null

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
        dataFound = FoundItem(
            itemID = "3rdweerrwewre",
            userID = userID ?: "",    // use the current user ID
            itemName = "TestItem",
            category = "TestCat",
            subCategory = "TestSubCat",
            color = listOf("Black", "Red"),
            brand = "TestBrand",
            dateTime = 1738819980L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "TestDesc",
            timePosted = 1739941511L,
            securityQuestion = "SecQ?",
            securityQuestionAns = "Ansowoowo"
        )

        // upload the user to firebase firestore
        val dataFoundUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName2",
            FirebaseNames.USERS_LASTNAME to "testLastName2"
        )

        // document the found user id and add it
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userID.toString())
            .set(dataFoundUser)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
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
                ViewFoundActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ID,
                    dataFound
                )
            }

        ActivityScenario.launch<ViewFoundActivity>(intent)
        composeTestRule.waitForIdle()

        // assert the correct intent has been passed
        assertEquals(
            dataFound, intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ID
            )
        )

        Thread.sleep(2000)

        // assert that the unknown image is displayed
        composeTestRule.onNodeWithContentDescription("No image provided").assertIsDisplayed()

        // assert the correct item details are posted
        composeTestRule.onNodeWithText("Reference: #3rdweerrwewre").assertExists()  // ref
        composeTestRule.onNodeWithText("Status: " + foundStatusText[0]) // status
        composeTestRule.onNodeWithTag("ViewFoundName").performScrollTo()
            .assertTextContains("TestItem")
        composeTestRule.onNodeWithTag("ViewFoundCategory").performScrollTo()
            .assertTextContains("TestCat, TestSubCat")
        composeTestRule.onNodeWithTag("ViewFoundDateTime").performScrollTo()
            .assertTextContains("06 Feb 2025 05:33")
        composeTestRule.onNodeWithTag("ViewFoundColor").performScrollTo()
            .assertTextContains("Black, Red")
        composeTestRule.onNodeWithTag("ViewFoundBrand").performScrollTo()
            .assertTextContains("TestBrand")
        composeTestRule.onNodeWithTag("ViewFoundDescription").performScrollTo()
            .assertTextContains("TestDesc")
        composeTestRule.onNodeWithTag("ViewFoundUser").performScrollTo()
            .assertTextContains("testFirstName2 testLastName2 (You)")
        composeTestRule.onNodeWithTag("ViewFoundSecurityQuestion").performScrollTo()
            .assertTextContains("Yes")
        composeTestRule.onNodeWithTag("ViewFoundTimePosted").performScrollTo()
            .assertTextContains("19 Feb 2025 05:05")
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