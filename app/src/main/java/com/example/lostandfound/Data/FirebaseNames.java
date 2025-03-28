package com.example.lostandfound.Data;

public final class FirebaseNames {
    private FirebaseNames() {
    }

    // strings that represent all collection names
    /*
    Whenever these names are changed, reflect the changes in the firebase cloud functions as well
     */
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_USER_VERIFICATIONS = "user_verifications";
    public static final String COLLECTION_LOST_ITEMS = "lost_items";
    public static final String COLLECTION_FOUND_ITEMS = "found_items";
    public static final String COLLECTION_REPORT_ISSUE = "reported_issues";
    public static final String COLLECTION_CLAIMED_ITEMS = "claims";
    public static final String COLLECTION_CHATS = "chats";
    public static final String COLLECTION_CHAT_INBOXES = "chat_inboxes";
    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    public static final String COLLECTION_ACTIVITY_LOG_ITEMS = "activity_log_items";

    // strings that represent all key names in the map that is stored
    public static final String USERS_FIRSTNAME = "firstName";
    public static final String USERS_LASTNAME = "lastName";
    public static final String USERS_AVATAR = "avatar";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_FCM_TOKEN = "fcm_token";
    public static final String USERS_ITEM_NOTIFICATION_ENABLED = "item_notification";
    public static final String USERS_MESSAGE_NOTIFICATION_ENABLED = "message_notification";

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
    public static final String FOUND_SECURITY_Q = "security_question";
    public static final String FOUND_SECURITY_Q_ANS = "security_question_answer";
    public static final String LOSTFOUND_TIMEPOSTED = "time_posted";
    public static final String LOST_IS_TRACKING = "is_tracking";

    // for use in claimed items
    public static final String CLAIM_LOST_ITEM_ID = "lost_item_id";
    public static final String CLAIM_FOUND_ITEM_ID = "found_item_id";
    public static final String CLAIM_IS_APPROVED = "is_approved";
    public static final String CLAIM_TIMESTAMP = "timestamp";
    public static final String CLAIM_SECURITY_QUESTION_ANS = "security_question_answer";

    // for storing lost and found images in storage
    public static final String FOLDER_LOST_IMAGE = "lost_images";
    public static final String FOLDER_FOUND_IMAGE = "found_images";

    // for reporting issues
    public static final String REPORT_ISSUE_USER = "user";
    public static final String REPORT_ISSUE_DESC = "description";

    // for chat system
    public static final String CHAT_SENDER_USER_ID = "sender_user_id";
    public static final String CHAT_RECIPIENT_USER_ID = "recipient_user_id";
    public static final String CHAT_FROM_TO = "from_to";  // an array for storing [from user id, to user id]
    public static final String CHAT_CONTENT = "chat_content";
    public static final String CHAT_TIMESTAMP = "timestamp";
    public static final String CHAT_IS_READ_BY_RECIPIENT = "is_read_by_recipient";

    // for chat inbox whether the message is read or unread
    public static final String CHAT_INBOX_PARTICIPANTS = "participants";  // sorted by alphabetical order
    public static final String CHAT_INBOX_LAST_MESSAGE_ID = "last_message_id";
    public static final String CHAT_INBOX_UPDATED_TIMESTAMP = "updated_timestamp";

    // for notifications
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_USER_ID = "user_id";
    public static final String NOTIFICATION_TIMESTAMP = "timestamp";
    public static final String NOTIFICATION_TYPE = "type";  // 0 1 2 3
    public static final String NOTIFICATION_IS_READ = "is_read";
    public static final String NOTIFICATION_LOST_ITEM_ID = "lost_item_id";   // for type 0 only
    public static final String NOTIFICATION_FOUND_ITEM_ID = "found_item_id";   // for type 0 only
    public static final String NOTIFICATION_CLAIM_ID = "claim_id";   // for type 1 2 3

    // for activity log items
    public static final String ACTIVITY_LOG_ITEM_TYPE = "type";  // 0 1 2 3 4 5
    public static final String ACTIVITY_LOG_ITEM_CONTENT = "content";
    public static final String ACTIVITY_LOG_ITEM_USER_ID = "user_id";
    public static final String ACTIVITY_LOG_ITEM_TIMESTAMP = "timestamp";

}
