package com.example.lostandfound;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import com.example.lostandfound.ui.register.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {
    @Rule
    public ActivityScenarioRule<RegisterActivity> activityScenarioRule = new ActivityScenarioRule<>(RegisterActivity.class);

    private String validFirstName = "FirstName";
    private String validLastName = "LastName";
    private String validEmail = "u0000000@warwick.ac.uk";
    private String validPassword = "yqwe902G*";

    /*
    @Test
    public void testFirstNameEmptyError(){
        // modify test input data here
        String firstName = "";
        String lastName = validLastName;
        String email = validEmail;
        String password = validPassword;

        onView(withId(R.id.register_first_name)).perform(replaceText(firstName));
        onView(withId(R.id.register_last_name)).perform(replaceText(lastName));
        onView(withId(R.id.register_email)).perform(replaceText(email));
        onView(withId(R.id.register_password)).perform(replaceText(password));

        // click the register button
        onView(withId(R.id.register_button)).perform(click());

        // verify that the correct error message is displayed
        onView(withId(R.id.register_error)).check(matches(withText(R.string.first_name_empty_error)));

    }
     */
}