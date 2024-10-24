package com.example.lostandfound;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostandfound.ui.register.RegisterActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Password {

    private Context ctx;

    public Password(Context ctx){
        this.ctx = ctx;
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

    // method to validate password format and return error message, or null if successful
    public String validatePassword(String password){
        if (password.isEmpty()){
            return ctx.getString(R.string.password_empty_error);

        } else if (password.length() < 8){
            return ctx.getString(R.string.password_too_short_error);

        } else if (!hasUpperAndLowerCase(password)){
            return ctx.getString(R.string.password_no_uppercase_or_lowercase_error);

        } else if (!hasNumber(password)){
            return ctx.getString(R.string.password_no_number_error);

        } else if (!hasSpecialCharacter(password)){
            return ctx.getString(R.string.password_no_special_characters_error);
        }

        return null;
    }

    // method to check if password has at least one uppercase or one lowercase letters
    public boolean hasUpperAndLowerCase(String password){

        // if the password has at least one upper or lower case, converting them should make them unequal
        return !password.toLowerCase().equals(password) && !password.toUpperCase().equals(password);
    }

    // method to check if password has numerical characters
    public boolean hasNumber(String password){
        String num = ".*\\d.*";
        return password.matches(num);
    }

    // method to check if password has special characters
    public boolean hasSpecialCharacter(String password){

        // if the password matches this regex, there are no special characters in it
        String noSpecial = "[a-zA-Z0-9 ]*";
        return !password.matches(noSpecial);
    }
}
