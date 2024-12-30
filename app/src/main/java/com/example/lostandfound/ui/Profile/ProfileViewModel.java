package com.example.lostandfound.ui.Profile;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lostandfound.FirebaseManagers.FirebaseAuthManager;
import com.example.lostandfound.FirebaseManagers.FirebaseUtility;

public class ProfileViewModel extends ViewModel {

    /*
    Link the textview to the count that is stored in the viewmodel.

    Whenever that count gets updated, an observer is set up to also change
    the displayed value (the binding object).

    The observer is set in the Activity file.
     */
    private final MutableLiveData<Integer> count = new MutableLiveData<>(0);   // set the initial value here

    public LiveData<Integer> getCount(){
        return count;
    }

    public void updateCount(int newCount){
        count.setValue(newCount);
    }


    // method to validate if user is already logged in
    public boolean isUserSignedIn(Context ctx){
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager(ctx);
        return FirebaseUtility.isUserLoggedIn();
    }

    // method to logout user
    public void logoutUser(Context ctx){
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager(ctx);
        firebaseAuthManager.logoutUser();
    }

}
