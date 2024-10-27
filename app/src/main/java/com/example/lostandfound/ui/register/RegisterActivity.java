package com.example.lostandfound.ui.register;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.Email;
import com.example.lostandfound.Password;
import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityLoginBinding;
import com.example.lostandfound.databinding.ActivityRegisterBinding;
import com.example.lostandfound.ui.settings.SettingsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up view model
        RegisterViewModel registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // get instance for firebase auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // button to exit activity
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // set up viewmodel observer for first, last name error, and email and password error
        registerViewModel.getFirstNameError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
                    // change the background of the firstName field
                    binding.registerFirstName.setBackgroundResource(R.drawable.item_background_light_gray);

                    // if the firstName error is empty, set view to be gone
                    binding.firstNameError.setVisibility(View.GONE);

                } else {
                    // change the background of the firstName field
                    binding.registerFirstName.setBackgroundResource(R.drawable.item_background_light_gray_error);

                    // display the firstName error
                    binding.firstNameError.setVisibility(View.VISIBLE);
                    binding.firstNameError.setText(s);

                }
            }
        });

        registerViewModel.getLastNameError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
                    // change the background of the lastName field
                    binding.registerLastName.setBackgroundResource(R.drawable.item_background_light_gray);

                    // if the lastName error is empty, set view to be gone
                    binding.lastNameError.setVisibility(View.GONE);

                } else {
                    // change the background of the lastName field
                    binding.registerLastName.setBackgroundResource(R.drawable.item_background_light_gray_error);

                    // display the lastName error
                    binding.lastNameError.setVisibility(View.VISIBLE);
                    binding.lastNameError.setText(s);

                }
            }
        });

        registerViewModel.getEmailError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
                    // change the background of the email field
                    binding.registerEmail.setBackgroundResource(R.drawable.item_background_light_gray);

                    // if the email error is empty, set view to be gone
                    binding.emailError.setVisibility(View.GONE);

                } else {
                    // change the background of the email field
                    binding.registerEmail.setBackgroundResource(R.drawable.item_background_light_gray_error);

                    // display the email error
                    binding.emailError.setVisibility(View.VISIBLE);
                    binding.emailError.setText(s);

                }
            }
        });

        registerViewModel.getPasswordError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
                    // change the background of the password field
                    binding.registerPassword.setBackgroundResource(R.drawable.item_background_light_gray);

                    // if the password error is empty, set view to be gone
                    binding.passwordError.setVisibility(View.GONE);

                } else {
                    // change the background of the password field
                    binding.registerPassword.setBackgroundResource(R.drawable.item_background_light_gray_error);

                    // display the password error
                    binding.passwordError.setVisibility(View.VISIBLE);
                    binding.passwordError.setText(s);

                }
            }
        });

        registerViewModel.getEmailExistsError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
                    // if the email exists error is empty, set view to be gone
                    binding.emailExistsError.setVisibility(View.GONE);

                } else {
                    // display the email exists error
                    binding.emailExistsError.setVisibility(View.VISIBLE);
                    binding.emailExistsError.setText(s);

                }
            }
        });

        // button to register user to database
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the first name, last name, email and password that user entered
                String firstName = binding.registerFirstName.getText().toString();
                String lastName = binding.registerLastName.getText().toString();
                String email = binding.registerEmail.getText().toString();
                String password = binding.registerPassword.getText().toString();

                // reset all error fields
                registerViewModel.setFirstNameError("");
                registerViewModel.setLastNameError("");
                registerViewModel.setEmailError("");
                registerViewModel.setPasswordError("");
                registerViewModel.setEmailExistsError("");

                // validate first name
                if (firstName.isEmpty()){
                    registerViewModel.setFirstNameError(getResources().getString(R.string.first_name_empty_error));
                    return;
                }

                // validate last name
                if (lastName.isEmpty()){
                    registerViewModel.setLastNameError(getResources().getString(R.string.last_name_empty_error));
                    return;
                }

                // validate email
                Email em = new Email(RegisterActivity.this);
                String emailError = em.validateEmail(email);
                if (emailError != null){
                    registerViewModel.setEmailError(emailError);
                    return;
                }

                // validate password
                Password pw = new Password(RegisterActivity.this);
                String passwordError = pw.validatePassword(password);
                if (passwordError != null){
                    registerViewModel.setPasswordError(passwordError);
                    return;
                }

                // hash password
                String hashedPassword = pw.generateHash(password);
                if (hashedPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Error while hashing password", Toast.LENGTH_SHORT).show();
                }

                // set the progress bar to be visible
                binding.progressBar.setVisibility(View.VISIBLE);

                // Authenticate with firebase using user's email and hashed passwords
                mAuth.createUserWithEmailAndPassword(email, hashedPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // set progress bar to be gone
                                binding.progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // Account successfully created
                                    Toast.makeText(RegisterActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();

                                } else {
                                    // check if this is due to email already exists
                                    if (task.getException() != null && task.getException() instanceof FirebaseAuthUserCollisionException){
                                        registerViewModel.setEmailExistsError(getResources().getString(R.string.email_exists_error));

                                    } else {
                                        // Account failed to create due to other reasons
                                        Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}