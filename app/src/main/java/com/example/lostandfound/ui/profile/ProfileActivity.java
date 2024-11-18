package com.example.lostandfound.ui.profile;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.MainActivity;
import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityProfileBinding;
import com.example.lostandfound.databinding.DialogLogoutBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up profile view model
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // inflate binding
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
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

        // button to log out the user
        /*
        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateDisplayedData();
    }

    // method to log the user out
    public void logout(){
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dialog_background, null));
        dialog.setCancelable(true);

        // load the dialog binding
        DialogLogoutBinding dialogLogoutBinding = DialogLogoutBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.setContentView(dialogLogoutBinding.getRoot());

        dialogLogoutBinding.cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogLogoutBinding.loginDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                // log out user here
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                // reset sharedpreferences
                SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // clear user data
                editor.clear();
                editor.apply();


                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // finish activity
                finish();
            }
        });

        dialog.show();
    }


    // method to update user data displayed on screen
    public void updateDisplayedData(){
        // get data from sharedpref
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String firstName = sharedPreferences.getString("first_name", "");
        String lastName = sharedPreferences.getString("last_name", "");

        String displayedName = firstName + ' ' + lastName;

        // update the displayed names and emails
        binding.profileDisplayName.setText(displayedName);
        binding.profileEmail.setText(email);
    }
}