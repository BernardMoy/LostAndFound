package com.example.lostandfound.Data;

public final class SharedPreferencesNames {
    private SharedPreferencesNames(){}

    // strings that represent all names of shared prefs
    public static final String NAME_USERS = "user";

    // strings that represent all key names
    public static final String USER_FIRSTNAME = "firstName";
    public static final String USER_LASTNAME = "lastName";
    public static final String USER_EMAIL = "email";
    public static final String USER_AVATAR = "avatar";

    public static final String THEME_NAME = "device_theme";
    public static final String THEME_VALUE = "value";  // 0 for light, 1 for dark, none for use device theme
}
