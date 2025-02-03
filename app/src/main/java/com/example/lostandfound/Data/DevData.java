package com.example.lostandfound.Data;

import java.util.Set;

public final class DevData {
    private DevData(){}

    /*
    A list of emails with special access.
    When registered with this email, an email wont be sent
    and also user can bypass the verify email process.

    These users also have access to the developer settings if enabled.
     */
    public static final Set<String> DEV_EMAILS = Set.of(
                "testdevholo@warwick.ac.uk",   // u2256784 is not a dev
                "testdevholo2@warwick.ac.uk",
                "testdevholo3@warwick.ac.uk"
    );

    // whether the dev settings are shown to dev users.
    // turn it off for demo purposes
    public static final Boolean IS_DEV_SETTINGS_SHOWN = true;
}
