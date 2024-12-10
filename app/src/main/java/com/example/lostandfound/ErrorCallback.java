package com.example.lostandfound;

public interface ErrorCallback {
    // a callback function that returns an error message, or empty string if no errors
    void onComplete(String error);
}
