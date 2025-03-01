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
        private var lost2ID: String? = null
        private var lost3ID: String? = null

        private var found1ID: String? = null
        private var found2ID: String? = null

        private var claimL2F1: String? = null
        private var claimL3F1: String? = null

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
        /*
        lost1 (Isolated)
        lost2 -> made a claim to found1
        lost3 -> made a claim to found1 and is approved
        found1 -> claimed by L2 and L3
        found2 (Isolated)
         */
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

        val dataLost2 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test2",
            FirebaseNames.LOSTFOUND_USER to "Rwowo2",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat2",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat2",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand2",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc2",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        val dataLost3 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test3",
            FirebaseNames.LOSTFOUND_USER to "Rwowo3",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat3",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat3",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand3",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc3",
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

        val dataFound2 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test2",
            FirebaseNames.LOSTFOUND_USER to "Rwowo2",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat2",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat2",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand2",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc2",
            FirebaseNames.FOUND_SECURITY_Q to "testSecQ2",
            FirebaseNames.FOUND_SECURITY_Q_ANS to "testSecQAns2",
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

        val task3 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost2)
        val ref3 = Tasks.await(task3, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        lost2ID = ref3.id

        val task4 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost3)
        val ref4 = Tasks.await(task4, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        lost3ID = ref4.id

        val task7 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound2)
        val ref7 = Tasks.await(task7, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        found2ID = ref7.id


        // create claims based on the ids
        val dataClaimL2F1 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739942998L,
            FirebaseNames.CLAIM_IS_APPROVED to false,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost2ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to found1ID.toString(),
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to ""
        )

        val dataClaimL3F1 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739942555L,
            FirebaseNames.CLAIM_IS_APPROVED to true,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost3ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to found1ID.toString(),
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to ""
        )

        // Post the claims
        val task5 = firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaimL2F1)
        val ref5 = Tasks.await(task5, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        claimL2F1 = ref5.id

        val task6 = firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaimL3F1)
        val ref6 = Tasks.await(task6, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        claimL3F1 = ref6.id
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

    @Test
    fun testGetClaimFromLostID(){

    }

    @Test
    fun testGetClaimsFromFoundID(){

    }

    @Test
    fun testGetLostItemStatus(){

    }

    @Test
    fun testGetFoundItemStatus(){

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