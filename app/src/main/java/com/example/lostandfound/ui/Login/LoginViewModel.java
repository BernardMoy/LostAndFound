package com.example.lostandfound.ui.Login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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