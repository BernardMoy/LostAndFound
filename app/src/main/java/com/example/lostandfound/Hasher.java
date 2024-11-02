package com.example.lostandfound;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// class to hash an String with SHA256
public class Hasher {

    public Hasher(){
    }

    // hash a string number, and return its hash.
    public String hash(String text){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e){
            return "";
        }
    }

    // hash the text and compare with the targetHash
    public boolean compareHash(String text, String targetHash){
        String hashedText = hash(text);
        return targetHash.equals(hashedText);
    }
}
