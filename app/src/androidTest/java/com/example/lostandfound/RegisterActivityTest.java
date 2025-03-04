package com.example.lostandfound;

import static org.junit.Assert.assertEquals;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.ui.Register.RegisterActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class RegisterActivityTest extends FirebaseTestsSetUp{
    private static final FirebaseFirestore firestore = getFirestore();
    private static final FirebaseAuth auth = getAuth();

    // user credentials
    private static final String email = "te23eweqwqweeqwads222222st@warwick.ac.uk";
    private static final String password = "1234ABCde*";
    private static final String firstName = "FN";
    private static final String lastName = "LN";

    @Rule
    public final ActivityScenarioRule<RegisterActivity> activityRule = new ActivityScenarioRule<>(RegisterActivity.class);


    /*
    Create the user in the database
     */
    @Before
    public void setUp() throws InterruptedException, ExecutionException, TimeoutException {

    }

    /*
    Test that an entry of user verification code has been generated after registeration
     */
    @Test
    public void testRegisterSuccessful() throws InterruptedException, ExecutionException, TimeoutException {
        // fill in the credentials
        Espresso.onView(ViewMatchers.withId(R.id.register_first_name)).perform(
                ViewActions.typeText(firstName)
        );
        Espresso.onView(ViewMatchers.withId(R.id.register_last_name)).perform(
                ViewActions.typeText(lastName)
        );
        Espresso.onView(ViewMatchers.withId(R.id.register_email)).perform(
                ViewActions.typeText(email)
        );
        Espresso.onView(ViewMatchers.withId(R.id.register_password)).perform(
                ViewActions.typeText(password)
        );
        Espresso.onView(ViewMatchers.withId(R.id.register_password_confirm)).perform(
                ViewActions.typeText(password)
        );

        // click the register button
        Espresso.closeSoftKeyboard();  // close the keyboard first
        Espresso.onView(ViewMatchers.withId(R.id.register_button)).perform(
                ViewActions.click()
        );

        Thread.sleep(5000);

        // assert that an entry of user verification has been created
        Task<QuerySnapshot> task = firestore.collection(FirebaseNames.COLLECTION_USER_VERIFICATIONS).get();
        QuerySnapshot querySnapshot = Tasks.await(task, 60, TimeUnit.SECONDS);
        assertEquals(1, querySnapshot.size());
    }


    @After
    public void tearDown() throws ExecutionException, InterruptedException, TimeoutException {
        deleteCollection(FirebaseNames.COLLECTION_USER_VERIFICATIONS);

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
