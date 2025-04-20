package com.example.lostandfound.Unit

import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.FirebaseManagers.UserManager.CheckIfClaimedCallback
import com.example.lostandfound.FirebaseManagers.UserManager.UpdateTimeCallback
import com.example.lostandfound.FirebaseTestsSetUp
import com.example.lostandfound.Utility.DateTimeManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.math.abs

class UserManagerTest : FirebaseTestsSetUp() {
    // static context, visible to the whole class for variables inside here
    companion object {
        private var auth: FirebaseAuth = getAuth()
        private var firestore: FirebaseFirestore? = getFirestore()

        // item ids
        private var userID: String? = null

    }

    @Before
    fun setUp() {
        // create and sign in test user
        val email = "test@warwick"
        val password = "1234ABCde"
        val firstName = "fn"
        val lastName = "ln"

        val latch: CountDownLatch = CountDownLatch(1)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
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
        assertNotNull(auth.currentUser)

        val dataUser1 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_FIRSTNAME to firstName,
            FirebaseNames.USERS_LASTNAME to lastName,
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_EMAIL to email,
            FirebaseNames.USERS_LAST_CLAIMED_TIMESTAMP to DateTimeManager.getCurrentEpochTime() - 250000
        )
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(
            userID.toString()
        )
            .set(dataUser1)
        Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    @Test
    fun testUpdateClaimTimestamp() {
        val latch = CountDownLatch(1)
        val currentTime = DateTimeManager.getCurrentEpochTime()

        UserManager.updateClaimTimestamp(object : UpdateTimeCallback {
            override fun onComplete(success: Boolean) {
                Assert.assertTrue(success)
                latch.countDown()
            }
        })
        latch.await(60, TimeUnit.SECONDS)

        // now see if the time is correct
        val latch2 = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_USERS)
            .document(userID ?: "")
            .get()
            .addOnSuccessListener { doc ->
                assert(
                    abs((doc[FirebaseNames.USERS_LAST_CLAIMED_TIMESTAMP] as Long) - currentTime) < 100
                )

                // countdown
                latch2.countDown()

            }
            .addOnFailureListener { e ->
                fail("Failed during db query")
                latch2.countDown()
            }

        latch2.await(60, TimeUnit.SECONDS)
    }

    @Test
    fun testCheckIfUserClaimedInLastThreeDays(){
        val latch = CountDownLatch(1)

        UserManager.checkIfUserClaimedInLastThreeDays(object: CheckIfClaimedCallback{
            override fun onComplete(result: Boolean) {
                Assert.assertTrue(result)
                latch.countDown()
            }

        })
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
        deleteCollection(FirebaseNames.COLLECTION_USERS)

        if (auth.currentUser != null) {
            Tasks.await(
                auth.currentUser!!.delete(), 60, TimeUnit.SECONDS
            )
        }
    }
}