package com.example.lostandfound

import com.example.lostandfound.Data.FirebaseNames
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.TimeUnit

class ItemManagerTest {
    // static context, visible to the whole class for variables inside here
    companion object {
        private var firestore: FirebaseFirestore? = null
        private var storage: FirebaseStorage? = null

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
            FirebaseNames.LOSTFOUND_LOCATION to Pair(52.381162440739686, -1.5614377315953403),
            FirebaseNames.LOSTFOUND_DESCRIPTION to "testDesc",
            FirebaseNames.LOST_IS_TRACKING to false,
            FirebaseNames.LOSTFOUND_TIMEPOSTED to 1739941511L
        )

        // Post the items
        val task1 = firestore!!.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost1)
        val ref1 = Tasks.await(task1, 60, TimeUnit.SECONDS)
        Thread.sleep(2000)
    }

    @Test
    fun testGetLostItemFromID() {

    }
}