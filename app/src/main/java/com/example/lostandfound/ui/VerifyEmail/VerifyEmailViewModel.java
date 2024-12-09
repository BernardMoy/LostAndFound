package com.example.lostandfound.ui.VerifyEmail;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lostandfound.CodeGenerationCallback;
import com.example.lostandfound.CodeVerificationCallback;
import com.example.lostandfound.EmailSender;
import com.example.lostandfound.FirebaseAuthManager;
import com.example.lostandfound.UserCreationCallback;
import com.example.lostandfound.VerificationCodeManager;

public class VerifyEmailViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> verificationError = new MutableLiveData<>("");

    public MutableLiveData<String> getVerificationError(){
        return this.verificationError;
    }

    public void setVerificationError(String s){
        this.verificationError.setValue(s);
    }


    // method to send an email containing verification code to the user, if the user has not generated another code last min
    public void sendVerificationEmail(Context ctx, String emailAddress, boolean hasToastMessage){
        // generate a new verification code
        VerificationCodeManager verificationCodeManager = new VerificationCodeManager(emailAddress);
        verificationCodeManager.generateNewVerificationCode(new CodeGenerationCallback() {
            @Override
            public void onCodeGenerated(String error, String code) {
                // exit and set error if there is one
                if (!error.isEmpty()){
                    setVerificationError(error);
                    return;
                }

                // Create email sender and send email
                String subject = "Lost and Found app - Please confirm your email";
                String body = code + " is your verification code.";
                EmailSender emailSender = new EmailSender(ctx, emailAddress);
                emailSender.sendEmail(subject, body, hasToastMessage);
            }
        });
    }

    // method to validate user's entered code
    public void validateVerificationCode(Context ctx, String emailAddress, String givenCode, CodeVerificationCallback callback){
        // validate the given code
        VerificationCodeManager verificationCodeManager = new VerificationCodeManager(emailAddress);
        verificationCodeManager.validateVerificationCode(givenCode, new CodeVerificationCallback() {
            @Override
            public void onCodeVerified(String error) {
                if (!error.isEmpty()){
                    setVerificationError(error);
                    callback.onCodeVerified(error);
                    return;
                }

                callback.onCodeVerified("");
            }
        });
    }

    // method to create user
    public void createUser(Context ctx, String firstName, String lastName, String emailAddress, String password, UserCreationCallback callback){
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager(ctx);
        firebaseAuthManager.createUser(firstName, lastName, emailAddress, password, new UserCreationCallback() {
            @Override
            public void onUserCreated(String error) {
                if (!error.isEmpty()){
                    setVerificationError(error);
                    callback.onUserCreated(error);
                    return;
                }

                callback.onUserCreated("");
            }
        });
    }

}