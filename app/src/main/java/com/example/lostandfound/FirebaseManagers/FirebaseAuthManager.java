package com.example.lostandfound.FirebaseManagers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.Data.SharedPreferencesNames;
import com.example.lostandfound.Utility.ErrorCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.HashMap;
import java.util.Map;


/*
This class only stores methods that involves using the context
If not, use that in firestore manager!
 */
public class FirebaseAuthManager {
    private final Context ctx;             // ctx required for accessing shared preferences
    private final FirebaseAuth mAuth;      // stores emails and passwords
    private final FirestoreManager db;     // stores emails to Userdata(firstName and lastName)

    public interface LogoutCallback {
        void onComplete(boolean success);
    }

    public FirebaseAuthManager(Context ctx) {
        this.ctx = ctx;
        mAuth = FirebaseAuth.getInstance();
        db = new FirestoreManager();
    }

    // method to create user with email and password
    public void createUser(String firstName, String lastName, String email, String password, String avatar, ErrorCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // get the user id
                    String userID = mAuth.getUid();

                    // add the firstname and lastname data to database where email is the id
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseNames.USERS_EMAIL, email);
                    data.put(FirebaseNames.USERS_FIRSTNAME, firstName);
                    data.put(FirebaseNames.USERS_LASTNAME, lastName);
                    data.put(FirebaseNames.USERS_AVATAR, avatar);
                    data.put(FirebaseNames.USERS_ITEM_NOTIFICATION_ENABLED, true);
                    data.put(FirebaseNames.USERS_MESSAGE_NOTIFICATION_ENABLED, true);
                    data.put(FirebaseNames.USERS_IS_ADMIN, false); // not admins by default

                    // add the data to firestore db
                    db.put(FirebaseNames.COLLECTION_USERS, userID, data, new FirestoreManager.Callback<Boolean>() {
                        @Override
                        public void onComplete(Boolean result) {
                            if (!result) {
                                callback.onComplete("Error adding user to the database");
                                return;
                            }

                            // Save the extra user credentials (First, last name, email, avatar) in sharedpreferences
                            SharedPreferences sharedPreferences = ctx.getSharedPreferences(SharedPreferencesNames.NAME_USERS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SharedPreferencesNames.USER_FIRSTNAME, firstName);
                            editor.putString(SharedPreferencesNames.USER_LASTNAME, lastName);
                            editor.putString(SharedPreferencesNames.USER_EMAIL, email);
                            editor.putString(SharedPreferencesNames.USER_AVATAR, avatar);
                            editor.apply();

                            // update the FCM token
                            FCMTokenManager.INSTANCE.updateFCMToken(userID, new FCMTokenManager.FCMTokenUpdateCallback() {
                                @Override
                                public void onComplete(boolean success) {
                                    if (!success) {
                                        callback.onComplete("Failed generating FCM token");
                                        return;
                                    }

                                    // task successful code executes here
                                    callback.onComplete("");
                                }
                            });
                        }
                    });

                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    // check if this is due to an account with same email already exists
                    callback.onComplete("An account with the same email already exists, please log in.");

                } else {
                    // failed due to other reasons
                    callback.onComplete("Account creation failed");
                }
            }
        });
    }


    // method to login with user credentials
    public void loginUser(String emailAddress, String password, ErrorCallback callback) {
        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // sign in successful

                    // get other parameters given the user ID
                    String userID = mAuth.getUid();

                    db.get(FirebaseNames.COLLECTION_USERS, userID, new FirestoreManager.Callback<Map<String, Object>>() {
                        @Override
                        public void onComplete(Map<String, Object> result) {
                            if (result == null) {
                                callback.onComplete("User not found in the database");
                                return;
                            }
                            String email = (String) result.get(FirebaseNames.USERS_EMAIL);
                            String firstName = (String) result.get(FirebaseNames.USERS_FIRSTNAME);
                            String lastName = (String) result.get(FirebaseNames.USERS_LASTNAME);
                            String avatar = (String) result.get(FirebaseNames.USERS_AVATAR);

                            // save the extra user credentials into shared preferences
                            SharedPreferences sharedPreferences = ctx.getSharedPreferences(SharedPreferencesNames.NAME_USERS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SharedPreferencesNames.USER_FIRSTNAME, firstName);
                            editor.putString(SharedPreferencesNames.USER_LASTNAME, lastName);
                            editor.putString(SharedPreferencesNames.USER_EMAIL, email);
                            editor.putString(SharedPreferencesNames.USER_AVATAR, avatar);
                            editor.apply();

                            // exit with no errors
                            callback.onComplete("");
                        }
                    });

                } else {
                    // sign in with the given credentials fails
                    callback.onComplete("The provided user credentials are incorrect");
                }
            }
        });
    }

    // method to update user credentials
    public void updateUser(String emailAddress, String newFirstName, String newLastName, String avatar, ErrorCallback callback) {
        // get uid
        String userID = mAuth.getUid();
        // if uid is null, return false
        if (userID == null) {
            callback.onComplete("You are not logged in.");
            return;
        }

        // add the firstname and lastname data to database where email is the id
        Map<String, Object> data = new HashMap<>();
        data.put(FirebaseNames.USERS_EMAIL, emailAddress);
        data.put(FirebaseNames.USERS_FIRSTNAME, newFirstName);
        data.put(FirebaseNames.USERS_LASTNAME, newLastName);
        data.put(FirebaseNames.USERS_AVATAR, avatar);

        db.put(FirebaseNames.COLLECTION_USERS, userID, data, new FirestoreManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                if (!result) {
                    callback.onComplete("Error updating user information");
                    return;
                }

                // Save the new user credentials (First, last name, email, avatar) in sharedpreferences
                SharedPreferences sharedPreferences = ctx.getSharedPreferences(SharedPreferencesNames.NAME_USERS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // no need to put email as it cant be updated
                editor.putString(SharedPreferencesNames.USER_FIRSTNAME, newFirstName);
                editor.putString(SharedPreferencesNames.USER_LASTNAME, newLastName);
                editor.putString(SharedPreferencesNames.USER_AVATAR, avatar);
                editor.apply();

                // task successful code executes here
                callback.onComplete("");
            }
        });
    }

    // method to update a user's password given email
    public void sendPasswordResetEmail(String emailAddress, ErrorCallback callback) {
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onComplete("");
                        } else {
                            callback.onComplete("Failed sending password reset email");
                        }
                    }
                });
    }

    // method to logout
    public void logoutUser(LogoutCallback callback) {
        // clear the FCM token for the user so that the user no longer receive push notifications
        FCMTokenManager.INSTANCE.removeFCMTokenFromUser(UserManager.getUserID(), new FCMTokenManager.FCMTokenDeleteCallback() {
            @Override
            public void onComplete(boolean success) {
                if (!success) {
                    callback.onComplete(false);
                    return;
                }

                // sign out the user here
                mAuth.signOut();

                // reset shared preferences for User
                SharedPreferences sharedPreferences = ctx.getSharedPreferences(SharedPreferencesNames.NAME_USERS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // clear user data
                editor.clear();
                editor.apply();

                // return result
                callback.onComplete(true);
            }
        });
    }
}
