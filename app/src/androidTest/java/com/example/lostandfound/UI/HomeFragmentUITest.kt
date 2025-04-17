package com.example.lostandfound.UI

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.Home.HomeFragmentScreen
import com.example.lostandfound.ui.Home.HomeFragmentViewModel
import com.example.lostandfound.ui.Lost.LostFragmentScreen
import com.example.lostandfound.ui.Lost.LostFragmentViewModel
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
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class HomeFragmentUITest : FirebaseTestsSetUp() {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()
        private var lostID: String? = null
        private var userID: String? = null

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
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    // test that in the home fragment, two large buttons are shown
    @Test
    fun testLargeLost(){
        val viewModel = HomeFragmentViewModel()
        composeTestRule.setContent {
            HomeFragmentScreen(
                viewModel = viewModel
            )
        }
        Thread.sleep(5000)

        composeTestRule.onNodeWithTag("largeLostButton").performClick()
        composeTestRule.onNodeWithText("New Lost item").assertExists()
    }

    @Test
    fun testLargeFound(){
        val viewModel = HomeFragmentViewModel()
        composeTestRule.setContent {
            HomeFragmentScreen(
                viewModel = viewModel
            )
        }
        Thread.sleep(5000)

        composeTestRule.onNodeWithTag("largeFoundButton").performClick()
        composeTestRule.onNodeWithText("New Found item").assertExists()
    }


    // clear all data in firestore after tests
    @After
    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        TimeoutException::class
    )
    fun tearDown() {
        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }

}