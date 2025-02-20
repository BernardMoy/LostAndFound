package com.example.lostandfound;

import com.example.lostandfound.Data.FirebaseNames;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FirebaseCloudFunctionsTest {
    /*
    BEFORE RUNNING THIS TEST
    cd to the firebase directory at home then run:
    firebase emulators:start

    to kill a process on a running port, first search
    netstat -ano | findstr :8080
    then do
    taskkill /PID 22348 /F

    Remember if you want both the firebase firestore and the cloud functions to work
    do not do firebase emulators:start --only firestore
     */

    private static FirebaseFirestore firestore;

    @BeforeClass
    public static void setupClass() {
        // create emulated firestore environment before everything is set up, and is performed only once
        firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator("10.0.2.2", 8080);   // use the emulator host, not 127.0.0.1 localhost
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        firestore.setFirestoreSettings(settings);
    }

    @Before
    public void setUp() {
        // set up performed multiple times before the start of each test
        // to prevent duplicate useEmulator() calls, the method cannot be used here
    }

    @Test
    public void testClaimsOnLostItemDeleted() throws ExecutionException, InterruptedException, TimeoutException {

        // create a lost item
        Map<String, Object> dataLost = new HashMap<>();
        dataLost.put(FirebaseNames.LOSTFOUND_ITEMNAME, "test");

        // The Task class allows async operations to block execution
        Task<DocumentReference> task1 = firestore.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost);
        DocumentReference lostItemRef = Tasks.await(task1, 60, TimeUnit.SECONDS);
        String uidLost = lostItemRef.getId();

        // create a claim with the lost item
        Map<String, Object> dataClaim = new HashMap<>();
        dataClaim.put(FirebaseNames.CLAIM_LOST_ITEM_ID, uidLost);
        Task<DocumentReference> task2 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaim);
        DocumentReference claimRef = Tasks.await(task2, 60, TimeUnit.SECONDS);
        String uidClaim = claimRef.getId();

        // delete the lost item
        Task<Void> task3 = firestore.collection(FirebaseNames.COLLECTION_LOST_ITEMS).document(uidLost).delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);

        // assert that the claim no longer exists
        Thread.sleep(2000);     // wait for 2 seconds for the function to activate
        Task<DocumentSnapshot> task4 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).document(uidClaim).get();
        DocumentSnapshot snapshot = Tasks.await(task4, 60, TimeUnit.SECONDS);
        assert !snapshot.exists();
    }

    @Test
    public void testClaimsOnFoundItemDeleted() throws ExecutionException, InterruptedException, TimeoutException {
        // create a found item
        Map<String, Object> dataFound = new HashMap<>();
        dataFound.put(FirebaseNames.LOSTFOUND_ITEMNAME, "test");

        // The Task class allows async operations to block execution
        Task<DocumentReference> task1 = firestore.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound);
        DocumentReference foundItemRef = Tasks.await(task1, 60, TimeUnit.SECONDS);
        String uidFound = foundItemRef.getId();

        // create a claim with the found item
        Map<String, Object> dataClaim = new HashMap<>();
        dataClaim.put(FirebaseNames.CLAIM_FOUND_ITEM_ID, uidFound);
        Task<DocumentReference> task2 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaim);
        DocumentReference claimRef = Tasks.await(task2, 60, TimeUnit.SECONDS);
        String uidClaim = claimRef.getId();

        // delete the found item
        Task<Void> task3 = firestore.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).document(uidFound).delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);

        // assert that the claim no longer exists
        Thread.sleep(2000);     // wait for 2 seconds for the function to activate
        Task<DocumentSnapshot> task4 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).document(uidClaim).get();
        DocumentSnapshot snapshot = Tasks.await(task4, 60, TimeUnit.SECONDS);
        assert !snapshot.exists();
    }

    @Test
    public void testItemsOnUserDeleted() throws ExecutionException, InterruptedException, TimeoutException {

    }

    // test involving firebase auth
    @Test
    public void testUserOnUserDeleted() throws ExecutionException, InterruptedException, TimeoutException {

    }

    // test involving firebase storage
    @Test
    public void testImageOnLostItemDeleted() throws ExecutionException, InterruptedException, TimeoutException {

    }

    @Test
    public void testImageOnFoundItemDeleted() throws ExecutionException, InterruptedException, TimeoutException {

    }

    @After
    public void tearDown() throws ExecutionException, InterruptedException, TimeoutException {
        // clear all data
        deleteCollection(FirebaseNames.COLLECTION_LOST_ITEMS);
        deleteCollection(FirebaseNames.COLLECTION_CLAIMED_ITEMS);
    }

    // private method to delete all elements inside a collection
    private void deleteCollection(String name) throws ExecutionException, InterruptedException, TimeoutException {
        Task<QuerySnapshot> taskGet = firestore.collection(name).get();
        QuerySnapshot docs = Tasks.await(taskGet, 60, TimeUnit.SECONDS);

        // create a list of delete tasks for each doc
        List<Task<Void>> deleteTasks = new ArrayList<>();
        for (DocumentSnapshot doc : docs) {
            Task<Void> deleteTask = firestore.collection(name)
                    .document(doc.getId())
                    .delete();
            deleteTasks.add(deleteTask);
        }
        // execute all tasks
        Tasks.await(Tasks.whenAll(deleteTasks), 60, TimeUnit.SECONDS);
    }
}
