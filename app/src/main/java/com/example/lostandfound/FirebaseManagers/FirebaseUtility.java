package com.example.lostandfound.FirebaseManagers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUtility {
    private FirebaseUtility(){}

    // get the current user's UID
    public static String getUserID(){
        // if logged in, get the current user's UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            return "";
        }

        return user.getUid();
    }

}
