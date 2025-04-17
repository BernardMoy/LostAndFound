package com.example.lostandfound.Integration

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.EditProfile.EditProfileScreen
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

class EditProfileActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()
        private var userID: String? = null
    }

    @get:Rule
    val composeTestRule = createComposeRule()


    @Before
    fun setUp() {
        // create and sign in test user
        val email = "test@warwick"
        val password = "1234ABCde"
        val firstName = "fn"
        val lastName = "ln"

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

        val dataUser1 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_FIRSTNAME to firstName,
            FirebaseNames.USERS_LASTNAME to lastName,
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_EMAIL to email
        )
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(
            userID.toString()
        )
            .set(dataUser1)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }


    /*
    Test if the profile edits are reflected in the db
     */
    @Test
    fun testEditProfile() {
        composeTestRule.setContent {
            EditProfileScreen(
                activity = ComponentActivity()
            )
        }
        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("editProfileFirstName").performTextClearance()
        composeTestRule.onNodeWithTag("editProfileFirstName").performTextInput("new2")
        composeTestRule.onNodeWithTag("editProfileLastName").performTextClearance()
        composeTestRule.onNodeWithTag("editProfileLastName").performTextInput("new3")

        // click the save profile button
        composeTestRule.onNodeWithText("Save Profile").performClick()
        Thread.sleep(2000)

        // assert new data in db
        val latch = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userID ?: "")
            .get()
            .addOnSuccessListener { doc ->
                // assert new data
                assertEquals("new2", doc[FirebaseNames.USERS_FIRSTNAME])
                assertEquals("new3", doc[FirebaseNames.USERS_LASTNAME])

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
        deleteCollection(FirebaseNames.COLLECTION_USERS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}