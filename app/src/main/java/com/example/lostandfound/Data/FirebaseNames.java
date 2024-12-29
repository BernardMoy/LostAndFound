package com.example.lostandfound.Data;

public final class FirebaseNames {
    private FirebaseNames(){}

    // strings that represent all collection names
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_USER_VERIFICATIONS = "user_verifications";
    public static final String COLLECTION_LOST_ITEMS = "lost_items";

    // strings that represent all key names in the map that is stored
    public static final String USERS_FIRSTNAME = "firstName";
    public static final String USERS_LASTNAME = "lastName";
    public static final String USERS_AVATAR = "avatar";

    public static final String USER_VERIFICATIONS_HASHEDCODE = "hashedCode";
    public static final String USER_VERIFICATIONS_TIMESTAMP = "timestamp";

    public static final String LOSTFOUND_IMAGE_FOLDER = "lost_images";
    public static final String LOSTFOUND_USER = "user";
    public static final String LOSTFOUND_ITEMNAME = "itemName";
    public static final String LOSTFOUND_CATEGORY = "category";
    public static final String LOSTFOUND_SUBCATEGORY = "subCategory";
    public static final String LOSTFOUND_COLOR = "color";
    public static final String LOSTFOUND_BRAND = "brand";
    public static final String LOSTFOUND_EPOCHDATETIME = "dateTime";
    public static final String LOSTFOUND_DESCRIPTION = "description";
    public static final String LOSTFOUND_LOCATION = "location";
    public static final String LOSTFOUND_STATUS = "status";
    public static final String FOUND_SECURITY_Q = "security_question";
    public static final String FOUND_SECURITY_Q_ANS = "security_question_answer";

}
