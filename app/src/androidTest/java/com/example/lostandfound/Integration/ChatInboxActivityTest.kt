package com.example.lostandfound.Integration

import android.content.Intent
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.Utility.DateTimeManager
import com.example.lostandfound.ui.ChatInbox.ChatInboxActivity
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

class ChatInboxActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var user1ID: String? = null
        private var user2ID: String = "89282didwui"
        private var user1: User? = null
        private var user2: User? = null

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
                    user1ID = user.uid
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


        // create user1 in firestore
        user1 = User(
            userID = user1ID.toString(),
            avatar = "",
            firstName = "u1f",
            lastName = "u1l"
        )

        val dataUser1 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_FIRSTNAME to "u1f",
            FirebaseNames.USERS_LASTNAME to "u1l",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_EMAIL to email
        )
        val task1 =
            firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(user1ID.toString())
                .set(dataUser1)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // create user2 in firestore
        user2 = User(
            userID = user2ID,
            avatar = "",
            firstName = "u2f",
            lastName = "u2l"
        )
        val dataUser2 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_FIRSTNAME to "u2f",
            FirebaseNames.USERS_LASTNAME to "u2l",
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_EMAIL to "test2@warwick"
        )
        val task2 =
            firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(user2ID).set(dataUser2)
        Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }


    /*
    Test if chat messages appear when the database is modified.
     */
    @Test
    fun testChatMessagesSent() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ChatInboxActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_CHAT_USER,
                    user2   // you are logged in as user1, user2 is passed
                )
            }

        ActivityScenario.launch<ChatInboxActivity>(intent)
        composeTestRule.waitForIdle()

        // assert the correct intent has been passed
        assertEquals(
            user2, intent.getParcelableExtra(
                IntentExtraNames.INTENT_CHAT_USER
            )
        )


        Thread.sleep(2000)

        // try input a message and click send
        composeTestRule.onNodeWithTag("ChatInput").performTextInput("Test message")
        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("SendButton").performClick()
        Thread.sleep(2000)

        // assert the new message is displayed on the screen and assert the input is cleared
        composeTestRule.onNodeWithTag("ChatInput").assertTextContains("Type your message...")

        // assert the message is displayed
        composeTestRule.onNodeWithText("Test message").assertExists()

        // simulate that another message is added to the database
        val newChat = mutableMapOf<String, Any>(
            FirebaseNames.CHAT_SENDER_USER_ID to user2ID,
            FirebaseNames.CHAT_RECIPIENT_USER_ID to user1ID.toString(),
            FirebaseNames.CHAT_FROM_TO to listOf(  // [from user, to user]
                user2ID, user1ID
            ),
            FirebaseNames.CHAT_CONTENT to "Reply232323",
            FirebaseNames.CHAT_TIMESTAMP to DateTimeManager.getCurrentEpochTime(),
            FirebaseNames.CHAT_IS_READ_BY_RECIPIENT to false, // default false
        )
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_CHATS).add(newChat)
        Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)

        // assert the reply message is displayed
        composeTestRule.onNodeWithText("Reply232323").assertExists()

    }

    @After
    fun tearDown() {
        deleteCollection(FirebaseNames.COLLECTION_USERS)
        deleteCollection(FirebaseNames.COLLECTION_CHATS)
        deleteCollection(FirebaseNames.COLLECTION_CHAT_INBOXES)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}