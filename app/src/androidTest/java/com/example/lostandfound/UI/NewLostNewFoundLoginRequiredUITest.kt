package com.example.lostandfound.UI

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.Home.HomeFragmentScreen
import com.example.lostandfound.ui.Home.HomeFragmentViewModel
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


class NewLostNewFoundLoginRequiredUITest : FirebaseTestsSetUp() {

    @get:Rule
    val composeTestRule = createComposeRule()

    // test that the login required dialog shows if the user is not logged in
    // but tries to access new lost and new found activity
    @Test
    fun testLoginRequiredNewLost() {
        val viewModel = HomeFragmentViewModel()
        composeTestRule.setContent {
            HomeFragmentScreen(
                viewModel = viewModel
            )
        }
        Thread.sleep(5000)

        composeTestRule.onNodeWithTag("largeLostButton").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("Login required").assertExists()

    }

    @Test
    fun testLoginRequiredNFound() {
        val viewModel = HomeFragmentViewModel()
        composeTestRule.setContent {
            HomeFragmentScreen(
                viewModel = viewModel
            )
        }
        Thread.sleep(5000)

        composeTestRule.onNodeWithTag("largeFoundButton").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("Login required").assertExists()

    }
}