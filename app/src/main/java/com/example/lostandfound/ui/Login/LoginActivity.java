package com.example.lostandfound.ui.Login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.FirebaseManagers.FCMTokenManager;
import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager;
import com.example.lostandfound.FirebaseManagers.UserManager;
import com.example.lostandfound.R;
import com.example.lostandfound.Utility.ErrorCallback;
import com.example.lostandfound.Utility.FontSizeManager;
import com.example.lostandfound.databinding.ActivityLoginBinding;
import com.example.lostandfound.ui.ForgotPassword.ForgotPasswordActivity;
import com.example.lostandfound.ui.Register.RegisterActivity;

/*
Login activity that contains directions to register and forgot password
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up view model
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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

        // set font size for this XML activity
        ViewGroup parentView = binding.main;
        FontSizeManager.INSTANCE.setFontSizeXML(parentView, LoginActivity.this);


        // set underlined text for the Register and forgot password textview
        binding.register.setPaintFlags(binding.register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.forgotPassword.setPaintFlags(binding.register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // start Register activity when the Register text is clicked
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

        // set up viewmodel observer for the Login errors
        loginViewModel.getLoginError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()) {
                    // set the view to be gone
                    binding.loginError.setVisibility(View.GONE);

                } else {
                    // display the error
                    binding.loginError.setText(s);
                    binding.loginError.setVisibility(View.VISIBLE);
                }
            }
        });

        // set function for log in button
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset error fields
                resetErrorFields();

                // get the input email and password
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                // validate email
                if (!loginViewModel.validateEmail(email)) {
                    binding.loginEmail.setBackgroundResource(R.drawable.background_light_gray_error);
                    return;
                }

                // validate password
                if (!loginViewModel.validatePassword(password)) {
                    binding.loginPassword.setBackgroundResource(R.drawable.background_light_gray_error);
                    return;
                }

                // set the progress bar to be visible
                binding.progressBar.setVisibility(View.VISIBLE);

                // set login button disabled
                binding.loginButton.setEnabled(false);

                // Login with user
                FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager(LoginActivity.this);
                firebaseAuthManager.loginUser(email, password, new ErrorCallback() {
                    @Override
                    public void onComplete(String error) {
                        if (!error.trim().isEmpty()) {
                            loginViewModel.setLoginError(error);

                            binding.progressBar.setVisibility(View.GONE);
                            binding.loginButton.setEnabled(true);
                            return;
                        }

                        // update FCM token only when login successful
                        FCMTokenManager.INSTANCE.updateFCMToken(UserManager.getUserID(), new FCMTokenManager.FCMTokenUpdateCallback() {
                            @Override
                            public void onComplete(boolean success) {
                                if (!success) {
                                    loginViewModel.setLoginError("Failed generating an FCM token");
                                    return;
                                }

                                // no errors
                                // if sign in successful, current user would not be null
                                if (UserManager.isUserLoggedIn()) {
                                    // finish activity and display message
                                    Toast.makeText(LoginActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    // method to reset all error fields, to prevent inconsistent error indicators
    private void resetErrorFields() {
        // reset error value
        loginViewModel.setLoginError("");

        // reset all background fields
        binding.loginEmail.setBackgroundResource(R.drawable.background_light_gray_selector);
        binding.loginPassword.setBackgroundResource(R.drawable.background_light_gray_selector);
    }
}