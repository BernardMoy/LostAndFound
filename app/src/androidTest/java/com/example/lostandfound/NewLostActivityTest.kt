package com.example.lostandfound

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.lostandfound.ui.NewLost.Category
import com.example.lostandfound.ui.NewLost.NewLostViewModel
import org.junit.Rule
import org.junit.Test

class NewLostActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSelectCategory(){
        val viewModel = NewLostViewModel()
        composeTestRule.setContent {
            Category(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Bags and Suitcases").performClick()
        assert(viewModel.selectedCategory != null && viewModel.selectedCategory!!.name == "Bags and Suitcases")
    }
}