package com.example.lostandfound.Integration;

import static org.junit.Assert.assertEquals;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.FirebaseTestsSetUp;
import com.example.lostandfound.R;
import com.example.lostandfound.ui.Register.RegisterActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.internal.matchers.Matches;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;


public class RegisterActivityTest extends FirebaseTestsSetUp {
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
    Test that the error message appear if attempting to register not using uni email
     */
    @Test
    public void testRegisterNotUniEmail() throws InterruptedException, ExecutionException, TimeoutException {
        // fill in the credentials
        Espresso.onView(ViewMatchers.withId(R.id.register_first_name)).perform(
                ViewActions.typeText(firstName)
        );
        Espresso.onView(ViewMatchers.withId(R.id.register_last_name)).perform(
                ViewActions.typeText(lastName)
        );
        Espresso.onView(ViewMatchers.withId(R.id.register_email)).perform(
                ViewActions.typeText("holo@gmail.com")
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

        // check if error is shown
        Espresso.onView(ViewMatchers.withId(R.id.register_error)).check(
                ViewAssertions.matches(ViewMatchers.withText("Please Register with your university email (@warwick.ac.uk)"))
        );

        Thread.sleep(2000);

        // assert that the error message is visible

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

}
