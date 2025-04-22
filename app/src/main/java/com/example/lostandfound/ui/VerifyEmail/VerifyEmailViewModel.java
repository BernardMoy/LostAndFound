package com.example.lostandfound.ui.VerifyEmail;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lostandfound.Data.DevData;
import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager;
import com.example.lostandfound.FirebaseManagers.FirestoreManager;
import com.example.lostandfound.Utility.EmailSender;
import com.example.lostandfound.Utility.ErrorCallback;
import com.example.lostandfound.FirebaseManagers.VerificationCodeManager;

public class VerifyEmailViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> verificationError = new MutableLiveData<>("");

    public MutableLiveData<String> getVerificationError() {
        return this.verificationError;
    }

    public void setVerificationError(String s) {
        this.verificationError.setValue(s);
    }


    // method to send an email containing verification code to the user, if the user has not generated another code last min
    public void sendVerificationEmail(Context ctx, String emailAddress, boolean hasToastMessage) {
        // dont send email if the email is DEV EMAIL
        if (DevData.DEV_EMAILS.contains(emailAddress)) {
            return;
        }

        // generate a new verification code
        VerificationCodeManager verificationCodeManager = new VerificationCodeManager(emailAddress, new FirestoreManager());
        verificationCodeManager.generateNewVerificationCode(new VerificationCodeManager.CodeGenerationCallback() {
            @Override
            public void onCodeGenerated(String error, String code) {
                // exit and set error if there is one
                if (!error.isEmpty()) {
                    setVerificationError(error);
                    return;
                }

                // Create email sender and send email
                String subject = "Lost and Found app - Please confirm your email";
                String body = code + " is your verification code.";
                EmailSender emailSender = new EmailSender(ctx, emailAddress);
                emailSender.sendEmail(subject, body, hasToastMessage, new EmailSender.EmailCallback() {
                    @Override
                    public void onComplete(Boolean success) {

                    }
                });
            }
        });
    }

    // method to validate user's entered code
    public void validateVerificationCode(Context ctx, String emailAddress, String givenCode, ErrorCallback callback) {
        /*
        Special permission is given to the user testDevHolo@warwick.ac.uk.
         */
        if (DevData.DEV_EMAILS.contains(emailAddress)) {
            callback.onComplete("");
        }

        // validate the given code
        VerificationCodeManager verificationCodeManager = new VerificationCodeManager(emailAddress, new FirestoreManager());
        verificationCodeManager.validateVerificationCode(givenCode, new ErrorCallback() {
            @Override
            public void onComplete(String error) {
                if (!error.isEmpty()) {
                    setVerificationError(error);
                    callback.onComplete(error);
                    return;
                }

                callback.onComplete("");
            }
        });
    }

    // method to create user
    public void createUser(Context ctx, String firstName, String lastName, String emailAddress, String password, ErrorCallback callback) {
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager(ctx);

        // initially, the avatar is null (Empty string)
        firebaseAuthManager.createUser(firstName, lastName, emailAddress, password, "", new ErrorCallback() {
            @Override
            public void onComplete(String error) {
                if (!error.isEmpty()) {
                    setVerificationError(error);
                    callback.onComplete(error);
                    return;
                }

                callback.onComplete("");
            }
        });
    }

}