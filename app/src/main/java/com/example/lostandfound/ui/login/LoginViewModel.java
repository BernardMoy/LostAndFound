package com.example.lostandfound.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    // store the textview used to display error
    private final MutableLiveData<String> loginError = new MutableLiveData<>();

    public MutableLiveData<String> getLoginError(){
        return this.loginError;
    }

    public void setLoginError(String s){
        this.loginError.setValue(s);
    }

}