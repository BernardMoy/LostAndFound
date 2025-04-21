package com.example.lostandfound.Integration

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.IntentExtraNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.ui.ViewFound.ViewFoundActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ViewFoundActivityTest : FirebaseTestsSetUp() {
    // set up firestore emulator in static context
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var auth: FirebaseAuth? = getAuth()

        private var userID: String? = null

        private var dataFound0: FoundItem? = null  // status 0
    }

    @get:Rule
    val composeTestRule = createComposeRule()


    @Before
    fun setUp() {
        // create and sign in test user
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


        // create item details
        dataFound0 = FoundItem(
            itemID = "2e9j8qijwqiie",
            user = User(
                userID = userID ?: "",
                firstName = "testFirstName2",
                lastName = "testLastName2"
            ),    // use the current user ID
            itemName = "TestItem",
            category = "TestCat",
            subCategory = "TestSubCat",
            color = listOf("Black", "Red"),
            brand = "TestBrand",
            dateTime = 1738819980L,
            location = Pair(52.381162440739686, -1.5614377315953403),
            description = "TestDesc",
            timePosted = 1739941511L,
            status = 0
        )

        val task3 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
            .document("2e9j8qijwqiie").set(dataFound0!!)
        val ref3 = Tasks.await(task3, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)


        // upload the user to firebase firestore
        val dataFoundUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "testFirstName2",
            FirebaseNames.USERS_LASTNAME to "testLastName2"
        )

        // document the found user id and add it
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userID.toString())
            .set(dataFoundUser)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    /*
    Test if the lost item can be deleted
     */
    @Test
    fun testDeleteItem() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                ViewFoundActivity::class.java
            ).apply {
                putExtra(
                    IntentExtraNames.INTENT_FOUND_ITEM,
                    dataFound0
                )
            }

        ActivityScenario.launch<ViewFoundActivity>(intent)
        composeTestRule.waitForIdle()

        // assert the correct intent has been passed
        assertEquals(
            dataFound0, intent.getParcelableExtra(
                IntentExtraNames.INTENT_FOUND_ITEM
            )
        )

        // assert the correct item details are posted
        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Delete item").performScrollTo()

        // click the delete button
        composeTestRule.onNodeWithText("Delete item").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("Delete").performClick()
        Thread.sleep(2000)

        // now assert the item no longer exist in the db
        val latch = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS)
            .document(dataFound0!!.itemID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Assert.assertFalse(querySnapshot.exists())
                latch.countDown()

            }
            .addOnFailureListener { e ->
                fail("Failed during db query")
                latch.countDown()
            }

        latch.await(60, TimeUnit.SECONDS)
    }

    @After
    fun tearDown() {
        deleteCollection(FirebaseNames.COLLECTION_USERS)
        deleteCollection(FirebaseNames.COLLECTION_ACTIVITY_LOG_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_FOUND_ITEMS)

        // delete current user at the end, as this will trigger cloud functions
        if (auth!!.currentUser != null) {
            Tasks.await(
                auth!!.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}