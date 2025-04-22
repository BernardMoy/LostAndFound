package com.example.lostandfound.Data;

// hardcoded intent extra names for consistency
public final class IntentExtraNames {
    private IntentExtraNames() {
    }

    public static final String INTENT_LOST_ITEM = "lost_item";
    public static final String INTENT_FOUND_ITEM = "found_item";

    // string for passing to the done activity
    public static final String INTENT_DONE_ACTIVITY_TITLE = "title";

    // claim id for passing to view claim activity
    public static final String INTENT_CLAIM_ITEM = "claim_item";

    // chat user
    public static final String INTENT_CHAT_USER = "chat_user";

    public static final String INTENT_SCORE_DATA = "score_data";

    // user id to be passed to activity log for admins
    public static final String INTENT_ACTIVITY_LOG_USER_ID = "user_id";

}
