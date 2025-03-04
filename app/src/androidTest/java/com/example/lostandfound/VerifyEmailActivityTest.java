package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.Utility.Hasher;
import com.example.lostandfound.ui.VerifyEmail.VerifyEmailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
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


public class VerifyEmailActivityTest extends FirebaseTestsSetUp{
    private static final FirebaseFirestore firestore = getFirestore();
    private static final FirebaseAuth auth = getAuth();

    // user credentials
    private static final String email = "te23eweqwqweeqwads222222st@warwick.ac.uk";
    private static final String password = "1234ABCde*";
    private static final String firstName = "FN";
    private static final String lastName = "LN";

    static Intent i;

    static {
        // create an intent with extra values
        i = new Intent(ApplicationProvider.getApplicationContext(), VerifyEmailActivity.class);
        i.putExtra("first_name", firstName);
        i.putExtra("last_name", lastName);
        i.putExtra("email", email);
        i.putExtra("password", password);
    }

    @Rule
    public ActivityScenarioRule<VerifyEmailActivity> activityScenarioRule = new ActivityScenarioRule<>(i);

    /*
    Create the user verification entry in the database
     */
    @Before
    public void setUp() throws InterruptedException, ExecutionException, TimeoutException {
        // create the entry of the user verification code
        Map<String, Object> dataUserVerification = new HashMap<>();
        dataUserVerification.put(FirebaseNames.USER_VERIFICATIONS_TIMESTAMP, System.currentTimeMillis());
        dataUserVerification.put(FirebaseNames.USER_VERIFICATIONS_HASHEDCODE, Hasher.hash("273292"));

        Task<Void> task1 = firestore.collection(FirebaseNames.COLLECTION_USER_VERIFICATIONS).document(email).set(dataUserVerification);
        Tasks.await(task1, 60, TimeUnit.SECONDS);
        Thread.sleep(2000);
    }

    /*
    Test that inputting the correct verification code creates the user
     */
    @Test
    public void testVerifyEmailSuccessful() throws InterruptedException, ExecutionException, TimeoutException {
        // assert the intent values are correctly passed
        assertEquals(email, i.getStringExtra("email"));
        assertEquals(password, i.getStringExtra("password"));

        // input the correct verification code (273292)
        Espresso.onView(ViewMatchers.withId(R.id.code1)).perform(
                ViewActions.typeText("2")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code2)).perform(
                ViewActions.typeText("7")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code3)).perform(
                ViewActions.typeText("3")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code4)).perform(
                ViewActions.typeText("2")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code5)).perform(
                ViewActions.typeText("9")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code6)).perform(
                ViewActions.typeText("2")
        );

        // click the verify email button
        Espresso.closeSoftKeyboard();  // close the keyboard first
        Espresso.onView(ViewMatchers.withId(R.id.verify_email_button)).perform(
                ViewActions.click()
        );

        // assert the user is created
        Thread.sleep(5000);

        // assert user created in firebase auth (The user is automatically logged in)
        assertNotNull(auth.getCurrentUser());
        assertEquals(email, auth.getCurrentUser().getEmail());

        // assert user created in firebase firestore (Its document id is the uid)
        CountDownLatch latch = new CountDownLatch(1);
        firestore.collection(FirebaseNames.COLLECTION_USERS).document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        assert (documentSnapshot.exists());
                        assertEquals(firstName, documentSnapshot.get("firstName"));
                        assertEquals(lastName, documentSnapshot.get("lastName"));
                        latch.countDown();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fail("User not found in firestore");
                        latch.countDown();
                    }
                });

        latch.await();

    }

    @Test
    public void testVerifyEmailFailed() throws InterruptedException {
        // input the correct verification code (273292)
        Espresso.onView(ViewMatchers.withId(R.id.code1)).perform(
                ViewActions.typeText("2")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code2)).perform(
                ViewActions.typeText("8")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code3)).perform(
                ViewActions.typeText("3")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code4)).perform(
                ViewActions.typeText("2")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code5)).perform(
                ViewActions.typeText("9")
        );
        Espresso.onView(ViewMatchers.withId(R.id.code6)).perform(
                ViewActions.typeText("2")
        );

        // click the verify email button
        Espresso.closeSoftKeyboard();  // close the keyboard first
        Espresso.onView(ViewMatchers.withId(R.id.verify_email_button)).perform(
                ViewActions.click()
        );

        // assert the user is created
        Thread.sleep(5000);

        // assert an error message is displayed
        Espresso.onView(ViewMatchers.withId(R.id.verification_error)).check(
                ViewAssertions.matches(ViewMatchers.withText("Invalid verification code"))
        );
    }


    @After
    public void tearDown() throws ExecutionException, InterruptedException, TimeoutException {
        deleteCollection(FirebaseNames.COLLECTION_USER_VERIFICATIONS);
        deleteCollection(FirebaseNames.COLLECTION_USERS);

        // delete current user at the end, as this will trigger cloud functions
        if (auth.getCurrentUser() != null) {
            Tasks.await(auth.getCurrentUser().delete(), 60, TimeUnit.SECONDS);
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
