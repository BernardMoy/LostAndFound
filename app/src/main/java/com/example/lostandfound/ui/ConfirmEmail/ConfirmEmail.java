package com.example.lostandfound.ui.ConfirmEmail;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.EmailSender;
import com.example.lostandfound.R;
import com.example.lostandfound.databinding.ActivityConfirmEmailBinding;
import com.example.lostandfound.databinding.ActivityProfileBinding;
import com.example.lostandfound.ui.profile.ProfileViewModel;

public class ConfirmEmail extends AppCompatActivity {

    private ActivityConfirmEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up profile view model
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // inflate binding
        binding = ActivityConfirmEmailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // set listeners to move focus when an edittext is filled
        setTextFocusChanger(binding.code1, binding.code2);
        setTextFocusChanger(binding.code2, binding.code3);
        setTextFocusChanger(binding.code3, binding.code4);
        setTextFocusChanger(binding.code4, binding.code5);
        setTextFocusChanger(binding.code5, binding.code6);

        // select the first edittext
        binding.code1.requestFocus();

        // back button
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        // button to confirm email
        binding.confirmEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmailSender emailSender = new EmailSender("u2256784@live.warwick.ac.uk");
                emailSender.sendEmail(ConfirmEmail.this);
            }
        });
    }

    // this method causes whenever edittext1 is filled with text, the focus is changed to edittext2
    public void setTextFocusChanger(EditText editText1, EditText editText2){
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    // if have text, set focus to edittext2
                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}