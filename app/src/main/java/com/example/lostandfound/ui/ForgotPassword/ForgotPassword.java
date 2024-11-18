package com.example.lostandfound.ui.ForgotPassword;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityForgotPasswordBinding;

public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel forgotPasswordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up confirm email view model
        forgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        // inflate binding
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}