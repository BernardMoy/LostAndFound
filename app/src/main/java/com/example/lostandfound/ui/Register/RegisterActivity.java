package com.example.lostandfound.ui.Register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityRegisterBinding;
import com.example.lostandfound.ui.VerifyEmail.VerifyEmailActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up view model
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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

        // set up viewmodel observer for first, last name error, and email and password error
        registerViewModel.getRegisterError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
                    // set the view to be gone
                    binding.registerError.setVisibility(View.GONE);

                } else {
                    // display the error
                    binding.registerError.setText(s);
                    binding.registerError.setVisibility(View.VISIBLE);
                }
            }
        });

        // button to Register user to database
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the first name, last name, email and password that user entered
                String firstName = binding.registerFirstName.getText().toString();
                String lastName = binding.registerLastName.getText().toString();
                String email = binding.registerEmail.getText().toString();
                String password = binding.registerPassword.getText().toString();

                // reset error field
                resetErrorFields();

                // validate first name
                if (!registerViewModel.validateFirstName(firstName)){
                    binding.registerFirstName.setBackgroundResource(R.drawable.background_light_gray_error);
                    return;
                }

                // validate last name
                if (!registerViewModel.validateLastName(lastName)){
                    binding.registerLastName.setBackgroundResource(R.drawable.background_light_gray_error);
                    return;
                }

                // validate email
                if (!registerViewModel.validateEmail(email)){
                    binding.registerEmail.setBackgroundResource(R.drawable.background_light_gray_error);
                    return;
                }

                // validate password
                if (!registerViewModel.validatePassword(password)){
                    binding.registerPassword.setBackgroundResource(R.drawable.background_light_gray_error);
                    return;
                }

                // if all tests pass, start confirm email intent
                Intent i = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                // pass the user data to the new intent
                i.putExtra("first_name", firstName);
                i.putExtra("last_name", lastName);
                i.putExtra("email", email);
                i.putExtra("password", password);

                startActivity(i);
                finish();   // closes current activity
            }
        });
    }

    // method to reset all error fields, to prevent inconsistent error indicators
    private void resetErrorFields(){
        // reset error value
        registerViewModel.setRegisterError("");

        // reset all background fields
        binding.registerFirstName.setBackgroundResource(R.drawable.background_light_gray_selector);
        binding.registerLastName.setBackgroundResource(R.drawable.background_light_gray_selector);
        binding.registerEmail.setBackgroundResource(R.drawable.background_light_gray_selector);
        binding.registerPassword.setBackgroundResource(R.drawable.background_light_gray_selector);
    }
}