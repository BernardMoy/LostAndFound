package com.example.lostandfound;

public interface UserLoginCallback {
    // empty string if no errors
    void onUserSignedIn(String error);
}
