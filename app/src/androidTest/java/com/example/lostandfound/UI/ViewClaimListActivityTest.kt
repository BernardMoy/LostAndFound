package com.example.lostandfound.UI

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.ViewClaim.ViewClaimActivity
import com.example.lostandfound.ui.ViewClaimList.ViewClaimListActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ViewClaimListActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userID: String? = null
        private var found1ID: String? = null
        private var lost1ID: String? = null
        private var lost2ID: String? = null

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
                    userID = user.uid  // you are logged in as the found user
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

        val dataFound1 = mutableMapOf<String, Any>(
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
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
            .add(dataFound1)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        found1ID = ref1.id
        Thread.sleep(2000)

        // create two lost items, each claimed the found item
        val dataLost1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "lostname1",
            FirebaseNames.LOSTFOUND_USER to userID.toString(),
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat2",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat2",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand2",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc2",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost1)
        val ref2 = Tasks.await(task2, 60, TimeUnit.SECONDS)
        lost1ID = ref2.id
        Thread.sleep(2000)

        val dataLost2 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "lostname2",
            FirebaseNames.LOSTFOUND_USER to userID.toString(),
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat2",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat2",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand2",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc2",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        val task3 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost2)
        val ref3 = Tasks.await(task3, 60, TimeUnit.SECONDS)
        lost2ID = ref3.id
        Thread.sleep(2000)


        // claims
        // create claims based on the ids
        val dataClaim1 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739942998L,
            FirebaseNames.CLAIM_IS_APPROVED to true,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost1ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to found1ID.toString(),
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to ""
        )

        val dataClaim2 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739942555L,
            FirebaseNames.CLAIM_IS_APPROVED to false,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost2ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to found1ID.toString(),
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to "claim3Text"
        )

        // Post the claims
        val task5 =
            firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaim1)
        val ref5 = Tasks.await(task5, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

        val task6 =
            firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaim2)
        val ref6 = Tasks.await(task6, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

    }

    /*
    Test that a claim can be approved using the "Approve this Claim" button
     */
    @Test
    fun testViewClaimListUI() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewClaimListActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ITEM,
                    FoundItem(
                        itemID = found1ID.toString()  // only the string is needed
                    )
                )
            }

        ActivityScenario.launch<ViewClaimActivity>(intent)
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // assert the correct intent has been passed
        assertNotNull(
            intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ITEM
            )
        )

        // assert the two lost items are displayed on the screen
        composeTestRule.onNodeWithText("lostname1").assertExists()
        composeTestRule.onNodeWithText("lostname2").assertExists()

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