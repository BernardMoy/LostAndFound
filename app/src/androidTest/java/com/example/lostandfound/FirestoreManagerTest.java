package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FirestoreManagerTest {

    private final String COLLECTION = "test_collection";
    private FirestoreManager firestoreManager;
    private FirebaseFirestore firestore;

    private Map<String, Object> testValue;

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

        // countdown latch for the 3 insert statements
        final CountDownLatch latch = new CountDownLatch(2);

        // insert values for the get, update and delete methods
        firestore.collection(COLLECTION).document("testGet").set(testValue).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        firestore.collection(COLLECTION).document("testUpdate").delete().addOnCompleteListener(task -> latch.countDown());
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
