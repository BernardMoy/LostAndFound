package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FirestoreManagerTest {

    private String COLLECTION = "test_collection";
    private Context ctx;
    private FirestoreManager firestoreManager;
    private FirebaseFirestore firebaseFirestore;

    private Map<String, Object> testValue;

    @Before
    public void setUp(){
        // get context
        ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // Create firestore manager
        firestoreManager = new FirestoreManager(ctx);

        // Create instance of firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // create test value
        testValue = new HashMap<>();
        testValue.put("att1", "val1");
        testValue.put("att2", 2);

    }

    @Test
    public void testPut() throws InterruptedException{
        final CountDownLatch insertLatch = new CountDownLatch(1);

        firestoreManager.put(COLLECTION, "testPut", testValue, new FirestoreManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                // verify insertion successful
                assertTrue(result);

                // signal operation completed
                insertLatch.countDown();
            }
        });

        // wait for the insert operation to complete
        insertLatch.await();
        final CountDownLatch fetchLatch = new CountDownLatch(1);

        // validate data after they are inserted into firestore
        firebaseFirestore.collection(COLLECTION).document("testPut").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // verify data get successful
                assertTrue(documentSnapshot.exists());

                // verify the data inserted is correct
                assertEquals("val1", documentSnapshot.get("att1"));
                assertEquals(Long.valueOf(2), documentSnapshot.get("att2"));    // it returns long value

                // signal operation completed
                fetchLatch.countDown();
            }
        });

        // wait for the get operation to complete
        fetchLatch.await();
    }
}
