package com.example.lostandfound.ui.register;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> registerError = new MutableLiveData<>();

    public MutableLiveData<String> getRegisterError(){
        return this.registerError;
    }

    public void setRegisterError(String s){
        this.registerError.setValue(s);
    }
}