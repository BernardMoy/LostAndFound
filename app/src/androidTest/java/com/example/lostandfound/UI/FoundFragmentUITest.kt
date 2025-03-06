package com.example.lostandfound.UI

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.Found.FoundFragmentScreen
import com.example.lostandfound.ui.Found.FoundFragmentViewModel
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


class FoundFragmentUITest : FirebaseTestsSetUp() {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()
        private var foundID: String? = null
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

        // add the data to database, with the user ID above
        val dataFound1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "298heh29",
            FirebaseNames.LOSTFOUND_USER to userID.toString(),
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCatwqq2",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L,
            FirebaseNames.FOUND_SECURITY_Q to "testSecQ",
            FirebaseNames.FOUND_SECURITY_Q_ANS to "testSecQAns"
        )

        // Post the items
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound1)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        foundID = ref1.id
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    // test that in the lost fragment, the item added is shown
    @Test
    fun testFoundItemShown() {
        val viewModel = FoundFragmentViewModel()
        composeTestRule.setContent {
            FoundFragmentScreen(viewmodel = viewModel, isTesting = true)
        }

        Thread.sleep(5000)

        // assert the lost item preview details of the current user is shown on screen
        composeTestRule.onNodeWithText("298heh29").assertExists()
        composeTestRule.onNodeWithText("Category: " + "testSubCatwqq2")
            .assertExists() // only the subcat is shown
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
        deleteCollection(FirebaseNames.COLLECTION_FOUND_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}