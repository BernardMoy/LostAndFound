package com.example.lostandfound

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.lostandfound.Data.categories
import com.example.lostandfound.ui.NewLost.Category
import com.example.lostandfound.ui.NewLost.NewLostViewModel
import com.example.lostandfound.ui.NewLost.Subcategory
import org.junit.Rule
import org.junit.Test

class NewLostActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSelectCategory(){
        val viewModel = NewLostViewModel()
        composeTestRule.setContent {
            Category(viewModel = viewModel)  // set content to be category while passing the created VM to it
        }
        
        val TESTNAME = categories[0].name  // use the name of the first category
        composeTestRule.onNodeWithText(TESTNAME).performClick()
        assert(viewModel.selectedCategory != null && viewModel.selectedCategory!!.name == TESTNAME)
    }

    @Test
    fun testSelectSubCategory(){
        val viewModel = NewLostViewModel()
        composeTestRule.setContent {
            Category(viewModel = viewModel)  // set content to be category while passing the created VM to it
            Subcategory(viewModel = viewModel)
        }

        val CATEGORYNAME = categories[0].name
        val SUBCATEGORYNAME = categories[0].subCategories[0]

        // select category
        composeTestRule.onNodeWithText(CATEGORYNAME).performClick()

        // select subcategory
        composeTestRule.onNodeWithTag("SubCategoryDropdown").performClick() // open the dropdown menu
        composeTestRule.onNodeWithText(SUBCATEGORYNAME).performClick()
        assert(viewModel.selectedSubCategory.value == SUBCATEGORYNAME)
    }

    @Test
    fun testOtherCategory(){
        val viewModel = NewLostViewModel()
        composeTestRule.setContent {
            Category(viewModel = viewModel)  // set content to be category while passing the created VM to it
            Subcategory(viewModel = viewModel)
        }

        val CATEGORYNAME = "Others"
        val SUBCATEGORYNAME = "Placeholder"

        // select category
        composeTestRule.onNodeWithText(CATEGORYNAME).performClick()

        // select subcategory
        composeTestRule.onNodeWithTag("SubCategoryInput")
            .performClick()
            .performTextInput(SUBCATEGORYNAME)

        assert(viewModel.selectedSubCategory.value == SUBCATEGORYNAME)
    }

    @Test
    fun testSelectSubCategoryOverridden(){
        val viewModel = NewLostViewModel()
        composeTestRule.setContent {
            Category(viewModel = viewModel)  // set content to be category while passing the created VM to it
            Subcategory(viewModel = viewModel)
        }

        val CATEGORYNAME = categories[0].name
        val NEWCATEGORYNAME = "Others"
        val SUBCATEGORYNAME = categories[0].subCategories[0]

        // select category
        composeTestRule.onNodeWithText(CATEGORYNAME).performClick()

        // select subcategory
        composeTestRule.onNodeWithTag("SubCategoryDropdown").performClick() // open the dropdown menu
        composeTestRule.onNodeWithText(SUBCATEGORYNAME).performClick()
        assert(viewModel.selectedSubCategory.value == SUBCATEGORYNAME)

        composeTestRule.onNodeWithText(NEWCATEGORYNAME).performClick()
        assert(viewModel.selectedSubCategory.value.isEmpty())
    }
}