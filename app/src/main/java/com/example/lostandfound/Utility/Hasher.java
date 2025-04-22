package com.example.lostandfound.Utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
Utility class to hash a string using sha 256
used to hash verification codes for registration
 */
public final class Hasher {

    private Hasher() {
    }

    // hash a string using SHA-256, and return its hash.
    public static String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            // convert hashed bytes to string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    // hash the text and compare with the targetHash
    public static boolean compareHash(String text, String targetHash) {
        return targetHash.equals(hash(text));
    }
}