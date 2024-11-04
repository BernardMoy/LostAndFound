package com.example.lostandfound.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityLoginBinding;
import com.example.lostandfound.databinding.ActivitySettingsBinding;
import com.example.lostandfound.ui.register.RegisterActivity;
import com.example.lostandfound.ui.register.RegisterViewModel;
import com.example.lostandfound.ui.settings.SettingsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up view model
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // get instance for firebase auth
        mAuth = FirebaseAuth.getInstance();
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

        // set underlined text for the register and forgot password textview
        binding.register.setPaintFlags(binding.register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.forgotPassword.setPaintFlags(binding.register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // start register activity when the register text is clicked
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        // set up viewmodel observer for the login errors
        loginViewModel.getLoginError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
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
                // get the input email and password
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                // reset all boxes backgrounds
                binding.loginEmail.setBackgroundResource(R.drawable.item_background_light_gray);
                binding.loginPassword.setBackgroundResource(R.drawable.item_background_light_gray);

                // reset error field
                loginViewModel.setLoginError("");

                // check if the user is already logged in
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    loginViewModel.setLoginError(getResources().getString(R.string.invalid_login_error));
                    return;
                }

                // check if email or password empty
                if (email.isEmpty()) {
                    loginViewModel.setLoginError(getResources().getString(R.string.email_empty_error));
                    binding.loginEmail.setBackgroundResource(R.drawable.item_background_light_gray_error);
                    return;
                }

                if (password.isEmpty()){
                    loginViewModel.setLoginError(getResources().getString(R.string.password_empty_error));
                    binding.loginPassword.setBackgroundResource(R.drawable.item_background_light_gray_error);
                    return;
                }

                // set the progress bar to be visible
                binding.progressBar.setVisibility(View.VISIBLE);

                // login with user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                binding.progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // Sign in success
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // get user's other info from the db
                                    db.collection("users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()){
                                                String firstName = documentSnapshot.getString("firstName");
                                                String lastName = documentSnapshot.getString("lastName");

                                                // Save the extra user credentials (First, last name, avatar) in sharedpreferences
                                                SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("firstName", firstName);
                                                editor.putString("lastName", lastName);
                                                editor.putString("email", email);
                                                editor.apply();

                                                // Display log in successful message
                                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                                // exit activity
                                                getOnBackPressedDispatcher().onBackPressed();

                                            } else {
                                                Toast.makeText(LoginActivity.this, "Error fetching user information", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, "Error fetching user information", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    loginViewModel.setLoginError(getResources().getString(R.string.email_or_password_incorrect_error));
                                    binding.loginEmail.setBackgroundResource(R.drawable.item_background_light_gray_error);
                                    binding.loginPassword.setBackgroundResource(R.drawable.item_background_light_gray_error);
                                }
                            }
                        });


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check if current user exists
        if (mAuth.getCurrentUser() != null){
            // finish activity if current user exists
            // They are not supposed to be able to log in
            // This also exists the activity after returning to this page from registeration
            finish();
        }

    }
}