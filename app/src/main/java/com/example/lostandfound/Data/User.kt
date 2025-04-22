package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
stores the attributes of a user that are commonly used and duplicated in the db only
 */
@Parcelize
data class User(
    val userID: String = "",
    val avatar: String = "",
    val firstName: String = "",
    val lastName: String = "",
    // does not contain isAdmin, notificationEnable etc.
    // They are not necessary and are only used in specific cases

) : Parcelable