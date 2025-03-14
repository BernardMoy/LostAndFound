package com.example.lostandfound.Unit

import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.FirebaseManagers.FCMTokenManager
import com.example.lostandfound.FirebaseTestsSetUp
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FCMTokenManagerTest : FirebaseTestsSetUp() {
    // static context, visible to the whole class for variables inside here
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()

        // stores the user id
        private var userID: String? = null
        private var user2ID: String? = null

    }

    @Before
    fun setUp() {
        // add the necessary data to the firebase database

        val user = mutableMapOf<String, Any>(
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "FirstName",
            FirebaseNames.USERS_LASTNAME to "LastName",
            FirebaseNames.USERS_EMAIL to "test@warwick"
        )

        // Post the item
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_USERS).add(user)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        userID = ref1.id


        // user2 with fcm token
        val user2 = mutableMapOf<String, Any>(
            FirebaseNames.USERS_AVATAR to "",
            FirebaseNames.USERS_FIRSTNAME to "FirstName",
            FirebaseNames.USERS_LASTNAME to "LastName",
            FirebaseNames.USERS_EMAIL to "test@warwick",
            FirebaseNames.USERS_FCM_TOKEN to "2i2eo220eo0202e0e2e2e2"
        )

        // Post the item
        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_USERS).add(user2)
        val ref2 = Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        user2ID = ref2.id
    }

    @Test
    fun testUpdateFCMToken() {
        // initially, the fcm token does not exist
        val latch = CountDownLatch(1)
        FCMTokenManager.updateFCMToken(
            userID = userID.toString(),
            object : FCMTokenManager.FCMTokenUpdateCallback {
                override fun onComplete(success: Boolean) {
                    Assert.assertTrue(success)
                    latch.countDown()
                }
            }
        )
        latch.await(60, TimeUnit.SECONDS)

        // now check if the FCM token is not null
        val latch2 = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(userID ?: "")
            .get()
            .addOnSuccessListener { result ->
                Assert.assertNotEquals("", result[FirebaseNames.USERS_FCM_TOKEN])
                Assert.assertNotEquals(null, result[FirebaseNames.USERS_FCM_TOKEN])
                latch2.countDown()
            }
            .addOnFailureListener { e ->
                fail()
                latch2.countDown()
            }
        latch2.await(60, TimeUnit.SECONDS)
    }

    @Test
    fun testGetFCMToken() {
        // initially, the fcm token does not exist
        val latch = CountDownLatch(1)
        FCMTokenManager.getFCMTokenFromUser(
            userID = userID.toString(),
            object : FCMTokenManager.FCMTokenGetCallback {
                override fun onComplete(token: String?) {
                    // initially it should be null
                    Assert.assertNull(token)
                    latch.countDown()
                }
            }
        )
        latch.await(60, TimeUnit.SECONDS)

        // the fcm token from user 2 should exist
        val latch2 = CountDownLatch(1)
        FCMTokenManager.getFCMTokenFromUser(
            userID = user2ID.toString(),
            object : FCMTokenManager.FCMTokenGetCallback {
                override fun onComplete(token: String?) {
                    Assert.assertEquals("2i2eo220eo0202e0e2e2e2", token)
                    latch2.countDown()
                }
            }
        )
        latch2.await(60, TimeUnit.SECONDS)
    }

    @Test
    fun testRemoveFCMToken() {
        // try removing the token from user 2
        val latch = CountDownLatch(1)
        FCMTokenManager.removeFCMTokenFromUser(
            userID = user2ID.toString(),
            object : FCMTokenManager.FCMTokenDeleteCallback {
                override fun onComplete(success: Boolean) {
                    Assert.assertTrue(success)
                    latch.countDown()
                }
            }
        )
        latch.await(60, TimeUnit.SECONDS)

        // now the fcm token should disappear
        val latch2 = CountDownLatch(1)
        firestore!!.collection(FirebaseNames.COLLECTION_USERS).document(user2ID ?: "")
            .get()
            .addOnSuccessListener { result ->
                Assert.assertNull(result[FirebaseNames.USERS_FCM_TOKEN])
                latch2.countDown()
            }
            .addOnFailureListener { e ->
                fail()
                latch2.countDown()
            }
        latch2.await(60, TimeUnit.SECONDS)
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