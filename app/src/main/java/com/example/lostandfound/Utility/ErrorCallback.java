package com.example.lostandfound.Utility;

/*
An error callback that returns an error message
used when there are multiple nested callbacks, and this can help identify the precise error
 */
public interface ErrorCallback {
    // a callback function that returns an error message, or empty string if no errors
    void onComplete(String error);
}
