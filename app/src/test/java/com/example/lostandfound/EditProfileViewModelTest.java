package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.lostandfound.ui.EditProfile.EditProfileViewModel;
import com.example.lostandfound.ui.Login.LoginViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.logging.Handler;

public class EditProfileViewModelTest {

    private EditProfileViewModel viewModel;

    // mock the looper to handle live data
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    Handler handler = Mockito.mock(Handler.class);

    @Before
    public void setUp(){
        viewModel = new EditProfileViewModel();
    }

    @Test
    public void testValidateEmail(){
        // test for invalid inputs
        viewModel.setFirstName("");
        viewModel.setLastName("Lastname");
        assertFalse(viewModel.validateNames());
        assertEquals("First name cannot be empty", viewModel.getProfileError().getValue());
    }
}
