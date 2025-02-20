package com.example.lostandfound.FirebaseManagers;

import com.example.lostandfound.Data.DevData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * For firebase functions that does not require any input parameters.
 */
public class FirebaseUtility {
    private FirebaseUtility() {
    }

    // get the current user's UID
    public static String getUserID() {
        // if logged in, get the current user's UID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return "";
        }

        return user.getUid();
    }

    // check if user is logged in
    public static boolean isUserLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    // check if user id dev user
    public static boolean isUserDev() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return false;
        }
        return DevData.DEV_EMAILS.contains(user.getEmail());
    }

}
