package com.example.lostandfound.ui.Login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager;
import com.example.lostandfound.FirebaseManagers.FirebaseMessagingManager;
import com.example.lostandfound.FirebaseManagers.FirebaseUtility;
import com.example.lostandfound.Utility.ErrorCallback;

public class LoginViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> loginError = new MutableLiveData<>("");

    public MutableLiveData<String> getLoginError() {
        return this.loginError;
    }

    public void setLoginError(String s) {
        this.loginError.setValue(s);
    }

    // validate email
    public boolean validateEmail(String email) {
        if (email.trim().isEmpty()) {
            setLoginError("Email cannot be empty");
            return false;

        }
        return true;
    }

    // validate password
    public boolean validatePassword(String password) {
        if (password.trim().isEmpty()) {
            setLoginError("Password cannot be empty");
            return false;

        }
        return true;
    }


    // method to validate if user is already logged in
    public boolean isUserSignedIn(Context ctx) {
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager(ctx);
        return FirebaseUtility.isUserLoggedIn();
    }

    // method to log in user
    public void loginUser(Context ctx, String emailAddress, String password, ErrorCallback callback) {
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager(ctx);
        firebaseAuthManager.loginUser(emailAddress, password, new ErrorCallback() {
            @Override
            public void onComplete(String error) {
                if (!error.trim().isEmpty()) {
                    setLoginError(error);
                    callback.onComplete(error);
                    return;
                }

                FirebaseMessagingManager.INSTANCE.updateFCMToken(FirebaseUtility.getUserID(), new FirebaseMessagingManager.FCMTokenUpdateCallback() {
                    @Override
                    public void onComplete(boolean success) {
                        if (!success){
                            setLoginError("Failed generating an FCM token");
                            callback.onComplete(error);
                            return;
                        }

                        // no errors
                        callback.onComplete("");
                    }
                });
            }
        });
    }
}