package com.example.lostandfound.FirebaseManagers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lostandfound.Data.DevData;
import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.Utility.DateTimeManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * For firebase functions that does not require any input parameters.
 */
public class UserManager {
    private UserManager() {
    }

    public interface IsAdminCallback {
        void onComplete(boolean result);
    }

    public interface UpdateTimeCallback {
        void onComplete(boolean success);
    }

    public interface CheckIfClaimedCallback {
        void onComplete(boolean result);
    }

    // get the current user's UID, or empty string if user is not logged in
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
    public static void isUserAdmin(IsAdminCallback callback) {
        FirestoreManager manager = new FirestoreManager();
        manager.get(FirebaseNames.COLLECTION_USERS, UserManager.getUserID(), new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                if (result == null || result.get(FirebaseNames.USERS_IS_ADMIN) == null) {
                    callback.onComplete(false);
                    return;
                }

                boolean isAdmin = (boolean) result.get(FirebaseNames.USERS_IS_ADMIN);
                callback.onComplete(isAdmin);
            }
        });
    }

    // update the claim timestamp to the current time
    public static void updateClaimTimestamp(UpdateTimeCallback callback) {
        Map<String, Long> data = new HashMap<>();
        data.put(FirebaseNames.USERS_LAST_CLAIMED_TIMESTAMP, DateTimeManager.getCurrentEpochTime());

        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
                .document(UserManager.getUserID())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase error", e.getMessage());
                        callback.onComplete(false);
                    }
                });
    }


    // check if the claim timestamp is in the last three days
    public static void checkIfUserClaimedInLastThreeDays(CheckIfClaimedCallback callback) {
        FirebaseFirestore.getInstance().collection(FirebaseNames.COLLECTION_USERS)
                .document(UserManager.getUserID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.get(FirebaseNames.USERS_LAST_CLAIMED_TIMESTAMP) == null) {
                            callback.onComplete(false);
                        } else {
                            boolean result = (DateTimeManager.getCurrentEpochTime() - (Long) documentSnapshot.get(FirebaseNames.USERS_LAST_CLAIMED_TIMESTAMP)) < 259200;
                            callback.onComplete(result);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase error", e.getMessage());
                        callback.onComplete(false);
                    }
                });
    }
}
