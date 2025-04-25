package com.example.lostandfound.UI

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lostandfound.Data.categories
import com.example.lostandfound.ui.NewFound.Category
import com.example.lostandfound.ui.NewFound.ItemColor
import com.example.lostandfound.ui.NewFound.NewFoundViewModel
import com.example.lostandfound.ui.NewFound.Subcategory
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class NewFoundUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSelectCategory() {
        val viewModel = NewFoundViewModel()
        composeTestRule.setContent {
            Category(viewModel = viewModel)  // set content to be category while passing the created VM to it
        }

        val TESTNAME = categories[0].name  // use the name of the first category
        composeTestRule.onNodeWithText(TESTNAME).performClick()
        assert(viewModel.selectedCategory != null && viewModel.selectedCategory!!.name == TESTNAME)
    }

    @Test
    fun testSelectSubCategory() {
        val viewModel = NewFoundViewModel()
        composeTestRule.setContent {
            Category(viewModel = viewModel)  // set content to be category while passing the created VM to it
            Subcategory(viewModel = viewModel)
        }

        val CATEGORYNAME = categories[0].name
        val SUBCATEGORYNAME = categories[0].subCategories[0]

        // select category
        composeTestRule.onNodeWithText(CATEGORYNAME).performClick()

        // select subcategory
        composeTestRule.onNodeWithTag("SubCategoryDropdown")
            .performClick() // open the dropdown menu
        composeTestRule.onNodeWithText(SUBCATEGORYNAME).performClick()
        assert(viewModel.selectedSubCategory.value == SUBCATEGORYNAME)
    }

    @Test
    fun testOtherCategory() {
        val viewModel = NewFoundViewModel()
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
    fun testSelectSubCategoryOverridden() {
        val viewModel = NewFoundViewModel()
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
        composeTestRule.onNodeWithTag("SubCategoryDropdown")
            .performClick() // open the dropdown menu
        composeTestRule.onNodeWithText(SUBCATEGORYNAME).performClick()
        assert(viewModel.selectedSubCategory.value == SUBCATEGORYNAME)

        composeTestRule.onNodeWithText(NEWCATEGORYNAME).performClick()
        assert(viewModel.selectedSubCategory.value.isEmpty())
    }

    // test that only maximum of 3 colors can be chosen
    @Test
    fun testSelectColor() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = NewFoundViewModel()
        composeTestRule.setContent {
            ItemColor(context = context, viewModel = viewModel)
        }

        // select color
        composeTestRule.onNodeWithText("Red").performClick()
        composeTestRule.onNodeWithText("Blue").performClick()
        composeTestRule.onNodeWithText("Yellow").performClick()

        // assert the three colors exist in the view model
        Assert.assertTrue(
            viewModel.selectedColor.contains("Red")
        )
        Assert.assertTrue(
            viewModel.selectedColor.contains("Blue")
        )
        Assert.assertTrue(
            viewModel.selectedColor.contains("Yellow")
        )

        // now attempt to select the 4th color
        composeTestRule.onNodeWithText("Black").performClick()

        // assert it does not exist in the VM
        Assert.assertTrue(
            viewModel.selectedColor.contains("Red")
        )
        Assert.assertTrue(
            viewModel.selectedColor.contains("Blue")
        )
        Assert.assertTrue(
            viewModel.selectedColor.contains("Yellow")
        )
        Assert.assertFalse(
            viewModel.selectedColor.contains("Black")
        )

    }
}