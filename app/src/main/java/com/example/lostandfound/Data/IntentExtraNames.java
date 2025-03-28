package com.example.lostandfound.Data;

public final class IntentExtraNames {
    private IntentExtraNames() {
    }

    // strings that represent all collection names
    /*
    Despite they are named after lost ids and found ids, the LostItem (Object) and FoundItem (Object)
    are instead passed.
    This is because the items have to be already loaded to generate the previews of the items.
    Then, it is unnecessary to load the items again when viewing a specific item.
     */
    public static final String INTENT_LOST_ID = "lost_item";
    public static final String INTENT_FOUND_ID = "found_item";

    // string for passing to the done activity
    public static final String INTENT_DONE_ACTIVITY_TITLE = "title";

    // claim id for passing to view claim activity
    public static final String INTENT_CLAIM_ITEM = "claim_item";
    // for the view found to view claim list intent, the INTENT_FOUND_ID is passed instead

    // chat user
    public static final String INTENT_CHAT_USER = "chat_user";

    public static final String INTENT_SCORE_DATA = "score_data";

}
