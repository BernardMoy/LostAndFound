package com.example.lostandfound.ui.Login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> loginError = new MutableLiveData<>("");

    public MutableLiveData<String> getLoginError(){
        return this.loginError;
    }

    public void setLoginError(String s){
        this.loginError.setValue(s);
    }

    // validate email
    public boolean validateEmail(String email){
        if (email.isEmpty()){
            setLoginError("Email cannot be empty");
            return false;

        } else if (!email.contains("@") || !email.endsWith("warwick.ac.uk")){
            setLoginError("Please Register with your university email (@warwick.ac.uk)");
            return false;
        }
        return true;
    }

    // validate password
    public boolean validatePassword(String password){
        if (password.isEmpty()){
            setLoginError("Password cannot be empty");
            return false;

        } else if (password.length() < 8){
            setLoginError("Password must be at least 8 characters long");
            return false;

        } else if (password.toLowerCase().equals(password) || password.toUpperCase().equals(password)){
            // password is all uppercase or all lowercase
            setLoginError("Password must have at least one uppercase and lowercase character");
            return false;

        } else if (!password.matches(".*\\d.*")){
            // password does not have number
            setLoginError("Password must have at least one numerical character");
            return false;

        } else if (password.matches("[a-zA-Z0-9 ]*")){
            // if password matches that regex, password does not have special character
            setLoginError("Password must have at least one special character");
            return false;
        }

        return true;
    }
}