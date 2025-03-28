package com.example.lostandfound.Integration

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.ViewClaim.ViewClaimActivity
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

class NotificationType1and2Test : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userLostID: String = "SIWSJI2332"
        private var userLost2ID: String = "289wio2idw"
        private var userFoundID: String? = null
        private var lostID: String = "duwindwmw"
        private var lost2ID: String = "2e892iiww"
        private var foundID: String = "2e9j8qijwqiie"
        private var claimID: String = "928je29e"
        private var claim2ID: String = "3289e99e"
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
                    userFoundID = user.uid  // you are logged in as the found user
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


        val dataLostUser1 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to "testEmail",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName",
            FirebaseNames.USERS_LASTNAME to "testLastName"
        )
        val taskU1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userLostID)
            .set(dataLostUser1)
        Tasks.await(taskU1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        val dataLostUser2 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to "testEmail",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName",
            FirebaseNames.USERS_LASTNAME to "testLastName"
        )
        val taskU2 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userLost2ID)
            .set(dataLostUser2)
        Tasks.await(taskU2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        val dataFoundUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to "testEmail2",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName2",
            FirebaseNames.USERS_LASTNAME to "testLastName2"
        )
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userFoundID.toString())
            .set(dataFoundUser)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // add the lost item and found item
        // the item to have claim approved
        val dataLost1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "TestItem",
            FirebaseNames.LOSTFOUND_USER to userLostID,
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
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .document(lostID.toString()).set(dataLost1)
        Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // the item to have claim not approved
        val dataLost2 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "TestItem2",
            FirebaseNames.LOSTFOUND_USER to userLost2ID,
            FirebaseNames.LOSTFOUND_CATEGORY to "TestCat2",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "TestSubCat2",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "TestBrand2",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "TestDesc2",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        // Post the items
        val task5 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .document(lost2ID.toString()).set(dataLost2)
        Tasks.await(task5, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // the found item
        val dataFound1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "TestItem",
            FirebaseNames.LOSTFOUND_USER to userFoundID.toString(),
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
        val task3 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
            .document(foundID.toString()).set(dataFound1)
        Tasks.await(task3, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

        // create claim data and post it
        // claim of lost item 1 to found item
        val dataClaim1 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739941511L,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lostID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to foundID.toString(),
            FirebaseNames.CLAIM_IS_APPROVED to false,
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to ""
        )
        val task4 = firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
            .document(claimID.toString()).set(dataClaim1)
        Tasks.await(task4, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // claim of lost item 2 to found item
        val dataClaim2 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739941511L,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost2ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to foundID.toString(),
            FirebaseNames.CLAIM_IS_APPROVED to false,
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to ""
        )
        val task6 = firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
            .document(claim2ID.toString()).set(dataClaim2)
        Tasks.await(task6, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // set the claim item (Activity intent to view claim)
        dataClaim = Claim(
            claimID = claimID.toString(),
            lostItemID = lostID.toString(),
            foundItemID = foundID.toString(),
            isApproved = false,     // initially is approved is false
            timestamp = 1739941511L,
            securityQuestionAns = ""
        )
    }

    /*
    Test that approving a claim generates a type 1 notification to the lost user
    and generates a type 2 notification to other users
     */
    @Test
    fun testNotificationYourClaimApproved() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewClaimActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_CLAIM_ITEM,
                    dataClaim
                )
            }

        ActivityScenario.launch<ViewClaimActivity>(intent)
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // assert the correct intent has been passed
        assertEquals(
            dataClaim, intent.getParcelableExtra(
                IntentExtraNames.INTENT_CLAIM_ITEM
            )
        )

        // click the "Approve this claim" button
        composeTestRule.onNodeWithText("Approve this Claim").performScrollTo().performClick()
        Thread.sleep(3000)

        // click the approve button in the pop up dialog
        composeTestRule.onNodeWithText("Approve").performClick()
        Thread.sleep(2000)

        // assert a notification of type 1 is sent to the lost user
        val latch = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
            .whereEqualTo(FirebaseNames.NOTIFICATION_TYPE, 1)
            .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, userLostID)
            .whereEqualTo(FirebaseNames.NOTIFICATION_CLAIM_ID, claimID)
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

        // assert a notification of type 2 is sent to the other lost user
        val latch2 = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
            .whereEqualTo(FirebaseNames.NOTIFICATION_TYPE, 2)
            .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, userLost2ID)
            .whereEqualTo(FirebaseNames.NOTIFICATION_CLAIM_ID, claim2ID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // assert only one item exists
                assertEquals(1, querySnapshot.size())

                // countdown
                latch2.countDown()

            }
            .addOnFailureListener { e ->
                fail("Failed during db query")
                latch.countDown()
            }

        latch2.await(60, TimeUnit.SECONDS)
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