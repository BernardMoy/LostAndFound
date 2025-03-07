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
import com.example.lostandfound.ui.NewFound.NewFoundScreen
import com.example.lostandfound.ui.NewFound.NewFoundViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class NotificationType0Test : FirebaseTestsSetUp() {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var lost1ID: String = "ewioieiwo"
        private var lost2ID: String = "iweiwioiew"
        private var userLost1ID: String = "2e929229"
        private var userLost2ID: String = "289e2iiu"

        // store item details
        private val itemName: String = "TESTITEM"
        private val categoryName: String = categories[0].name
        private val subCategoryName: String = categories[0].subCategories[0]
        private val color1: String = "Black"
        private val color2: String = "Pink"
        private val brand: String = "TESTBRAND"
        private val time: Long = 1738819980L
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // upload two lost item with identical details to the found item
        // one with tracking one without
        val dataLost1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to itemName,
            FirebaseNames.LOSTFOUND_USER to userLost1ID,
            FirebaseNames.LOSTFOUND_CATEGORY to categoryName,
            FirebaseNames.LOSTFOUND_SUBCATEGORY to subCategoryName,
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf(color1, color2),
            FirebaseNames.LOSTFOUND_BRAND to brand,
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to time,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "",
            FirebaseNames.LOST_IS_TRACKING to true,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to time
        )
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .document(lost1ID.toString()).set(dataLost1)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        val dataLost2 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to itemName,
            FirebaseNames.LOSTFOUND_USER to userLost1ID,
            FirebaseNames.LOSTFOUND_CATEGORY to categoryName,
            FirebaseNames.LOSTFOUND_SUBCATEGORY to subCategoryName,
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf(color1, color2),
            FirebaseNames.LOSTFOUND_BRAND to brand,
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to time,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to time
        )
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS)
            .document(lost2ID.toString()).set(dataLost2)
        Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    /*
    Test that when a found item is posted, its entry appears in the database.
    Remember to start firebase emulator
     */
    @Test
    fun testNewMatchingItemNotification() {
        val viewModel = NewFoundViewModel()
        composeTestRule.setContent {
            NewFoundScreen(
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

        // click the done button
        composeTestRule.onNodeWithText("Done").performScrollTo().performClick()

        // wait for 5 seconds
        Thread.sleep(5000)

        // assert only one type 0 notification is sent, and that its user id is correct
        val latch = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
            .whereEqualTo(FirebaseNames.NOTIFICATION_TYPE, 0)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // assert only one item exists
                assertEquals(1, querySnapshot.size())

                // get the lost item
                val document = querySnapshot.documents[0]

                // verify the attributes are correct
                assertNotNull(document)
                assertEquals(userLost1ID, document[FirebaseNames.NOTIFICATION_USER_ID])

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
        deleteCollection(FirebaseNames.COLLECTION_FOUND_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_NOTIFICATIONS)
    }

}