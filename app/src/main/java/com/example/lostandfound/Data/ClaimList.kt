package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// this class is a parcelable implementation of a list of claim objects
@Parcelize
data class ClaimList(
    val claimList: MutableList<Claim> = mutableListOf()

) : Parcelable