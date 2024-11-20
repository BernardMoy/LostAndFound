package com.example.lostandfound;

public interface CodeGenerationCallback {
    // if there are errors, code is empty
    // if there are no errors, error is empty
    void onCodeGenerated(String error, String code);
}
