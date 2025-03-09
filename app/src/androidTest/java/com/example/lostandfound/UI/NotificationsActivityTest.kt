package com.example.lostandfound.UI

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.notificationContent
import com.example.lostandfound.Data.notificationTitle
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.Notifications.NotificationsScreen
import com.example.lostandfound.ui.Notifications.NotificationsViewModel
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


class NotificationsActivityTest : FirebaseTestsSetUp() {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()
        private var userID: String? = null

        // notification ids
        private var notification0ID: String? = null
        private var notification1ID: String? = null
        private var notification2ID: String? = null
        private var notification3ID: String? = null

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

        // add all types of notifications 0 1 2 3
        // type 0 (Only this is unread)
        val dataNotif0 = mutableMapOf<String, Any>(
            FirebaseNames.NOTIFICATION_USER_ID to userID.toString(),
            FirebaseNames.NOTIFICATION_TYPE to 0,
            FirebaseNames.NOTIFICATION_TIMESTAMP to 1739942999L,
            FirebaseNames.NOTIFICATION_IS_READ to false,
            FirebaseNames.NOTIFICATION_LOST_ITEM_ID to "2092i2didoi",
            FirebaseNames.NOTIFICATION_FOUND_ITEM_ID to "2e92ie2iooe2"
        )
        val task0 = firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS).add(dataNotif0)
        val ref0 = Tasks.await(task0, 60, TimeUnit.SECONDS)
        notification0ID = ref0.id
        Thread.sleep(2000)

        // type 1
        val dataNotif1 = mutableMapOf<String, Any>(
            FirebaseNames.NOTIFICATION_USER_ID to userID.toString(),
            FirebaseNames.NOTIFICATION_TYPE to 1,
            FirebaseNames.NOTIFICATION_TIMESTAMP to 1739942910L,
            FirebaseNames.NOTIFICATION_IS_READ to true,
            FirebaseNames.NOTIFICATION_CLAIM_ID to "skiwiio2oi"
        )
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS).add(dataNotif1)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        notification1ID = ref1.id
        Thread.sleep(2000)

        // type 2
        val dataNotif2 = mutableMapOf<String, Any>(
            FirebaseNames.NOTIFICATION_USER_ID to userID.toString(),
            FirebaseNames.NOTIFICATION_TYPE to 2,
            FirebaseNames.NOTIFICATION_TIMESTAMP to 1739942911L,
            FirebaseNames.NOTIFICATION_IS_READ to true,
            FirebaseNames.NOTIFICATION_CLAIM_ID to "skiwiio2oi"
        )
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS).add(dataNotif2)
        val ref2 = Tasks.await(task2, 60, TimeUnit.SECONDS)
        notification2ID = ref2.id
        Thread.sleep(2000)

        // type 3
        val dataNotif3 = mutableMapOf<String, Any>(
            FirebaseNames.NOTIFICATION_USER_ID to userID.toString(),
            FirebaseNames.NOTIFICATION_TYPE to 3,
            FirebaseNames.NOTIFICATION_TIMESTAMP to 1739942910L,
            FirebaseNames.NOTIFICATION_IS_READ to true,
            FirebaseNames.NOTIFICATION_CLAIM_ID to "skiwiio2oi"
        )
        val task3 = firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS).add(dataNotif3)
        val ref3 = Tasks.await(task3, 60, TimeUnit.SECONDS)
        notification3ID = ref3.id
        Thread.sleep(2000)
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    // test that the correct notification titles are shown at correct positions
    @Test
    fun testNotificationsShown() {
        val viewModel = NotificationsViewModel()
        composeTestRule.setContent {
            NotificationsScreen(
                activity = ComponentActivity(),
                viewModel = viewModel,
                isTesting = true
            )
        }

        composeTestRule.waitForIdle()
        Thread.sleep(2000)

        // assert the 4 titles and contents of the notifications are displayed
        composeTestRule.onNodeWithTag("notification_$notification0ID")
            .assertTextContains(notificationTitle[0]!!)
        composeTestRule.onNodeWithTag("notification_$notification1ID")
            .assertTextContains(notificationTitle[1]!!)
        composeTestRule.onNodeWithTag("notification_$notification2ID")
            .assertTextContains(notificationTitle[2]!!)
        composeTestRule.onNodeWithTag("notification_$notification3ID")
            .assertTextContains(notificationTitle[3]!!)

        composeTestRule.onNodeWithTag("notification_$notification0ID").assertTextContains(
            notificationContent[0]!!
        )
        composeTestRule.onNodeWithTag("notification_$notification1ID").assertTextContains(
            notificationContent[1]!!
        )
        composeTestRule.onNodeWithTag("notification_$notification2ID").assertTextContains(
            notificationContent[2]!!
        )
        composeTestRule.onNodeWithTag("notification_$notification3ID").assertTextContains(
            notificationContent[3]!!
        )


        // assert that the red dot for the notification type 0 one exist
        composeTestRule.onNodeWithTag("red_dot_$notification0ID", useUnmergedTree = true)
            .assertExists()

        // now try clicking on the notification of type 0
        composeTestRule.onNodeWithText(notificationTitle[0]!!).performClick()

        // now that the red dot should disappear
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("red_dot_$notification0ID", useUnmergedTree = true)
            .assertDoesNotExist()
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
        deleteCollection(FirebaseNames.COLLECTION_NOTIFICATIONS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }

}