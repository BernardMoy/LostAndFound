package com.example.lostandfound

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lostandfound.ui.EditProfile.MainContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEmptyFirstName(){
        composeTestRule.setContent {
            MainContent()
        }

        // add other UI tests here
        
        // assert that the error message is displayed
        composeTestRule.onNodeWithText("Only your name and avatar would be visible to others.").assertIsDisplayed()
    }



}