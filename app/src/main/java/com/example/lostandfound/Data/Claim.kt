package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// stores a claim object that currently does not include lost and found item, they are rendered when clicked on view claim instead
@Parcelize
data class Claim(
    val claimID: String = "",
    val lostItemID: String = "",
    val foundItemID: String = "",
    val isApproved: Boolean = false,
    val timestamp: Long = 0L,
    val securityQuestionAns: String = ""

) : Parcelable