package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.ui.Login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class LoginActivityTest extends FirebaseTestsSetUp{
    private static final FirebaseFirestore firestore = getFirestore();
    private static final FirebaseAuth auth = getAuth();

    // user credentials
    private static String userID;  // the one in firebase auth, not the one in firestore
    private static final String email = "test@warwick";
    private static final String password = "1234ABCde";
    private static final String firstName = "FN";
    private static final String lastName = "LN";

    @Rule
    public final ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

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

    @Test
    public void testLoginFailed() throws InterruptedException {
        // make sure initially no users are logged in
        assertNull(auth.getCurrentUser());

        // input email
        Espresso.onView(ViewMatchers.withId(R.id.login_email)).perform(
                ViewActions.typeText(email)
        );
        // input password
        Espresso.onView(ViewMatchers.withId(R.id.login_password)).perform(
                ViewActions.typeText("whatisthis?")
        );

        // click the log in button
        Espresso.closeSoftKeyboard();  // close the keyboard first
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(
                ViewActions.click()
        );

        // wait for 5 seconds
        Thread.sleep(5000);

        // assert error message appears
        Espresso.onView(ViewMatchers.withId(R.id.login_error)).check(
                ViewAssertions.matches(ViewMatchers.withText("The provided user credentials are incorrect"))
        );
    }


    @After
    public void tearDown() throws ExecutionException, InterruptedException, TimeoutException {
        // delete user from firestore
        deleteCollection(FirebaseNames.COLLECTION_USERS);

        // delete current user
        if (auth.getCurrentUser() != null) {
            Tasks.await(auth.getCurrentUser().delete(), 60, TimeUnit.SECONDS);

        } else {
            // else if not signed in, SIGN IN AND DELETE IT
            CountDownLatch latch = new CountDownLatch(1);
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    latch.countDown();
                }
            });
            latch.await();

            if (auth.getCurrentUser() != null) {
                Tasks.await(auth.getCurrentUser().delete(), 60, TimeUnit.SECONDS);
            }
        }
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
