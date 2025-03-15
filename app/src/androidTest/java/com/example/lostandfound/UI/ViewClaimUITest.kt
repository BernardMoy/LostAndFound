package com.example.lostandfound.UI

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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

class ViewClaimUITest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userLostID: String = "SIWSJI2332"
        private var userFoundID: String? = null

        // for the first case (Found user is current user and status 1)
        private var lost2ID: String? = null
        private var lost3ID: String? = null
        private var found1ID: String? = null
        private var claimL2F1: String? = null
        private var claimL3F1: String? = null

        private var claim1: Claim? = null
        private var claim2: Claim? = null

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
                    userFoundID = user.uid
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


        // post an user entry of the found user in firestore. this is because it is required in fetching data
        val dataLostUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to "testEmail",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName",
            FirebaseNames.USERS_LASTNAME to "testLastName"
        )

        // document the found user id and add it
        val task0 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userLostID)
            .set(dataLostUser)
        Tasks.await(task0, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

        val dataFoundUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to "testEmail2",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName2",
            FirebaseNames.USERS_LASTNAME to "testLastName2"
        )

        // document the found user id and add it
        val taska1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userFoundID.toString())
            .set(dataFoundUser)
        Tasks.await(taska1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // add the lost item and found item
        val dataLost2 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test2",
            FirebaseNames.LOSTFOUND_USER to userLostID,
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

        val dataLost3 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test3",
            FirebaseNames.LOSTFOUND_USER to userLostID,
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat3",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat3",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand3",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc3",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        val dataFound1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test",
            FirebaseNames.LOSTFOUND_USER to userFoundID.toString(),
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.FOUND_SECURITY_Q to "testSecQ",
            FirebaseNames.FOUND_SECURITY_Q_ANS to "testSecQAns",
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )


        // Post the items
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound1)
        val ref2 = Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        found1ID = ref2.id

        val task3 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost2)
        val ref3 = Tasks.await(task3, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        lost2ID = ref3.id

        val task4 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost3)
        val ref4 = Tasks.await(task4, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        lost3ID = ref4.id


        // create claims based on the ids
        val dataClaimL2F1 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739942998L,
            FirebaseNames.CLAIM_IS_APPROVED to false,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost2ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to found1ID.toString(),
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to ""
        )

        val dataClaimL3F1 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739942555L,
            FirebaseNames.CLAIM_IS_APPROVED to false,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost3ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to found1ID.toString(),
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to "claim3Text"
        )


        // Post the claims
        val task5 =
            firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaimL2F1)
        val ref5 = Tasks.await(task5, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        claimL2F1 = ref5.id

        val task6 =
            firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaimL3F1)
        val ref6 = Tasks.await(task6, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        claimL3F1 = ref6.id


        claim1 = Claim(
            claimID = claimL2F1.toString(),
            lostItemID = lost2ID.toString(),
            foundItemID = found1ID.toString(),
            isApproved = false,
            timestamp = 23909239L,
            securityQuestionAns = "siswi"
        )

        claim2 = Claim(
            claimID = claimL3F1.toString(),
            lostItemID = lost3ID.toString(),
            foundItemID = found1ID.toString(),
            isApproved = false,
            timestamp = 23909239L,
            securityQuestionAns = "siswi"
        )

    }


    @Test
    fun testFoundUserIsCurrentUserAndStatus1() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewClaimActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_CLAIM_ITEM,
                    claim1
                )
            }

        ActivityScenario.launch<ViewClaimActivity>(intent)
        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // assert the correct intent has been passed
        assertEquals(
            claim1, intent.getParcelableExtra(
                IntentExtraNames.INTENT_CLAIM_ITEM
            )
        )

        composeTestRule.onNodeWithText("Approve this Claim").assertExists()
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