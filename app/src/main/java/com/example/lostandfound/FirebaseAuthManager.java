package com.example.lostandfound;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthManager {
    private Context ctx;             // ctx required for accessing shared preferences
    private FirebaseAuth mAuth;      // stores emails and passwords
    private FirestoreManager db;     // stores emails to Userdata(firstName and lastName)

    public FirebaseAuthManager(Context ctx){
        this.ctx = ctx;
        mAuth = FirebaseAuth.getInstance();
        db = new FirestoreManager();
    }

    // method to create user with email and password
    public void createUser(String firstName, String lastName, String email, String password, UserCreationCallback callback){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // add the firstname and lastname data to database where email is the id
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirestoreNames.USERS_FIRSTNAME, firstName);
                    data.put(FirestoreNames.USERS_LASTNAME, lastName);

                    // add the data to firestore db
                    db.put("users", email, data, new FirestoreManager.Callback<Boolean>() {
                        @Override
                        public void onComplete(Boolean result) {
                            if (!result){
                                callback.onUserCreated("Error adding user to the database");
                                return;
                            }

                            // Save the extra user credentials (First, last name, email, avatar) in sharedpreferences
                            SharedPreferences sharedPreferences = ctx.getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SharedPreferencesNames.USER_FIRSTNAME, firstName);
                            editor.putString(SharedPreferencesNames.USER_LASTNAME, lastName);
                            editor.putString(SharedPreferencesNames.USER_EMAIL, email);
                            editor.apply();

                            // task successful code executes here
                            callback.onUserCreated("");
                        }
                    });

                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    // check if this is due to an account with same email already exists
                    callback.onUserCreated("An account with the same email already exists, please log in.");

                } else {
                    // failed due to other reasons
                    callback.onUserCreated("Account creation failed");
                }
            }
        });
    }


    // method to login with user credentials
    public void loginUser(String emailAddress, String password, UserLoginCallback callback){
        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // sign in successful
                    // get other parameters given the user email
                    db.get("users", emailAddress, new FirestoreManager.Callback<Map<String, Object>>() {
                        @Override
                        public void onComplete(Map<String, Object> result) {
                            if (result == null){
                                callback.onUserSignedIn("User not found in the database");
                                return;
                            }
                            String firstName = (String) result.get(FirestoreNames.USERS_FIRSTNAME);
                            String lastName = (String) result.get(FirestoreNames.USERS_LASTNAME);

                            // save the extra user credentials into shared preferences
                            SharedPreferences sharedPreferences = ctx.getSharedPreferences(SharedPreferencesNames.NAME_USERS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SharedPreferencesNames.USER_FIRSTNAME, firstName);
                            editor.putString(SharedPreferencesNames.USER_LASTNAME, lastName);
                            editor.putString(SharedPreferencesNames.USER_EMAIL, emailAddress);
                            editor.apply();

                            // exit with no errors
                            callback.onUserSignedIn("");
                        }
                    });

                } else {
                    // sign in with the given credentials fails
                    callback.onUserSignedIn("The provided user credentials are incorrect");
                }
            }
        });
    }

    // method to logout
    public void logoutUser(){
        mAuth.signOut();

        // reset shared preferences for User
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // clear user data
        editor.clear();
        editor.apply();
    }


    // method to check if user is already logged in
    public boolean isUserLoggedIn(){
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }
}
