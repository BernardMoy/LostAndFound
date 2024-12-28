package com.example.lostandfound.Utility;

public interface ErrorCallback {
    // a callback function that returns an error message, or empty string if no errors
    void onComplete(String error);
}
