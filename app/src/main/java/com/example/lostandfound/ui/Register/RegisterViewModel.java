package com.example.lostandfound.ui.Register;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> registerError = new MutableLiveData<>("");

    public MutableLiveData<String> getRegisterError() {
        return this.registerError;
    }

    public void setRegisterError(String s) {
        this.registerError.setValue(s);
    }


    // methods to validate whether the first name, last name, email and passwords are valid
    public boolean validateFirstName(String firstName) {
        if (firstName.trim().isEmpty()) {
            setRegisterError("First name cannot be empty");
            return false;
        }
        return true;
    }

    // validate last name
    public boolean validateLastName(String lastName) {
        if (lastName.trim().isEmpty()) {
            setRegisterError("Last name cannot be empty");
            return false;
        }
        return true;
    }

    // validate email
    public boolean validateEmail(String email) {
        if (email.trim().isEmpty()) {
            setRegisterError("Email cannot be empty");
            return false;

        } else if (!email.contains("@") || !email.endsWith("warwick.ac.uk")) {
            setRegisterError("Please Register with your university email (@warwick.ac.uk)");
            return false;
        }
        return true;
    }

    // validate password
    public boolean validatePassword(String password, String confirmPassword) {
        if (password.trim().isEmpty()) {
            setRegisterError("Password cannot be empty");
            return false;

        } else if (password.length() < 8) {
            setRegisterError("Password must be at least 8 characters long");
            return false;

        } else if (password.toLowerCase().equals(password) || password.toUpperCase().equals(password)) {
            // password is all uppercase or all lowercase
            setRegisterError("Password must have at least one uppercase and lowercase character");
            return false;

        } else if (!password.matches(".*\\d.*")) {
            // password does not have number
            setRegisterError("Password must have at least one numerical character");
            return false;

        } else if (password.matches("[a-zA-Z0-9 ]*")) {
            // if password matches that regex, password does not have special character
            setRegisterError("Password must have at least one special character");
            return false;

        } else if (!password.equals(confirmPassword)) {
            setRegisterError("Confirm password does not match");
            return false;
        }

        return true;
    }

}