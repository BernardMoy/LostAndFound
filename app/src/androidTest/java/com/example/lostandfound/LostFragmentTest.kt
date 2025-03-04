package com.example.lostandfound

import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FirebaseUtility
import com.example.lostandfound.ui.Lost.LostFragmentScreen
import com.example.lostandfound.ui.Lost.LostFragmentViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class LostFragmentTest {

    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = null
        private var auth: FirebaseAuth? = null
        private var lostID: String? = null
        private var userID: String? = null

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


            // create auth emulator
            auth = FirebaseAuth.getInstance()
            auth!!.useEmulator("10.0.2.2", 9099)

        }
    }

    @Before
    fun setUp() {
        // create test user
        val email = "test@warwick"
        val password = "1234ABCde"

        val latch: CountDownLatch = CountDownLatch(1)
        auth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task ->
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
        val dataLost1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "298heh29",
            FirebaseNames.LOSTFOUND_USER to userID.toString(),
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCatwqq2",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        // Post the items
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost1)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        lostID = ref1.id
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    // test that in the lost fragment, the item added is shown
    @Test
    fun testLostItemShown() {
        val viewModel = LostFragmentViewModel()
        composeTestRule.setContent {
            LostFragmentScreen(
                viewModel = viewModel,
                isTesting = true
            )
        }

        Thread.sleep(5000)

        // assert the lost item preview details of the current user is shown on screen
        composeTestRule.onNodeWithText("298heh29").assertExists()
        composeTestRule.onNodeWithText("Category: " + "testSubCatwqq2").assertExists() // only the subcat is shown
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