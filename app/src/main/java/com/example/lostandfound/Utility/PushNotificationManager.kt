package com.example.lostandfound.Utility

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.lostandfound.Data.SharedPreferencesNames

object PushNotificationManager {
    fun setItemPushNotificationEnabled(
        enabled: Boolean,
        context: Context
    ) {
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_PUSH_NOTIFICATION_ITEM,
            Context.MODE_PRIVATE
        )
        sp.edit().putBoolean(SharedPreferencesNames.ITEM_PUSH_NOTIFICATION_VALUE, enabled).apply()
    }

    // load animation to be called on create in the main activity
    fun loadItemPushNotificationEnabled(
        context: Context
    ): Boolean {
        // first get the settings from sp or true if not exist yet
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_PUSH_NOTIFICATION_ITEM,
            Context.MODE_PRIVATE
        )
        return sp.getBoolean(SharedPreferencesNames.ITEM_PUSH_NOTIFICATION_VALUE, true)
    }


    fun setMessagePushNotificationEnabled(
        enabled: Boolean,
        context: Context
    ) {
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_PUSH_NOTIFICATION_MESSAGE,
            Context.MODE_PRIVATE
        )
        sp.edit().putBoolean(SharedPreferencesNames.MESSAGE_PUSH_NOTIFICATION_VALUE, enabled).apply()
    }

    // load animation to be called on create in the main activity
    fun loadMessagePushNotificationEnabled(
        context: Context
    ): Boolean {
        // first get the settings from sp or true if not exist yet
        val sp = context.getSharedPreferences(
            SharedPreferencesNames.NAME_PUSH_NOTIFICATION_MESSAGE,
            Context.MODE_PRIVATE
        )
        return sp.getBoolean(SharedPreferencesNames.MESSAGE_PUSH_NOTIFICATION_VALUE, true)
    }
}
