package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.lostandfound.ui.ForgotPassword.ForgotPasswordViewModel;
import com.example.lostandfound.ui.Login.LoginViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.logging.Handler;

public class ForgotPasswordViewModelTest {

    /*
    // these are valid inputs
    private static final String VALID_EMAIL = "u0000000@warwick.ac.uk";

    private ForgotPasswordViewModel viewModel;

    // mock the looper to handle live data
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    Handler handler = Mockito.mock(Handler.class);

    @Before
    public void setUp(){
        viewModel = new ForgotPasswordViewModel();
    }

    @Test
    public void testValidateEmail(){
        // test for invalid inputs
        assertFalse(viewModel.validateEmail(""));
        assertEquals("Email cannot be empty", viewModel.getForgotPasswordError().getValue());

        // test for the valid input
        assertTrue(viewModel.validateEmail(VALID_EMAIL));
    }

     */
}
