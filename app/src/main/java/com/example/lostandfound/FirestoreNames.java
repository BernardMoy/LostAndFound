package com.example.lostandfound;

public final class FirestoreNames {
    private FirestoreNames(){}

    // strings that represent all collection names
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_USER_VERIFICATIONS = "user_verifications";

    // strings that represent all key names in the map that is stored
    public static final String USERS_FIRSTNAME = "firstName";
    public static final String USERS_LASTNAME = "lastName";
    public static final String USER_VERIFICATIONS_HASHEDCODE = "hashedCode";
    public static final String USER_VERIFICATIONS_TIMESTAMP = "timestamp";
}
