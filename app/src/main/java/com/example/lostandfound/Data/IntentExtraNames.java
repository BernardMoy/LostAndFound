package com.example.lostandfound.Data;

public final class IntentExtraNames {
    private IntentExtraNames(){}

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
    public static final String INTENT_CLAIM_ITEM = "claim_id";
    public static final String INTENT_CLAIM_ID_LIST = "claim_id_list"; // for the found user

    // chat inbox id
    public static final String INTENT_CHAT_INBOX_ID = "chat_inbox_id";

}
