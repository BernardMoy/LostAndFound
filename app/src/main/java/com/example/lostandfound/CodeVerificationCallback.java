package com.example.lostandfound;

public interface CodeVerificationCallback {
    // if there are no errors, code is empty
    void onCodeVerified(String error);
}
