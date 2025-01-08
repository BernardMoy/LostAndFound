package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Claim(
    val claimID: String = "",
    val lostItemID: String = "",
    val foundItemID: String = "",
    val isApproved: Boolean = false,
    val timestamp: Long = 0L

) : Parcelable