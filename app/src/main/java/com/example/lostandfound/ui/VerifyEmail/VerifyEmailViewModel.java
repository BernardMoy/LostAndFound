package com.example.lostandfound.ui.VerifyEmail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VerifyEmailViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> verificationError = new MutableLiveData<>("");

    public MutableLiveData<String> getVerificationError(){
        return this.verificationError;
    }

    public void setVerificationError(String s){
        this.verificationError.setValue(s);
    }
}