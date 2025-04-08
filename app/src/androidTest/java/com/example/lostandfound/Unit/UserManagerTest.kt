package com.example.lostandfound.Unit

import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseTestsSetUp
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