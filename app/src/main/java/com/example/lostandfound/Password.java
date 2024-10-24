package com.example.lostandfound;

import android.widget.TextView;
import android.widget.Toast;

import com.example.lostandfound.ui.register.RegisterActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Password {


    public Password(){
    }

    // method to generate password hash
    public String generateHash(String password){
        String hashedPassword = "";
        // hash the password using SHA-256
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            hashedPassword = Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e){
        }

        return hashedPassword;
    }

    // method to validate if a password matches a hash
    public boolean validateHash(String password, String hashedPassword){
        return true;
    }

    // method to validate password and return error message, or null if successful
    public String validatePassword(String password){

        return null;
    }
}
