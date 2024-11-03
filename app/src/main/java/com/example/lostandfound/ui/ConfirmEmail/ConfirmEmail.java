package com.example.lostandfound.ui.ConfirmEmail;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.EmailSender;
import com.example.lostandfound.FirestoreManager;
import com.example.lostandfound.Hasher;
import com.example.lostandfound.R;
import com.example.lostandfound.VerificationData;
import com.example.lostandfound.databinding.ActivityConfirmEmailBinding;
import com.example.lostandfound.databinding.ActivityProfileBinding;
import com.example.lostandfound.ui.profile.ProfileViewModel;

import java.util.Calendar;
import java.util.Map;

public class ConfirmEmail extends AppCompatActivity {

    private ActivityConfirmEmailBinding binding;
    private ConfirmEmailViewModel confirmEmailViewModel;

    private FirestoreManager db;
    private String storedHashedCode = "";
    private long storedTimeStamp = 0;

    // time that the verification code is valid
    private final long VALID_TIME = 600000;   // 10 minutes

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up confirm email view model
        confirmEmailViewModel = new ViewModelProvider(this).get(ConfirmEmailViewModel.class);

        // inflate binding
        binding = ActivityConfirmEmailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // set up db
        db = new FirestoreManager(ConfirmEmail.this);

        // set listeners to move focus when an edittext is filled
        setTextFocusChanger(binding.code1, binding.code2);
        setTextFocusChanger(binding.code2, binding.code3);
        setTextFocusChanger(binding.code3, binding.code4);
        setTextFocusChanger(binding.code4, binding.code5);
        setTextFocusChanger(binding.code5, binding.code6);

        // set up viewmodel observer fo verification error
        confirmEmailViewModel.getVerificationError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()){
                    // set the view to be gone
                    binding.verificationError.setVisibility(View.GONE);

                } else {
                    // display the error
                    binding.verificationError.setText(s);
                    binding.verificationError.setVisibility(View.VISIBLE);
                }
            }
        });

        // get the passed email and modify the displayed field
        intent = getIntent();
        if (intent.hasExtra("email")){
            binding.recipientEmailAddress.setText(intent.getStringExtra("email"));
        }


        // select the first edittext
        binding.code1.requestFocus();

        // back button
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        // button to verify email
        binding.confirmEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        // set underlined text for the resend text
        binding.resend.setPaintFlags(binding.resend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // make the resend text send email
        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(true);

            }
        });


        // method to verify the code when the verify button is pressed
        binding.confirmEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // extract the string
                String code = "000000";
                StringBuilder builder = new StringBuilder(code);
                if (binding.code1.getText().length() > 0){
                    builder.setCharAt(0, binding.code1.getText().charAt(0));
                }
                if (binding.code2.getText().length() > 0){
                    builder.setCharAt(1, binding.code2.getText().charAt(0));
                }
                if (binding.code3.getText().length() > 0){
                    builder.setCharAt(2, binding.code3.getText().charAt(0));
                }
                if (binding.code4.getText().length() > 0){
                    builder.setCharAt(3, binding.code4.getText().charAt(0));
                }
                if (binding.code5.getText().length() > 0){
                    builder.setCharAt(4, binding.code5.getText().charAt(0));
                }
                if (binding.code6.getText().length() > 0){
                    builder.setCharAt(5, binding.code6.getText().charAt(0));
                }

                code = builder.toString();

                // reset verification error
                confirmEmailViewModel.setVerificationError("");

                // verify the code and create user if successful
                verifyCodeAndCreateUser(code);
            }
        });


        // send email whenever this activity starts
        sendEmail(false);
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

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0){
                    // if no text, set focus to edittext1
                    editText1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // method to send email to the email in the passed indent
    private void sendEmail(boolean isRegenerated){
        if (!intent.hasExtra("email")){
            Toast.makeText(ConfirmEmail.this, "Email address not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = intent.getStringExtra("email");
        EmailSender emailSender = new EmailSender(ConfirmEmail.this, email);

        // send email
        emailSender.sendEmail(isRegenerated);
    }


    // method to verify if a code is valid, and it is only valid if it is generated within last 10 minutes.
    // if a code is valid, it will create the user
    public void verifyCodeAndCreateUser(String code){

        // get the passed email
        if (!intent.hasExtra("email")) {
            confirmEmailViewModel.setVerificationError("The email address is not valid.");
            return;
        }
        String emailAddress = intent.getStringExtra("email");

        db.getValue("user_verifications", emailAddress, new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                if (result == null){
                    return;
                }

                storedHashedCode = (String) result.get("code");
                storedTimeStamp = (long) result.get("timestamp");

                // check if timestamp is within last 10 mins
                long currentTimeStamp = Calendar.getInstance().getTimeInMillis();

                if (currentTimeStamp - storedTimeStamp > VALID_TIME) {
                    Toast.makeText(ConfirmEmail.this, "This code has expired, please generate another one", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if user's code is valid
                Hasher hasher = new Hasher();
                if (!hasher.compareHash(code, storedHashedCode)) {
                    confirmEmailViewModel.setVerificationError("The code entered was invalid.");
                    return;
                }

                // create user
                createUser();
            }
        });
    }

    // method to create the user, add to Firebase auth and db, and exit activity
    public void createUser(){

    }
}