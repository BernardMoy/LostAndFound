package com.example.lostandfound;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.lostandfound.ui.Register.RegisterActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {
    @Rule
    public ActivityScenarioRule<RegisterActivity> activityScenarioRule = new ActivityScenarioRule<>(RegisterActivity.class);

    private final String validFirstName = "FirstName";
    private final String validLastName = "LastName";
    private final String validEmail = "u0000000@warwick.ac.uk";
    private final String validPassword = "yqwe902G*";

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

        // click the Register button
        onView(withId(R.id.register_button)).perform(click());

        // verify that the correct error message is displayed
        onView(withId(R.id.register_error)).check(matches(withText(R.string.first_name_empty_error)));

    }
     */
}