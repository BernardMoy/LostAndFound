package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.lostandfound.ui.Login.LoginViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.logging.Handler;

public class LoginViewModelTest {

    // these are valid inputs
    private static final String VALID_EMAIL = "u0000000@warwick.ac.uk";
    private static final String VALID_PASSWORD = "yqwe902G*";

    private LoginViewModel viewModel;

    // mock the looper to handle live data
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    Handler handler = Mockito.mock(Handler.class);

    @Before
    public void setUp(){
        viewModel = new LoginViewModel();
    }

    @Test
    public void testValidateEmail(){
        // test for invalid inputs
        assertFalse(viewModel.validateEmail(""));
        assertEquals("Email cannot be empty", viewModel.getLoginError().getValue());

        // test for the valid input
        assertTrue(viewModel.validateEmail(VALID_EMAIL));
    }

    @Test
    public void testValidatePassword(){
        // test for invalid inputs
        assertFalse(viewModel.validatePassword(""));
        assertEquals("Password cannot be empty", viewModel.getLoginError().getValue());
        
        // test for the valid input
        assertTrue(viewModel.validatePassword(VALID_PASSWORD));
    }

}
