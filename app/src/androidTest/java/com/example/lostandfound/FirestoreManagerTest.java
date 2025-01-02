package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.lostandfound.FirebaseManagers.FirestoreManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FirestoreManagerTest {

    private String COLLECTION = "test_collection";
    private FirestoreManager firestoreManager;
    private FirebaseFirestore firestore;

    private Map<String, Object> testValue;
    private Map<String, Object> testValueSpecial;

    @Before
    public void setUp() throws InterruptedException{
        // Create firestore manager
        firestoreManager = new FirestoreManager();

        // Create instance of firestore
        firestore = FirebaseFirestore.getInstance();

        // create test value
        testValue = new HashMap<>();
        testValue.put("att1", "val1");
        testValue.put("att2", 2);

        // test values for the getWhere method only, to verify that it actually extracts the correct data
        testValueSpecial = new HashMap<>();
        testValueSpecial.put("att1", "val1");
        testValueSpecial.put("att2", 3);

        // countdown latch for the 3 insert statements
        final CountDownLatch latch = new CountDownLatch(3);

        // insert values for the get, getwhere and delete methods
        firestore.collection(COLLECTION).document("testGet").set(testValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                latch.countDown();
            }
        });

        firestore.collection(COLLECTION).document("testGetWhere").set(testValueSpecial).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                latch.countDown();
            }
        });

        firestore.collection(COLLECTION).document("testDelete").set(testValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                latch.countDown();
            }
        });

        latch.await();
    }

    // clear all entries present in the collection after the tests
    @After
    public void tearDown() throws InterruptedException{

        final CountDownLatch latch = new CountDownLatch(4);

        firestore.collection(COLLECTION).document("testGet").delete().addOnCompleteListener(task -> latch.countDown());
        firestore.collection(COLLECTION).document("testPut").delete().addOnCompleteListener(task -> latch.countDown());
        firestore.collection(COLLECTION).document("testGetWhere").delete().addOnCompleteListener(task -> latch.countDown());
        firestore.collection(COLLECTION).document("testDelete").delete().addOnCompleteListener(task -> latch.countDown());

        // wait for all operations to finish
        latch.await();

    }


    @Test
    public void testPut() throws InterruptedException{
        final CountDownLatch putLatch = new CountDownLatch(1);

        firestoreManager.put(COLLECTION, "testPut", testValue, new FirestoreManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                // verify insertion successful
                assertTrue(result);

                // signal operation completed
                putLatch.countDown();
            }
        });

        // wait for the insert operation to complete
        putLatch.await();
        final CountDownLatch getLatch = new CountDownLatch(1);

        // validate data after they are inserted into firestore
        firestore.collection(COLLECTION).document("testPut").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // verify data get successful
                assertTrue(documentSnapshot.exists());

                // verify the data inserted is correct
                assertEquals("val1", documentSnapshot.get("att1"));
                assertEquals(2L, documentSnapshot.get("att2"));    // it returns long value

                // signal operation completed
                getLatch.countDown();
            }
        });

        // wait for the get operation to complete
        getLatch.await();
    }

    @Test
    public void testGet() throws InterruptedException{
        final CountDownLatch getLatch = new CountDownLatch(1);

        // validate if data can be retrieved using the method
        firestoreManager.get(COLLECTION, "testGet", new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                // verify the data retrieved is correct
                assertEquals("val1", result.get("att1"));
                assertEquals(2L, result.get("att2"));

                // signal operation completed
                getLatch.countDown();
            }
        });

        // wait for the get operation to complete
        getLatch.await();
    }

    @Test
    public void testGetIdsWhere() throws InterruptedException{
        final CountDownLatch latch = new CountDownLatch(1);

        firestoreManager.getIdsWhere(COLLECTION, "att2", 3L, "att2", new FirestoreManager.Callback<List<String>>() {
            @Override
            public void onComplete(List<String> result) {
                // the result should only be length 1
                assertEquals(1, result.size());

                // verify the data retrieved is correct - it should return full values of the document
                assertEquals("testGetWhere", result.get(0));

                // signal operation completed
                latch.countDown();
            }
        });

        latch.await();
    }


    @Test
    public void testDelete() throws InterruptedException{

        // validate data initially exists
        final CountDownLatch getLatch = new CountDownLatch(1);
        firestore.collection(COLLECTION).document("testDelete").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // verify data get successful
                assertTrue(documentSnapshot.exists());
                // signal operation completed
                getLatch.countDown();
            }
        });
        getLatch.await();

        // delete data now
        final CountDownLatch deleteLatch = new CountDownLatch(1);
        firestoreManager.delete(COLLECTION, "testDelete", new FirestoreManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                assertTrue(result);
                deleteLatch.countDown();
            }
        });

        deleteLatch.await();

        // validate data does not exist anymore
        final CountDownLatch get2Latch = new CountDownLatch(1);
        firestore.collection(COLLECTION).document("testDelete").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // verify data get successful
                assertFalse(documentSnapshot.exists());
                // signal operation completed
                get2Latch.countDown();
            }
        });
        get2Latch.await();

    }
}
