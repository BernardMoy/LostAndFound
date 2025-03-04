package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.ui.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class LoginActivityTest {
    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;

    // user credentials
    private static String userID;  // the one in firebase auth, not the one in firestore
    private static final String email = "test@warwick";
    private static final String password = "1234ABCde";
    private static final String firstName = "FN";
    private static final String lastName = "LN";

    @Rule
    public final ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @BeforeClass
    public static void setupClass() {
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
    }

    /*
    Create the user in the database
     */
    @Before
    public void setUp() throws InterruptedException, ExecutionException, TimeoutException {
        // create test user
        CountDownLatch latch = new CountDownLatch(1);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        userID = user.getUid();
                    }
                    latch.countDown();
                } else {
                    fail("Failed while creating a user");
                    latch.countDown();
                }
            }
        });
        latch.await();

        // add the test user credentials to firestore
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put(FirebaseNames.USERS_EMAIL, email);
        dataUser.put(FirebaseNames.USERS_FIRSTNAME, firstName);
        dataUser.put(FirebaseNames.USERS_LASTNAME, lastName);
        dataUser.put(FirebaseNames.USERS_AVATAR, "");  // empty avatar

        Task<DocumentReference> task1 = firestore.collection(FirebaseNames.COLLECTION_USERS).add(dataUser);
        DocumentReference ref = Tasks.await(task1, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);

        // INITIALLY, LOG OUT THE USER
        auth.signOut();
    }

    /*
    Test that a user can successfully input credentials and login.
     */
    @Test
    public void testLoginSuccess() throws InterruptedException {
        // make sure initially no users are logged in
        assertNull(auth.getCurrentUser());

        // input email
        Espresso.onView(ViewMatchers.withId(R.id.login_email)).perform(
                ViewActions.typeText(email)
        );
        // input password
        Espresso.onView(ViewMatchers.withId(R.id.login_password)).perform(
                ViewActions.typeText(password)
        );

        // click the log in button
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(
                ViewActions.click()
        );

        // wait for 5 seconds
        Thread.sleep(5000);

        // assert the user is logged in
        assertEquals(userID, auth.getCurrentUser().getUid());
    }

    @After
    public void tearDown() throws ExecutionException, InterruptedException, TimeoutException {
        // delete current user
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().delete();
        }

        deleteCollection(FirebaseNames.COLLECTION_USERS);
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
        Thread.sleep(2000);
    }

}
