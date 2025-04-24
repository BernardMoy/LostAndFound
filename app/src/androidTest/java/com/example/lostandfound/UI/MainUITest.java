package com.example.lostandfound.UI;

import static org.junit.Assert.assertNotNull;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostandfound.FirebaseTestsSetUp;
import com.example.lostandfound.MainActivity;
import com.example.lostandfound.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;


public class MainUITest extends FirebaseTestsSetUp {
    // get auth
    private FirebaseAuth auth = getAuth();
    private String userID = "";

    @Rule
    public final ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        // create test user
        String email = "test@warwick";
        String password = "1234ABCde";

        CountDownLatch latch = new CountDownLatch(1);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                userID = auth.getCurrentUser().getUid();
                latch.countDown();
            }
        });
        latch.await();

        // after user creation, the user is logged in
        // assert current user is not null
        assertNotNull(auth.getCurrentUser());
    }

    /*
    Test that the floating action button works properly
     */
    @Test
    public void testFloatingActionButton() throws InterruptedException {
        // click the general FAB
        Espresso.onView(ViewMatchers.withId(R.id.floating_action_button)).perform(
                ViewActions.click()
        );

        // click the new lost FAB
        Thread.sleep(2000);
        Espresso.onView(ViewMatchers.withId(R.id.lost_floating_action_button)).perform(
                ViewActions.click()
        );

        // verify the new lost activity is launched
        Thread.sleep(1000);
        Espresso.onView(ViewMatchers.withText("New lost item")).check(
                ViewAssertions.matches(ViewMatchers.isDisplayed())
        );
    }
}
