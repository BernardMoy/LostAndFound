package com.example.lostandfound.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

}
