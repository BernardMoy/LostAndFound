package com.example.lostandfound;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/*
Set up class for firebase services tests
By inheriting this class, it gains methods to get firestore, auth and storage
that are using the firebase emulator.
It also provides methods to delete a collection after the test
 */
public class FirebaseTestsSetUp {
    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;
    private static FirebaseStorage storage;
    private static boolean initialized = false;


    /*
    All classes that uses firebase services extends from this class.
    Ensure that the emulator is only initialised once, otherwise it generates an exception.
    Having the same parent class ensures the boolean variable *initialised* is shared across all test files.
     */
    @BeforeClass
    public static void setupClass() {
        if (!initialized) {
            // create emulated firestore environment before everything is set up, and is performed only once
            firestore = FirebaseFirestore.getInstance();
            firestore.useEmulator("10.0.2.2", 8080);   // use the emulator host, not 127.0.0.1 localhost
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            firestore.setFirestoreSettings(settings);

            // create auth emulator
            auth = FirebaseAuth.getInstance();
            auth.useEmulator("10.0.2.2", 9099);

            // create storage emulator
            storage = FirebaseStorage.getInstance();
            storage.useEmulator("10.0.2.2", 9199);

            initialized = true;
        }

    }

    public static FirebaseFirestore getFirestore() {
        return firestore;
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static FirebaseStorage getStorage() {
        return storage;
    }

    // method to delete a collection in firebase firestore
    public void deleteCollection(String name) throws ExecutionException, InterruptedException, TimeoutException {
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
        Thread.sleep(2000);
    }

}
