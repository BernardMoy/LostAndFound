package com.example.lostandfound.Unit

import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseTestsSetUp
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ItemManagerTest : FirebaseTestsSetUp() {
    // static context, visible to the whole class for variables inside here
    companion object {
        private var firestore: FirebaseFirestore? = getFirestore()
        private var storage: FirebaseStorage? = getStorage()

        // item ids
        private var lost1ID: String? = null
        private var lost2ID: String? = null
        private var lost3ID: String? = null
        private var lost4ID: String? = null

        private var found1ID: String? = null
        private var found2ID: String? = null
        private var found3ID: String? = null

        private var claimL2F1: String? = null
        private var claimL3F1: String? = null
        private var claimL4F3: String? = null

    }

    @Before
    fun setUp() {
        // add the necessary data to the firebase database

        // create a lost item to be added to firestore
        /*
        lost1 (Isolated)
        lost2 -> made a claim to found1 and havent approved
        lost3 -> made a claim to found1 and is approved
        lost4 -> made a claim to found3 and havent approved
        found1 -> claimed by L2 and L3
        found2 (Isolated)
        found3 -> claimed by L4
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

        val dataLost4 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test4",
            FirebaseNames.LOSTFOUND_USER to "Rwowo4",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat4",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat4",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand4",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1748819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(52.481162440749685, -1.5614477415954404),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc4",
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

        val dataFound3 = mutableMapOf<String, Any>(
            FirebaseNames.LOSTFOUND_ITEMNAME to "test3",
            FirebaseNames.LOSTFOUND_USER to "Rwowo3",
            FirebaseNames.LOSTFOUND_CATEGORY to "testCat3",
            FirebaseNames.LOSTFOUND_SUBCATEGORY to "testSubCat3",
            FirebaseNames.LOSTFOUND_COLOR to mutableListOf("Black", "Red"),
            FirebaseNames.LOSTFOUND_BRAND to "testBrand3",
            FirebaseNames.LOSTFOUND_EPOCHDATETIME to 1738819980L,
            FirebaseNames.LOSTFOUND_LOCATION to LatLng(53.381163440739684, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc3",
            FirebaseNames.FOUND_SECURITY_Q to "testSecQ3",
            FirebaseNames.FOUND_SECURITY_Q_ANS to "testSecQAns3",
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

        val task8 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost4)
        val ref8 = Tasks.await(task8, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        lost4ID = ref8.id

        val task9 = firestore!!.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound3)
        val ref9 = Tasks.await(task9, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        found3ID = ref9.id


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
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to "claim3Text"
        )

        val dataClaimL4F3 = mutableMapOf<String, Any>(
            FirebaseNames.CLAIM_TIMESTAMP to 1739942555L,
            FirebaseNames.CLAIM_IS_APPROVED to false,
            FirebaseNames.CLAIM_LOST_ITEM_ID to lost4ID.toString(),
            FirebaseNames.CLAIM_FOUND_ITEM_ID to found3ID.toString(),
            FirebaseNames.CLAIM_SECURITY_QUESTION_ANS to "claim4Text"
        )

        // Post the claims
        val task5 =
            firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaimL2F1)
        val ref5 = Tasks.await(task5, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        claimL2F1 = ref5.id

        val task6 =
            firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaimL3F1)
        val ref6 = Tasks.await(task6, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        claimL3F1 = ref6.id

        val task10 =
            firestore!!.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaimL4F3)
        val ref10 = Tasks.await(task10, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
        claimL4F3 = ref10.id
    }

    @Test
    fun testGetLostItemFromID() {
        var target: LostItem? = null
        val latch = CountDownLatch(1)

        ItemManager.getLostItemFromId(lost1ID ?: "", object : ItemManager.LostItemCallback {
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
    fun testGetFoundItemFromID() {
        var target: FoundItem? = null
        val latch = CountDownLatch(1)

        ItemManager.getFoundItemFromId(found1ID ?: "", object : ItemManager.FoundItemCallback {
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
    fun testGetClaimFromLostID() {
        var target: Claim? = null
        val latch = CountDownLatch(1)

        ItemManager.getClaimFromLostId(lost2ID ?: "", object : ItemManager.LostClaimCallback {
            override fun onComplete(claim: Claim?) {
                target = claim
                latch.countDown()
            }
        })
        latch.await(60, TimeUnit.SECONDS)

        // assert the item is not null
        assertNotNull(target)

        // assert each attribute of the claim is correct
        assertEquals(1739942998L, target?.timestamp)
    }

    @Test
    fun testGetClaimsFromFoundID() {
        var target: List<Claim>? = null
        val latch = CountDownLatch(1)

        ItemManager.getClaimsFromFoundId(found1ID ?: "", object : ItemManager.FoundClaimCallback {
            override fun onComplete(claimList: MutableList<Claim>) {
                target = claimList
                latch.countDown()
            }
        })
        latch.await(60, TimeUnit.SECONDS)

        // assert the item is not null
        assertNotNull(target)

        // assert there are two claim ids
        assertEquals(2, target?.size)

        // assert the two claim ids are correct
        assert(
            setOf(target?.get(0)?.claimID, target?.get(1)?.claimID) == setOf(claimL2F1, claimL3F1)
        )
    }

    @Test
    fun testGetClaimFromClaimID() {
        var target: Claim? = null
        val latch = CountDownLatch(1)

        ItemManager.getClaimFromClaimId(claimL3F1 ?: "", object : ItemManager.ClaimCallback {
            override fun onComplete(claim: Claim?) {
                target = claim
                latch.countDown()
            }
        })
        latch.await(60, TimeUnit.SECONDS)

        // assert the item is not null
        assertNotNull(target)

        // assert each attribute of the claim is correct
        assertEquals("claim3Text", target?.securityQuestionAns)
    }

    @Test
    fun testGetLostItemStatus() {
        // statuses of the lost item l1, l2, l3 should be 0, 1, 2 respectively
        var l1Status: Int = -1
        var l2Status: Int = -1
        var l3Status: Int = -1

        val latch1 = CountDownLatch(1)
        ItemManager.getLostItemStatus(lost1ID ?: "", object : ItemManager.StatusCallback {
            override fun onComplete(status: Int) {
                l1Status = status
                latch1.countDown()
            }
        })
        latch1.await(60, TimeUnit.SECONDS)

        val latch2 = CountDownLatch(1)
        ItemManager.getLostItemStatus(lost2ID ?: "", object : ItemManager.StatusCallback {
            override fun onComplete(status: Int) {
                l2Status = status
                latch2.countDown()
            }
        })
        latch2.await(60, TimeUnit.SECONDS)

        val latch3 = CountDownLatch(1)
        ItemManager.getLostItemStatus(lost3ID ?: "", object : ItemManager.StatusCallback {
            override fun onComplete(status: Int) {
                l3Status = status
                latch3.countDown()
            }
        })
        latch3.await(60, TimeUnit.SECONDS)

        // assert statuses are 0 1 2
        assertEquals(0, l1Status)
        assertEquals(1, l2Status)
        assertEquals(2, l3Status)
    }

    @Test
    fun testGetFoundItemStatus() {
        // statuses of the lost item f1, f2 should be 2, 0 respectively
        var f1Status: Int = -1
        var f2Status: Int = -1
        var f3Status: Int = -1

        val latch1 = CountDownLatch(1)
        ItemManager.getFoundItemStatus(found1ID ?: "", object : ItemManager.StatusCallback {
            override fun onComplete(status: Int) {
                f1Status = status
                latch1.countDown()
            }
        })
        latch1.await(60, TimeUnit.SECONDS)

        val latch2 = CountDownLatch(1)
        ItemManager.getFoundItemStatus(found2ID ?: "", object : ItemManager.StatusCallback {
            override fun onComplete(status: Int) {
                f2Status = status
                latch2.countDown()
            }
        })
        latch2.await(60, TimeUnit.SECONDS)

        val latch3 = CountDownLatch(1)
        ItemManager.getFoundItemStatus(found3ID ?: "", object : ItemManager.StatusCallback {
            override fun onComplete(status: Int) {
                f3Status = status
                latch3.countDown()
            }
        })
        latch3.await(60, TimeUnit.SECONDS)

        // assert statuses are 2 0 1
        assertEquals(2, f1Status)
        assertEquals(0, f2Status)
        assertEquals(1, f3Status)
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
}