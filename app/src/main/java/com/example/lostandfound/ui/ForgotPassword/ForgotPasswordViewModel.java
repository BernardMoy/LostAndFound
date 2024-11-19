package com.example.lostandfound.ui.ForgotPassword;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForgotPasswordViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> forgotPasswordError = new MutableLiveData<>();

    public MutableLiveData<String> getForgotPasswordError(){
        return this.forgotPasswordError;
    }

    public void setForgotPasswordError(String s){
        this.forgotPasswordError.setValue(s);
    }


    // method to validate if email provided is empty
    public boolean validateEmail(String email){
        if (email.isEmpty()){
            forgotPasswordError.setValue("Email cannot be empty");
            return false;
        }
        return true;
    }
}