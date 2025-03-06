package com.example.lostandfound.Unit;

import android.net.Uri;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.FirebaseTestsSetUp;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FirebaseCloudFunctionsTest extends FirebaseTestsSetUp {
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

    If the tests fails, try waiting for a minute for the emulator to set up then try again.
     */

    private static final FirebaseFirestore firestore = getFirestore();
    private static final FirebaseAuth auth = getAuth();
    private static final FirebaseStorage storage = getStorage();


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
        Thread.sleep(2000);
        String uidLost = lostItemRef.getId();

        // create a claim with the lost item
        Map<String, Object> dataClaim = new HashMap<>();
        dataClaim.put(FirebaseNames.CLAIM_LOST_ITEM_ID, uidLost);
        Task<DocumentReference> task2 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaim);
        DocumentReference claimRef = Tasks.await(task2, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);
        String uidClaim = claimRef.getId();

        // delete the lost item
        Task<Void> task3 = firestore.collection(FirebaseNames.COLLECTION_LOST_ITEMS).document(uidLost).delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // assert that the claim no longer exists
        Task<DocumentSnapshot> task4 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).document(uidClaim).get();
        DocumentSnapshot snapshot = Tasks.await(task4, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

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
        Thread.sleep(2000);
        String uidFound = foundItemRef.getId();

        // create a claim with the found item
        Map<String, Object> dataClaim = new HashMap<>();
        dataClaim.put(FirebaseNames.CLAIM_FOUND_ITEM_ID, uidFound);
        Task<DocumentReference> task2 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaim);
        DocumentReference claimRef = Tasks.await(task2, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);
        String uidClaim = claimRef.getId();

        // delete the found item
        Task<Void> task3 = firestore.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).document(uidFound).delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // assert that the claim no longer exists
        Task<DocumentSnapshot> task4 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).document(uidClaim).get();
        DocumentSnapshot snapshot = Tasks.await(task4, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        assert !snapshot.exists();
    }

    @Test
    public void testNotificationsOnClaimsDeleted() throws ExecutionException, InterruptedException, TimeoutException {
        // create a claim
        Map<String, Object> dataClaim = new HashMap<>();
        dataClaim.put(FirebaseNames.CLAIM_IS_APPROVED, true);

        Task<DocumentReference> task1 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).add(dataClaim);
        DocumentReference claimItemRef = Tasks.await(task1, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);
        String uidClaim = claimItemRef.getId();

        // create a notif with type 3 and claim id = it
        Map<String, Object> dataNotif = new HashMap<>();
        dataNotif.put(FirebaseNames.NOTIFICATION_TYPE, 3);
        dataNotif.put(FirebaseNames.NOTIFICATION_CLAIM_ID, uidClaim);
        Task<DocumentReference> task2 = firestore.collection(FirebaseNames.COLLECTION_NOTIFICATIONS).add(dataNotif);
        DocumentReference notifRef = Tasks.await(task2, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);
        String uidNotif = notifRef.getId();

        // delete the claim item
        Task<Void> task3 = firestore.collection(FirebaseNames.COLLECTION_CLAIMED_ITEMS).document(uidClaim).delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // assert that the notif no longer exists
        Task<DocumentSnapshot> task4 = firestore.collection(FirebaseNames.COLLECTION_NOTIFICATIONS).document(uidNotif).get();
        DocumentSnapshot snapshot = Tasks.await(task4, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        assert !snapshot.exists();
    }

    // test involving firebase auth
    @Test
    public void testUserOnUserDeleted() throws ExecutionException, InterruptedException, TimeoutException {
        // create a user
        Task<AuthResult> task1 = auth.createUserWithEmailAndPassword("test@gmail.com", "testtest");
        AuthResult authResult = Tasks.await(task1, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // get the user id
        if (authResult.getUser() == null) {
            assert false;
            return;
        }
        FirebaseUser currentUser = authResult.getUser();
        String userId = currentUser.getUid();

        // create an entry in the user firestore database with
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put(FirebaseNames.USERS_EMAIL, "test@gmail.com");
        Task<Void> task2 = firestore.collection(FirebaseNames.COLLECTION_USERS).document(userId).set(dataUser);
        Tasks.await(task2, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // delete the user from auth
        Task<Void> task3 = currentUser.delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // assert that the user entry in firestore no longer exists
        Task<DocumentSnapshot> task4 = firestore.collection(FirebaseNames.COLLECTION_USERS).document(userId).get();
        DocumentSnapshot snapshot = Tasks.await(task4, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        assert !snapshot.exists();
    }

    // test involving firebase storage
    @Test
    public void testImageOnLostItemDeleted() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        // create a new lost item
        Map<String, Object> dataLost = new HashMap<>();
        dataLost.put(FirebaseNames.LOSTFOUND_ITEMNAME, "test");

        // The Task class allows async operations to block execution
        Task<DocumentReference> task1 = firestore.collection(FirebaseNames.COLLECTION_LOST_ITEMS).add(dataLost);
        DocumentReference lostItemRef = Tasks.await(task1, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);
        String uidLost = lostItemRef.getId();

        // add a storage image
        File img = File.createTempFile(uidLost, ".jpeg");
        try (OutputStream outputStream = Files.newOutputStream(img.toPath())) {
            outputStream.write(new byte[1024]);    // write empty image
        }

        // upload the storage image to firebase storage
        StorageReference ref = storage.getReference().child(FirebaseNames.FOLDER_LOST_IMAGE).child(uidLost);
        Task<UploadTask.TaskSnapshot> task2 = ref.putFile(Uri.fromFile(img));
        Tasks.await(task2, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // Delete the lost item
        Task<Void> task3 = firestore.collection(FirebaseNames.COLLECTION_LOST_ITEMS).document(uidLost).delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);
        Thread.sleep(4000);

        // assert the image no longer exists
        StorageReference ref2 = storage.getReference().child(FirebaseNames.FOLDER_LOST_IMAGE).child(uidLost);

        // catch the exception thrown when trying to access an image path that does not exist
        Exception e = null;
        try {
            Task<Uri> task4 = ref2.getDownloadUrl();
            Tasks.await(task4, 60, TimeUnit.SECONDS);
            Thread.sleep(2000);
        } catch (Exception ex) {
            e = ex;
        }
        Thread.sleep(4000);
        assert e != null;
    }

    @Test
    public void testImageOnFoundItemDeleted() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        // create a new found item
        Map<String, Object> dataFound = new HashMap<>();
        dataFound.put(FirebaseNames.LOSTFOUND_ITEMNAME, "test");

        // The Task class allows async operations to block execution
        Task<DocumentReference> task1 = firestore.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).add(dataFound);
        DocumentReference foundItemRef = Tasks.await(task1, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);
        String uidFound = foundItemRef.getId();

        // add a storage image
        File img = File.createTempFile(uidFound, ".jpeg");
        try (OutputStream outputStream = Files.newOutputStream(img.toPath())) {
            outputStream.write(new byte[1024]);    // write empty image
        }

        // upload the storage image to firebase storage
        StorageReference ref = storage.getReference().child(FirebaseNames.FOLDER_FOUND_IMAGE).child(uidFound);
        Task<UploadTask.TaskSnapshot> task2 = ref.putFile(Uri.fromFile(img));
        Tasks.await(task2, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // Delete the found item
        Task<Void> task3 = firestore.collection(FirebaseNames.COLLECTION_FOUND_ITEMS).document(uidFound).delete();
        Tasks.await(task3, 60, TimeUnit.SECONDS);
        Thread.sleep(4000);

        // assert the image no longer exists
        StorageReference ref2 = storage.getReference().child(FirebaseNames.FOLDER_FOUND_IMAGE).child(uidFound);

        // catch the exception thrown when trying to access an image path that does not exist
        Exception e = null;
        try {
            Task<Uri> task4 = ref2.getDownloadUrl();
            Tasks.await(task4, 60, TimeUnit.SECONDS);
            Thread.sleep(2000);
        } catch (Exception ex) {
            e = ex;
        }
        Thread.sleep(4000);
        assert e != null;
    }

    @After
    public void tearDown() throws ExecutionException, InterruptedException, TimeoutException {
        // clear all data
        deleteCollection(FirebaseNames.COLLECTION_LOST_ITEMS);
        deleteCollection(FirebaseNames.COLLECTION_CLAIMED_ITEMS);
        deleteCollection(FirebaseNames.COLLECTION_FOUND_ITEMS);
        deleteCollection(FirebaseNames.COLLECTION_NOTIFICATIONS);
        deleteCollection(FirebaseNames.COLLECTION_USERS);
        Thread.sleep(2000);
    }
}
