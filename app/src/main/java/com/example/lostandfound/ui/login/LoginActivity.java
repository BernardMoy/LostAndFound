package com.example.lostandfound.ui.login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityLoginBinding;
import com.example.lostandfound.databinding.ActivitySettingsBinding;
import com.example.lostandfound.ui.register.RegisterActivity;
import com.example.lostandfound.ui.register.RegisterViewModel;
import com.example.lostandfound.ui.settings.SettingsViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up view model
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

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

        // set underlined text for the register and forgot password textview
        binding.register.setPaintFlags(binding.register.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        binding.forgotPassword.setPaintFlags(binding.register.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        // start register activity when the register text is clicked
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}