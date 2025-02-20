package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userID: String = "",
    val avatar: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""

) : Parcelable