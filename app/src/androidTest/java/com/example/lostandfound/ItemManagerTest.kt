package com.example.lostandfound

import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ItemManagerTest {
    // static context, visible to the whole class for variables inside here
    companion object {
        private var firestore: FirebaseFirestore? = null
        private var storage: FirebaseStorage? = null

        // item ids
        private var lost1ID: String? = null
        private var found1ID: String? = null

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
            firestore!!.firestoreSettings =
                settings

            // create storage emulator
            storage = FirebaseStorage.getInstance()
            storage!!.useEmulator("10.0.2.2", 9199)
        }
    }

    @Before
    fun setUp() {
        // add the necessary data to the firebase database

        // create a lost item to be added to firestore
        // data lost 1 is isolated -> status 0
        val dataLost1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test",
            FirebaseNames.LOSTFOUND_USER to "Rwowo",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        val dataFound1 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test",
            FirebaseNames.LOSTFOUND_USER to "Rwowo",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.FOUND_SECURITY_Q to "testSecQ",
            FirebaseNames.FOUND_SECURITY_Q_ANS to "testSecQAns",
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        // Post the items
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost1)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        lost1ID = ref1.id

        val task2 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound1)
        val ref2 = Tasks.await(task2, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        found1ID = ref2.id
    }

    @Test
    fun testGetLostItemFromID() {
        var target: LostItem? = null
        val latch = CountDownLatch(1)

        ItemManager.getLostItemFromId(lost1ID?:"", object: ItemManager.LostItemCallback{
            override fun onComplete(lostItem: LostItem?) {
                target = lostItem
                latch.countDown()
            }
        })
        latch.await(60, TimeUnit.SECONDS)

        // assert the item is not null
        assertNotNull(target)

        // assert each SPECIFIED attribute is correct
        // the inferred attributes (status etc.) are tested separately
        assertEquals("test", target?.itemName)
        assertEquals("Rwowo", target?.userID)
        assertEquals("testCat", target?.category)
        assertEquals("testSubCat", target?.subCategory)
        assertEquals(mutableListOf("Black", "Red"), target?.color)
        assertEquals("testBrand", target?.brand)
        assertEquals(1738819980L, target?.dateTime)
        assertEquals(Pair(52.381162440739686, -1.5614377315953403), target?.location)
        assertEquals("testDesc", target?.description)
        assertEquals(false, target?.isTracking)
        assertEquals(1739941511L, target?.timePosted)
    }

    @Test
    fun testGetFoundItemFromID(){
        var target: FoundItem? = null
        val latch = CountDownLatch(1)

        ItemManager.getFoundItemFromId(found1ID?:"", object: ItemManager.FoundItemCallback{
            override fun onComplete(foundItem: FoundItem?) {
                target = foundItem
                latch.countDown()
            }
        })
        latch.await(60, TimeUnit.SECONDS)

        // assert the item is not null
        assertNotNull(target)

        // assert each SPECIFIED attribute is correct
        // the inferred attributes (status etc.) are tested separately
        assertEquals("test", target?.itemName)
        assertEquals("Rwowo", target?.userID)
        assertEquals("testCat", target?.category)
        assertEquals("testSubCat", target?.subCategory)
        assertEquals(mutableListOf("Black", "Red"), target?.color)
        assertEquals("testBrand", target?.brand)
        assertEquals(1738819980L, target?.dateTime)
        assertEquals(Pair(52.381162440739686, -1.5614377315953403), target?.location)
        assertEquals("testDesc", target?.description)
        assertEquals("testSecQ", target?.securityQuestion)
        assertEquals("testSecQAns", target?.securityQuestionAns)
        assertEquals(1739941511L, target?.timePosted)
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
        deleteCollection(FirebaseNames.COLLECTION_CLAIMED_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_FOUND_ITEMS)
        deleteCollection(FirebaseNames.COLLECTION_NOTIFICATIONS)
        deleteCollection(FirebaseNames.COLLECTION_USERS)
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