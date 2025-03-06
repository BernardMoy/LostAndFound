package com.example.lostandfound.Integration

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.categories
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.NewLost.NewLostScreen
import com.example.lostandfound.ui.NewLost.NewLostViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class NewLostActivityTest : FirebaseTestsSetUp() {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()

        // store item details
        private val itemName: String = "TESTITEM"
        private val categoryName: String = categories[0].name
        private val subCategoryName: String = categories[0].subCategories[0]
        private val color1: String = "Black"
        private val color2: String = "Pink"
        private val brand: String = "TESTBRAND"
        private val description: String = "TESTDESCRIPTION"  // use current date time

    }

    @get:Rule
    val composeTestRule = createComposeRule()

    /*
    Test that when a lost item is posted, its entry appears in the database.
    Remember to start firebase emulator
     */
    @Test
    fun testPostNewLostItemInDB() {
        val viewModel = NewLostViewModel()
        composeTestRule.setContent {
            NewLostScreen(
                activity = ComponentActivity(),
                viewModel = viewModel
            )   // set content to be main content of new lost activity
        }

        // input the details
        // tags are in the form of XXXInput
        composeTestRule.onNodeWithTag("NameInput").performTextInput(itemName)  // name
        composeTestRule.onNodeWithText(categoryName).performScrollTo().performClick()  // category
        composeTestRule.onNodeWithTag("SubCategoryDropdown")
            .performScrollTo().performClick() // open the dropdown menu
        composeTestRule.onNodeWithText(subCategoryName).performScrollTo()
            .performClick()  // subcategory
        composeTestRule.onNodeWithText(color1).performScrollTo().performClick()  // color 1
        composeTestRule.onNodeWithText(color2).performScrollTo().performClick()  // color 2
        composeTestRule.onNodeWithTag("BrandInput").performTextInput(brand)  // brand
        composeTestRule.onNodeWithTag("DateInput").performScrollTo().performClick()
        Thread.sleep(3000)
        composeTestRule.onNodeWithText("Select").performClick()  // date
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("TimeInput").performScrollTo().performClick()
        Thread.sleep(3000)
        composeTestRule.onNodeWithText("Select").performClick()  // time
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("DescriptionInput")
            .performTextInput(description)  // description

        // click the done button
        composeTestRule.onNodeWithText("Done").performScrollTo().performClick()

        // wait for 5 seconds
        Thread.sleep(5000)

        // assert the entry exists in the database
        val latch = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .whereEqualTo(FirebaseNames.LOSTFOUND_ITEMNAME, itemName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // assert only one item exists
                assertEquals(1, querySnapshot.size())

                // get the lost item
                val document = querySnapshot.documents[0]

                // verify the attributes are correct
                assertNotNull(document)
                assertEquals(itemName, document[FirebaseNames.LOSTFOUND_ITEMNAME] as String)
                assertEquals(categoryName, document[FirebaseNames.LOSTFOUND_CATEGORY] as String)
                assertEquals(
                    subCategoryName,
                    document[FirebaseNames.LOSTFOUND_SUBCATEGORY] as String
                )
                assertEquals(
                    setOf(color1, color2),
                    (document[FirebaseNames.LOSTFOUND_COLOR] as List<String>).toSet()
                )
                assert(
                    // reference date. If current datetime is selected it is def greater
                    document[FirebaseNames.LOSTFOUND_EPOCHDATETIME] as Long > 1739978940
                )

                assertEquals(brand, document[FirebaseNames.LOSTFOUND_BRAND] as String)
                assertEquals(description, document[FirebaseNames.LOSTFOUND_DESCRIPTION] as String)

                // countdown
                latch.countDown()

            }
            .addOnFailureListener { e ->
                fail("Failed during db query")
                latch.countDown()
            }

        latch.await(60, TimeUnit.SECONDS)

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
        deleteCollection(FirebaseNames.COLLECTION_LOST_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS)
    }
}