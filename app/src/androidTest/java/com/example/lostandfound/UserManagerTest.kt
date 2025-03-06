package com.example.lostandfound

import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.UserManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class UserManagerTest : FirebaseTestsSetUp() {
    // static context, visible to the whole class for variables inside here
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()

        // item ids
        private var userID: String? = null

    }

    @Before
    fun setUp() {
        // add the necessary data to the firebase database
        val dataUser = mutableMapOf<String, Any>(
            FirebaseNames.USERS_AVATAR to "ABCDE",
            FirebaseNames.USERS_EMAIL to "test@warwick.ac.uk",
            FirebaseNames.USERS_FIRSTNAME to "fnfn",
            FirebaseNames.USERS_LASTNAME to "lnln"
        )

        // Post the items
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS).add(dataUser)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        userID = ref1.id
    }

    @Test
    fun testGetUserFromID() {
        val latch: CountDownLatch = CountDownLatch(1)
        UserManager.getUserFromId(userID.toString(), object : UserManager.UserCallback {
            override fun onComplete(user: User?) {
                assertNotNull(user)

                assertEquals(userID, user!!.userID)
                assertEquals("ABCDE", user.avatar)
                assertEquals("fnfn", user.firstName)
                assertEquals("lnln", user.lastName)
                assertEquals("test@warwick.ac.uk", user.email)

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
    }
}