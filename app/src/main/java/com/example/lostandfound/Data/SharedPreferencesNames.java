package com.example.lostandfound.Data;

public final class SharedPreferencesNames {
    private SharedPreferencesNames() {
    }

    // strings that represent all names of shared prefs
    public static final String NAME_USERS = "user";
    public static final String NAME_THEME = "device_theme";
    public static final String NAME_ISLARGEFONT = "is_large_font";
    public static final String NAME_ANIMATION = "animation_enabled";
    public static final String NAME_AUTO_LOADING = "auto_loading_enabled";
    public static final String NAME_SHOW_WELCOME_MESSAGE = "show_welcome_message";

    // strings that represent all key names
    public static final String USER_FIRSTNAME = "firstName";
    public static final String USER_LASTNAME = "lastName";
    public static final String USER_EMAIL = "email";
    public static final String USER_AVATAR = "avatar";

    public static final String THEME_VALUE = "value";  // 0 for light (Default value if not found), 1 for dark, none for use device theme
    public static final String ISLARGEFONT_VALUE = "value";
    public static final String ANIMATION_VALUE = "value";   // false or true whether animations are enabled
    public static final String AUTO_LOADING_VALUE = "value";
    public static final String SHOW_WELCOME_MESSAGE_VALUE = "value";
}
