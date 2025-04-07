package com.example.lostandfound.ui.Login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager;
import com.example.lostandfound.FirebaseManagers.FCMTokenManager;
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
}