package com.example.lostandfound.FirebaseManagers;

import com.example.lostandfound.Data.DevData;
import com.example.lostandfound.Data.FirebaseNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;


/**
 * For firebase functions that does not require any input parameters.
 */
public class FirebaseUtility {
    private FirebaseUtility() {
    }

    public interface isAdminCallback {
        void onComplete(boolean result);
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

    // check if user is dev user
    public static boolean isUserDev() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return false;
        }
        return DevData.DEV_EMAILS.contains(user.getEmail());
    }

    // check if user is admin user
    public static void isUserAdmin(isAdminCallback callback){
        FirestoreManager manager = new FirestoreManager();
        manager.get(FirebaseNames.COLLECTION_USERS, FirebaseUtility.getUserID(), new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                if (result == null || result.get(FirebaseNames.USERS_IS_ADMIN) == null){
                    callback.onComplete(false);
                    return;
                }

                boolean isAdmin = (boolean) result.get(FirebaseNames.USERS_IS_ADMIN);
                callback.onComplete(isAdmin);
            }
        });
    }

}
