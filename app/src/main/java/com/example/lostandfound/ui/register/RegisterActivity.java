package com.example.lostandfound.ui.register;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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
import com.example.lostandfound.ui.User;
import com.example.lostandfound.ui.settings.SettingsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

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

        // init database
        db = FirebaseFirestore.getInstance();

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

        // button to register user to database
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the first name, last name, email and password that user entered
                String firstName = binding.registerFirstName.getText().toString();
                String lastName = binding.registerLastName.getText().toString();
                String email = binding.registerEmail.getText().toString();
                String password = binding.registerPassword.getText().toString();

                // reset error field
                registerViewModel.setRegisterError("");

                // reset all background fields
                binding.registerFirstName.setBackgroundResource(R.drawable.item_background_light_gray);
                binding.registerLastName.setBackgroundResource(R.drawable.item_background_light_gray);
                binding.registerEmail.setBackgroundResource(R.drawable.item_background_light_gray);
                binding.registerPassword.setBackgroundResource(R.drawable.item_background_light_gray);


                // validate first name
                if (firstName.isEmpty()){
                    registerViewModel.setRegisterError(getResources().getString(R.string.first_name_empty_error));
                    binding.registerFirstName.setBackgroundResource(R.drawable.item_background_light_gray_error);
                    return;
                }

                // validate last name
                if (lastName.isEmpty()){
                    registerViewModel.setRegisterError(getResources().getString(R.string.last_name_empty_error));
                    binding.registerLastName.setBackgroundResource(R.drawable.item_background_light_gray_error);
                    return;
                }

                // validate email
                Email em = new Email(RegisterActivity.this);
                String emailError = em.validateEmail(email);
                if (emailError != null){
                    registerViewModel.setRegisterError(emailError);
                    binding.registerEmail.setBackgroundResource(R.drawable.item_background_light_gray_error);
                    return;
                }

                // validate password
                Password pw = new Password(RegisterActivity.this);
                String passwordError = pw.validatePassword(password);
                if (passwordError != null){
                    registerViewModel.setRegisterError(passwordError);
                    binding.registerPassword.setBackgroundResource(R.drawable.item_background_light_gray_error);
                    return;
                }


                // set the progress bar to be visible
                binding.progressBar.setVisibility(View.VISIBLE);

                // Authenticate with firebase using user's email and hashed passwords
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // set progress bar to be gone
                                binding.progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // add the data to database where email is the id
                                    User user = new User(firstName, lastName, email);

                                    db.collection("user").document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    });

                                    // Account successfully created
                                    Toast.makeText(RegisterActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();

                                } else {
                                    // check if this is due to email already exists
                                    if (task.getException() != null && task.getException() instanceof FirebaseAuthUserCollisionException){
                                        registerViewModel.setRegisterError(getResources().getString(R.string.email_exists_error));
                                        binding.registerEmail.setBackgroundResource(R.drawable.item_background_light_gray_error);
                                        binding.registerPassword.setBackgroundResource(R.drawable.item_background_light_gray_error);

                                    } else {
                                        // Account failed to create due to other reasons
                                        Toast.makeText(RegisterActivity.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}