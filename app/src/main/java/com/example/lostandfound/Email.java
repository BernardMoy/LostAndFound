package com.example.lostandfound;

import android.content.Context;

public class Email {

    private Context ctx;

    public Email(Context ctx){
        this.ctx = ctx;
    }

    // method to validate email and return error message, or return null if pass
    public String validateEmail(String email){
        if (email.isEmpty()){
            return ctx.getString(R.string.email_empty_error);

        } else if (!email.endsWith(ctx.getString(R.string.email_end_string))){
            return ctx.getString(R.string.email_not_uni_error);
        }

        return null;
    }
}
