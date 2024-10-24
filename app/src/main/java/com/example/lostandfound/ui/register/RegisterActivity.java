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
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityLoginBinding;
import com.example.lostandfound.databinding.ActivityRegisterBinding;
import com.example.lostandfound.ui.settings.SettingsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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


        // button to register user to database
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the progress bar to be visible
                binding.progressBar.setVisibility(View.VISIBLE);

                // get the first name, last name, email and password that user entered
                String firstName = binding.registerFirstName.getText().toString();
                String lastName = binding.registerLastName.getText().toString();
                String email = binding.registerEmail.getText().toString();
                String password = binding.registerPassword.getText().toString();
                String hashedPassword = "";

                // hash the password using SHA-256
                try{
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    hashedPassword = Base64.getEncoder().encodeToString(hash);

                } catch (NoSuchAlgorithmException e){
                    Toast.makeText(RegisterActivity.this, "Error while handling password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authenticate with firebase using user's email and hashed passwords
                mAuth.createUserWithEmailAndPassword(email, hashedPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // set progress bar to be gone
                                binding.progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}