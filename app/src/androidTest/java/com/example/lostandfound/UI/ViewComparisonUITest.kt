package com.example.lostandfound.UI

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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

class ViewComparisonUITest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        // the score is fixed in all examples
        private var dataScore: ScoreData = ScoreData(
            categoryScore = 3.0,
            colorScore = 3.0,
            brandScore = 3.0,
            locationScore = 3.0,
            overallScore = 3.0
        )

        private var userID: String? = null
        private var userID2: String = "qiqiiqioqwio"

        // for the first case (Owner and lost status 0)
        private var dataLost0: LostItem? = null
        private var dataFound0: FoundItem? = null
        private var dataClaim0: Claim? = null

        // for the second case (Owner and claim found = found id)
        private var dataLost1: LostItem? = null
        private var dataFound1: FoundItem? = null
        private var dataClaim1: Claim? = null

        // for the third case (Owner and Else)
        private var dataLost2: LostItem? = null
        private var dataFound2: FoundItem? = null
        private var dataClaim2: Claim? = null

        // for the last case (Not owner)
        private var dataLost3: LostItem? = null
        private var dataFound3: FoundItem? = null
        private var dataClaim3: Claim? = null

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
        // the first case 0
        dataLost0 = LostItem(
            itemID = "2e9j8qirereerjwqiie",
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
            timePosted = 1739941511L,
            status = 0
        )

        dataFound0 = FoundItem(
            itemID = "3rdweereerrerwewre",
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
            timePosted = 1739941511L,
            securityQuestion = "SecQ?",
            securityQuestionAns = "Ansowoowo",
            status = 0
        )

        dataClaim0 = null


        // second case 1
        dataLost1 = LostItem(
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
            timePosted = 1739941511L,
            status = 1
        )

        dataFound1 = FoundItem(
            itemID = "3rdweerrwewre",
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
            timePosted = 1739941511L,
            securityQuestion = "SecQ?",
            securityQuestionAns = "Ansowoowo",
            status = 0
        )

        dataClaim1 = Claim(
            claimID = "209eu902e902e",
            lostItemID = "2e9j8qijwqiie",
            foundItemID = "3rdweerrwewre",
            isApproved = false,
            timestamp = 1738819980L,
            securityQuestionAns = "testAns"
        )


        // second case 2
        dataLost2 = LostItem(
            itemID = "2e9j8qijsddasddaswqiie",
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
            timePosted = 1739941511L,
            status = 1
        )

        dataFound2 = FoundItem(
            itemID = "3rdwadadsdadsadseerrwewre",
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
            timePosted = 1739941511L,
            securityQuestion = "SecQ?",
            securityQuestionAns = "Ansowoowo",
            status = 0
        )

        dataClaim2 = Claim(
            claimID = "209eu902eadssad902e",
            lostItemID = "2e9j8qijsddasddaswqiie",
            foundItemID = "sdsdsddsdsds",
            isApproved = false,
            timestamp = 1738819980L,
            securityQuestionAns = "testAns"
        )


        // the last case 3
        dataLost3 = LostItem(
            itemID = "2e9qiie",
            user = User(
                userID = userID2
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
            timePosted = 1739941511L,
            status = 0
        )

        dataFound3 = FoundItem(
            itemID = "3rdweereerrerwewre",
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
            timePosted = 1739941511L,
            securityQuestion = "SecQ?",
            securityQuestionAns = "Ansowoowo",
            status = 0
        )

        dataClaim3 = null


        // upload the users to firebase firestore
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

        // upload another user
        val dataFoundUser2 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName3",
            FirebaseNames.USERS_LASTNAME to "testLastName3"
        )

        // document the found user id and add it
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userID2)
            .set(dataFoundUser2)
        Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }


    @Test
    fun testOwnerAndLostStatus0() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewComparisonActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost0
                )
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ID,
                    dataFound0
                )
                putExtra(
                    IntentExtraNames.INTENT_CLAIM_ITEM,
                    dataClaim0
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
            dataLost0, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        assertEquals(
            dataFound0, intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ID
            )
        )
        assertEquals(
            dataClaim0, intent.getParcelableExtra(
                IntentExtraNames.INTENT_CLAIM_ITEM
            )
        )

        // assert the claim this item button appears

        composeTestRule.onNodeWithText("Claim this Item").assertExists()
        composeTestRule.onNodeWithText("You have already claimed this item.").assertDoesNotExist()
        composeTestRule.onNodeWithText("You cannot claim this item as you have already claimed another item.")
            .assertDoesNotExist()
    }


    @Test
    fun testOwnerAndClaimFoundIsFound() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewComparisonActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost1
                )
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ID,
                    dataFound1
                )
                putExtra(
                    IntentExtraNames.INTENT_CLAIM_ITEM,
                    dataClaim1
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
            dataLost1, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        assertEquals(
            dataFound1, intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ID
            )
        )
        assertEquals(
            dataClaim1, intent.getParcelableExtra(
                IntentExtraNames.INTENT_CLAIM_ITEM
            )
        )

        // assert the message of already claimed appears

        composeTestRule.onNodeWithText("Claim this Item").assertDoesNotExist()
        composeTestRule.onNodeWithText("You have already claimed this item.").assertExists()
        composeTestRule.onNodeWithText("You cannot claim this item as you have already claimed another item.")
            .assertDoesNotExist()
    }


    @Test
    fun testOwnerAndElse() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewComparisonActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost2
                )
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ID,
                    dataFound2
                )
                putExtra(
                    IntentExtraNames.INTENT_CLAIM_ITEM,
                    dataClaim2
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
            dataLost2, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        assertEquals(
            dataFound2, intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ID
            )
        )
        assertEquals(
            dataClaim2, intent.getParcelableExtra(
                IntentExtraNames.INTENT_CLAIM_ITEM
            )
        )

        // assert the message of already claimed appears
        composeTestRule.onNodeWithText("Claim this Item").assertDoesNotExist()
        composeTestRule.onNodeWithText("You have already claimed this item.").assertDoesNotExist()
        composeTestRule.onNodeWithText("You cannot claim this item as you have already claimed another item.")
            .assertExists()
    }

    @Test
    fun testNotOwner() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewComparisonActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_LOST_ID,
                    dataLost3
                )
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ID,
                    dataFound3
                )
                putExtra(
                    IntentExtraNames.INTENT_CLAIM_ITEM,
                    dataClaim3
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
            dataLost3, intent.getParcelableExtra(
                IntentExtraNames.INTENT_LOST_ID
            )
        )
        assertEquals(
            dataFound3, intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ID
            )
        )
        assertEquals(
            dataClaim3, intent.getParcelableExtra(
                IntentExtraNames.INTENT_CLAIM_ITEM
            )
        )

        // assert the message of already claimed appears
        composeTestRule.onNodeWithText("Claim this Item").assertDoesNotExist()
        composeTestRule.onNodeWithText("You have already claimed this item.").assertDoesNotExist()
        composeTestRule.onNodeWithText("You cannot claim this item as you have already claimed another item.")
            .assertDoesNotExist()
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