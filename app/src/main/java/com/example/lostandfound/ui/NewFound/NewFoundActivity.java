package com.example.lostandfound.ui.NewFound;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityLogBinding;
import com.example.lostandfound.databinding.ActivityNewFoundBinding;
import com.example.lostandfound.ui.ActivityLog.ActivityLogViewModel;

public class NewFoundActivity extends AppCompatActivity {

    private ActivityNewFoundBinding binding;
    private NewFoundViewModel newFoundViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up view model
        newFoundViewModel = new ViewModelProvider(this).get(NewFoundViewModel.class);

        // inflate binding
        binding = ActivityNewFoundBinding.inflate(getLayoutInflater());
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
    }
}