package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.lostandfound.ui.Register.RegisterViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.logging.Handler;

public class RegisterViewModelTest {

    // these are valid inputs
    private static final String VALID_FIRST_NAME = "FirstName";
    private static final String VALID_LAST_NAME = "LastName";
    private static final String VALID_EMAIL = "u0000000@warwick.ac.uk";
    private static final String VALID_PASSWORD = "yqwe902G*";

    private RegisterViewModel viewModel;

    // mock the looper to handle live data
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    Handler handler = Mockito.mock(Handler.class);

    @Before
    public void setUp(){
        viewModel = new RegisterViewModel();
    }

    @Test
    public void testValidateFirstName(){
        // test for invalid inputs
        boolean invalid = viewModel.validateFirstName("");
        assertFalse(invalid);
        assertEquals("First name cannot be empty", viewModel.getRegisterError().getValue());

        // test for the valid input
        assertTrue(viewModel.validateFirstName(VALID_FIRST_NAME));
    }

    @Test
    public void testValidateLastName(){
        // test for invalid inputs
        assertFalse(viewModel.validateLastName(""));
        assertEquals("Last name cannot be empty", viewModel.getRegisterError().getValue());

        // test for the valid input
        assertTrue(viewModel.validateLastName(VALID_LAST_NAME));
    }

    @Test
    public void testValidateEmail(){
        // test for invalid inputs
        assertFalse(viewModel.validateEmail(""));
        assertEquals("Email cannot be empty", viewModel.getRegisterError().getValue());

        assertFalse(viewModel.validateEmail("eeeeeeewarwick.ac.uk"));
        assertEquals("Please Register with your university email (@warwick.ac.uk)",
                viewModel.getRegisterError().getValue());

        assertFalse(viewModel.validateEmail("eeeeeee@gmail.com"));
        assertEquals("Please Register with your university email (@warwick.ac.uk)",
                viewModel.getRegisterError().getValue());


        // test for the valid input
        assertTrue(viewModel.validateEmail(VALID_EMAIL));
    }

    @Test
    public void testValidatePassword(){
        // test for invalid inputs
        assertFalse(viewModel.validatePassword("", ""));
        assertEquals("Password cannot be empty", viewModel.getRegisterError().getValue());

        assertFalse(viewModel.validatePassword("5tY*)io", "5tY*)io"));
        assertEquals("Password must be at least 8 characters long",
                viewModel.getRegisterError().getValue());

        assertFalse(viewModel.validatePassword("weiwewiiw=9*", "weiwewiiw=9*"));
        assertEquals("Password must have at least one uppercase and lowercase character",
                viewModel.getRegisterError().getValue());

        assertFalse(viewModel.validatePassword("weiweweuUE)", "weiweweuUE)"));
        assertEquals("Password must have at least one numerical character",
                viewModel.getRegisterError().getValue());

        assertFalse(viewModel.validatePassword("weiweEiw5", "weiweEiw5"));
        assertEquals("Password must have at least one special character",
                viewModel.getRegisterError().getValue());

        assertFalse(viewModel.validatePassword("weiweEiw5(", "weiweEiw5e("));
        assertEquals("Confirm password does not match",
                viewModel.getRegisterError().getValue());

        // test for the valid input
        assertTrue(viewModel.validatePassword(VALID_PASSWORD, VALID_PASSWORD));
    }

}
