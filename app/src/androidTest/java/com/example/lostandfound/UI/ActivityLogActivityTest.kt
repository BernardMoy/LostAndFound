package com.example.lostandfound.UI

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.activityLogTitles
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.ActivityLog.ActivityLogScreen
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
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class ActivityLogActivityTest : FirebaseTestsSetUp() {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()
        private var userID: String? = null

        // activity log ids
        private var activityLog0ID: String? = null
        private var activityLog1ID: String? = null
        private var activityLog2ID: String? = null
        private var activityLog3ID: String? = null
        private var activityLog4ID: String? = null
        private var activityLog5ID: String? = null
        private var activityLog6ID: String? = null
        private var activityLog7ID: String? = null


    }

    @Before
    fun setUp() {
        // create test user
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

        // add all types of activity log 0 to 7

        val latch2: CountDownLatch = CountDownLatch(8)

        val dataActivityLog0 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 0,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 0",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog0)
            .addOnSuccessListener { documentReference ->
                activityLog0ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        val dataActivityLog1 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 1,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 1",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog1)
            .addOnSuccessListener { documentReference ->
                activityLog1ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        val dataActivityLog2 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 2,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 2",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog2)
            .addOnSuccessListener { documentReference ->
                activityLog2ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        val dataActivityLog3 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 3,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 3",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog3)
            .addOnSuccessListener { documentReference ->
                activityLog3ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        val dataActivityLog4 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 4,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 4",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog4)
            .addOnSuccessListener { documentReference ->
                activityLog4ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        val dataActivityLog5 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 5,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 5",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog5)
            .addOnSuccessListener { documentReference ->
                activityLog5ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        val dataActivityLog6 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 6,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 6",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog6)
            .addOnSuccessListener { documentReference ->
                activityLog6ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        val dataActivityLog7 = mutableMapOf<String, Any>(
            FirebaseNames.ACTIVITY_LOG_ITEM_USER_ID to userID.toString(),
            FirebaseNames.ACTIVITY_LOG_ITEM_TYPE to 7,
            FirebaseNames.ACTIVITY_LOG_ITEM_TIMESTAMP to 1739942999L,
            FirebaseNames.ACTIVITY_LOG_ITEM_CONTENT to "content 7",
        )
        firestore!!.collection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS).add(dataActivityLog7)
            .addOnSuccessListener { documentReference ->
                activityLog7ID = documentReference.id
                latch2.countDown()
            }.addOnFailureListener { e ->
                fail("Failed adding db items")
                latch2.countDown()
            }

        // wait for all operations to complete
        latch2.await(60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    // test that the correct notification titles are shown at correct positions
    @Test
    fun testActivityLogItemsShown() {
        composeTestRule.setContent {
            ActivityLogScreen(
                activity = ComponentActivity()
            )
        }

        composeTestRule.waitForIdle()
        Thread.sleep(4000)

        // Select the text nodes that are may not be a direct child of the outer box
        composeTestRule.onNodeWithTag("activity_log_$activityLog0ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[0]!!))
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("activity_log_$activityLog1ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[1]!!))
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("activity_log_$activityLog2ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[2]!!))
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("activity_log_$activityLog3ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[3]!!))
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("activity_log_$activityLog4ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[4]!!))
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("activity_log_$activityLog5ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[5]!!))
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("activity_log_$activityLog6ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[6]!!))
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("activity_log_$activityLog7ID")
            .performScrollTo()
            .onChildren()
            .assertAny(hasText(activityLogTitles[7]!!))
        Thread.sleep(1000)

    }


    // clear all data in firestore after tests
    @After
    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        TimeoutException::class
    )
    fun tearDown() {
        // clear all data
        deleteCollection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }

}