package com.example.lostandfound.Data;

public final class FirebaseNames {
    private FirebaseNames(){}

    // strings that represent all collection names
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_USER_VERIFICATIONS = "user_verifications";
    public static final String COLLECTION_LOST_ITEMS = "lost_items";
    public static final String COLLECTION_FOUND_ITEMS = "found_items";
    public static final String COLLECTION_REPORT_ISSUE = "reported_issues";

    // strings that represent all key names in the map that is stored
    public static final String USERS_FIRSTNAME = "firstName";
    public static final String USERS_LASTNAME = "lastName";
    public static final String USERS_AVATAR = "avatar";

    public static final String USER_VERIFICATIONS_HASHEDCODE = "hashedCode";
    public static final String USER_VERIFICATIONS_TIMESTAMP = "timestamp";

    public static final String LOSTFOUND_USER = "user";
    public static final String LOSTFOUND_ITEMNAME = "itemName";
    public static final String LOSTFOUND_CATEGORY = "category";
    public static final String LOSTFOUND_SUBCATEGORY = "subCategory";
    public static final String LOSTFOUND_COLOR = "color";
    public static final String LOSTFOUND_BRAND = "brand";
    public static final String LOSTFOUND_EPOCHDATETIME = "dateTime";
    public static final String LOSTFOUND_DESCRIPTION = "description";
    public static final String LOSTFOUND_LOCATION = "location";
    public static final String LOSTFOUND_IMAGE = "image";  // to be added to the data map
    public static final String LOSTFOUND_STATUS = "status";
    public static final String FOUND_SECURITY_Q = "security_question";
    public static final String FOUND_SECURITY_Q_ANS = "security_question_answer";
    public static final String LOSTFOUND_ID = "item_id";

    public static final String FOLDER_LOST_IMAGE = "lost_images";
    public static final String FOLDER_FOUND_IMAGE = "found_images";

    public static final String REPORT_ISSUE_USER = "user";
    public static final String REPORT_ISSUE_DESC = "description";

}
