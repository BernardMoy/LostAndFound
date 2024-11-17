package com.example.lostandfound;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// class to hash an String with SHA256
public class Hasher {

    // hash a string using SHA-256, and return its hash.
    public static String hash(String text){
        return text;
    }

    // hash the text and compare with the targetHash
    public static boolean compareHash(String text, String targetHash){
        return false;
    }
}