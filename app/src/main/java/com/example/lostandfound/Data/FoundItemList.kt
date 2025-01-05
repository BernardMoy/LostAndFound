package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoundItemList(
    // store a list of found items to be displayed in search activity
    val foundItemList: List<FoundItem>
) : Parcelable