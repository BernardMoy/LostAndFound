package com.example.lostandfound.ui.VerifyEmail;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lostandfound.MainActivity;
import com.example.lostandfound.R;
import com.example.lostandfound.Utility.ErrorCallback;
import com.example.lostandfound.Utility.FontSizeManager;
import com.example.lostandfound.databinding.ActivityVerifyEmailBinding;

public class VerifyEmailActivity extends AppCompatActivity {

    private ActivityVerifyEmailBinding binding;
    private VerifyEmailViewModel verifyEmailViewModel;
    private Intent intent;
    private String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // set up verify email view model
        verifyEmailViewModel = new ViewModelProvider(this).get(VerifyEmailViewModel.class);

        // inflate binding
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // back button
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // set font size for this XML activity
        ViewGroup parentView = binding.main;
        FontSizeManager.INSTANCE.setFontSizeXML(parentView, VerifyEmailActivity.this);

        // set listeners to move focus when an edittext is filled
        setTextFocusChanger(binding.code1, binding.code2);
        setTextFocusChanger(binding.code2, binding.code3);
        setTextFocusChanger(binding.code3, binding.code4);
        setTextFocusChanger(binding.code4, binding.code5);
        setTextFocusChanger(binding.code5, binding.code6);

        // set up viewmodel observer fo verification error
        verifyEmailViewModel.getVerificationError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()) {
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
        if (!intent.hasExtra("email")
                || !intent.hasExtra("first_name")
                || !intent.hasExtra("first_name")
                || !intent.hasExtra("first_name")) {
            Toast.makeText(VerifyEmailActivity.this, "Missing required user parameters", Toast.LENGTH_SHORT).show();
            finish();
        }
        emailAddress = intent.getStringExtra("email");
        binding.recipientEmailAddress.setText(intent.getStringExtra("email"));

        // select the first edittext
        binding.code1.requestFocus();

        // set underlined text for the resend text
        binding.resend.setPaintFlags(binding.resend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // textview to resend email
        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyEmailViewModel.sendVerificationEmail(VerifyEmailActivity.this, emailAddress, true);

            }
        });

        // method to verify the code when the verify button is pressed
        binding.verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set progress bar to be visible
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.verifyEmailButton.setEnabled(false);

                // extract the string
                String code = "000000";
                StringBuilder builder = new StringBuilder(code);
                if (binding.code1.getText().length() > 0) {
                    builder.setCharAt(0, binding.code1.getText().charAt(0));
                }
                if (binding.code2.getText().length() > 0) {
                    builder.setCharAt(1, binding.code2.getText().charAt(0));
                }
                if (binding.code3.getText().length() > 0) {
                    builder.setCharAt(2, binding.code3.getText().charAt(0));
                }
                if (binding.code4.getText().length() > 0) {
                    builder.setCharAt(3, binding.code4.getText().charAt(0));
                }
                if (binding.code5.getText().length() > 0) {
                    builder.setCharAt(4, binding.code5.getText().charAt(0));
                }
                if (binding.code6.getText().length() > 0) {
                    builder.setCharAt(5, binding.code6.getText().charAt(0));
                }

                code = builder.toString();

                // reset verification error
                verifyEmailViewModel.setVerificationError("");

                // verify the code and create user if successful
                verifyEmailViewModel.validateVerificationCode(VerifyEmailActivity.this, emailAddress, code, new ErrorCallback() {
                    @Override
                    public void onComplete(String error) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.verifyEmailButton.setEnabled(true);

                        if (!error.isEmpty()) {
                            return;
                        }

                        // create user if no errors
                        verifyEmailViewModel.createUser(VerifyEmailActivity.this,
                                intent.getStringExtra("first_name"),
                                intent.getStringExtra("last_name"),
                                emailAddress,
                                intent.getStringExtra("password"),
                                new ErrorCallback() {
                                    @Override
                                    public void onComplete(String error) {

                                        // hide progress bar
                                        binding.progressBar.setVisibility(View.GONE);

                                        // if there is an error, exit function
                                        if (!error.isEmpty()) {
                                            return;
                                        }

                                        // If no error, display message and exit
                                        Toast.makeText(VerifyEmailActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();


                                        // force redirect to the home page
                                        Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);   // bitwise or

                                        // start main activity
                                        startActivity(intent);

                                        // finish current activity
                                        finish();
                                    }
                                });
                    }

                });
            }
        });

        // send email whenever this activity starts
        verifyEmailViewModel.sendVerificationEmail(VerifyEmailActivity.this, emailAddress, false);
    }

    // this method causes whenever edittext1 is filled with text, the focus is changed to edittext2
    public void setTextFocusChanger(EditText editText1, EditText editText2) {
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
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
                if (charSequence.length() == 0) {
                    // if no text, set focus to edittext1
                    editText1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}