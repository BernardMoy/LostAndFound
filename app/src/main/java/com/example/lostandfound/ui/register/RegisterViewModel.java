package com.example.lostandfound.ui.register;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    // store the first name, last name, email and password error message
    private MutableLiveData<String> firstNameError = new MutableLiveData<>();
    private MutableLiveData<String> lastNameError = new MutableLiveData<>();
    private MutableLiveData<String> emailError = new MutableLiveData<>();
    private MutableLiveData<String> passwordError = new MutableLiveData<>();

    public MutableLiveData<String> getFirstNameError() {
        return firstNameError;
    }

    public MutableLiveData<String> getLastNameError() {
        return lastNameError;
    }

    public MutableLiveData<String> getEmailError() {
        return emailError;
    }

    public MutableLiveData<String> getPasswordError() {
        return passwordError;
    }

    public void setFirstNameError(String error){
        this.firstNameError.setValue(error);
    }

    public void setLastNameError(String error){
        this.lastNameError.setValue(error);
    }

    public void setEmailError(String error){
        this.emailError.setValue(error);
    }

    public void setPasswordError(String error){
        this.passwordError.setValue(error);
    }
}