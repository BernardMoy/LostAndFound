package com.example.lostandfound

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.categories
import com.example.lostandfound.ItemManagerTest.Companion
import com.example.lostandfound.ui.NewLost.MainContent
import com.example.lostandfound.ui.NewLost.NewLostViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class PostNewLostItemTest {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = null

        // store item details
        private val itemName: String = "TESTITEM"
        private val categoryName: String = categories[0].name
        private val subCategoryName: String = categories[0].subCategories[0]
        private val color1: String = "Black"
        private val color2: String = "Pink"
        private val brand: String = "TESTBRAND"
        private val description: String = "TESTDESCRIPTION"  // use current date time


        @BeforeClass
        @JvmStatic
        fun setupClass() {
            firestore = FirebaseFirestore.getInstance()
            firestore!!.useEmulator(
                "10.0.2.2",
                8080
            ) // use the emulator host, not 127.0.0.1 localhost
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
            firestore!!.firestoreSettings = settings
        }
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    /*
    Test that when a lost item is posted, its entry appears in the database.
     */
    @Test
    fun testPostNewLostItemInDB() {
        val viewModel = NewLostViewModel()
        composeTestRule.setContent {
            MainContent(viewModel = viewModel)   // set content to be main content of new lost activity
        }

        // input the details
        // tags are in the form of XXXInput
        composeTestRule.onNodeWithTag("NameInput").performTextInput(itemName)  // name
        composeTestRule.onNodeWithText(categoryName).performClick()  // category
        composeTestRule.onNodeWithTag("SubCategoryDropdown")
            .performClick() // open the dropdown menu
        composeTestRule.onNodeWithText(subCategoryName).performClick()  // subcategory
        composeTestRule.onNodeWithText(color1).performClick()  // color 1
        composeTestRule.onNodeWithText(color2).performClick()  // color 2
        composeTestRule.onNodeWithTag("BrandInput").performTextInput(brand)  // brand
        composeTestRule.onNodeWithTag("DateInput").performClick()
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Select").performClick()  // date
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("TimeInput").performClick()
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Select").performClick()  // time
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("DescriptionInput")
            .performTextInput(description)  // description

        // click the done button
        composeTestRule.onNodeWithText("Done").performClick()

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
                assertEquals(subCategoryName, document[FirebaseNames.LOSTFOUND_SUBCATEGORY] as String)
                assertEquals(setOf(color1, color2), (document[FirebaseNames.LOSTFOUND_COLOR] as List<String>).toSet())
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
    }

    // private method to delete all elements inside a collection
    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        TimeoutException::class
    )
    private fun deleteCollection(name: String) {
        val taskGet = firestore!!.collection(name).get()
        val docs = Tasks.await(taskGet, 60, TimeUnit.SECONDS)

        // create a list of delete tasks for each doc
        val deleteTasks: MutableList<Task<Void>> = ArrayList()
        for (doc in docs) {
            val deleteTask = firestore!!.collection(name)
                .document(doc.id)
                .delete()
            deleteTasks.add(deleteTask)
        }
        // execute all tasks
        Tasks.await(Tasks.whenAll(deleteTasks), 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }
}