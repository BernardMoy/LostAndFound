package com.example.lostandfound.Integration

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.Settings.SettingsScreen
import com.example.lostandfound.ui.Settings.SettingsViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SettingsActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()
        private var userID: String? = null
    }

    @get:Rule
    val composeTestRule = createComposeRule()


    /*
    Test if admin settings show for admins
     */
    @Test
    fun testWithAdmin() {
        // logout current user
        if (auth!!.currentUser != null) {
            auth!!.signOut()
        }
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
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_IS_ADMIN to true
        )
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(
            userID.toString()
        )
            .set(dataUser1)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        composeTestRule.setContent {
            SettingsScreen(
                activity = ComponentActivity(),
                viewModel = SettingsViewModel()
            )
        }
        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Admin settings").assertExists()
    }

    /*
    Test if admin setting not show for non admins
     */
    @Test
    fun testWithoutAdmin() {
        // logout current user
        if (auth!!.currentUser != null) {
            auth!!.signOut()
        }
        // create and sign in test user
        val email = "test2@warwick"
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
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_IS_ADMIN to false
        )
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(
            userID.toString()
        )
            .set(dataUser1)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        composeTestRule.setContent {
            SettingsScreen(
                activity = ComponentActivity(),
                viewModel = SettingsViewModel()
            )
        }
        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Admin settings").assertDoesNotExist()
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