package com.example.lostandfound.ui.Settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.R

class SettingsViewModel : ViewModel(){

    // placeholder lost and found items used for developer settings
    var placeholderLostItem = LostItem(
        itemID = "Placeholder lost",
        userID = "Unknown",
        itemName = "Unknown",
        category = "Unknown",
        subCategory = "Unknown",
        color = "Unknown",
        brand = "",
        dateTime = 0L,
        location = Pair(52.37930763817003,-1.5614912710215834),
        description = "",
        timePosted = 0L,
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
        status = 0
    )

    var placeholderFoundItem = FoundItem(
        itemID = "Placeholder found",
        userID = "Unknown",
        itemName = "Unknown",
        category = "Unknown",
        subCategory = "Unknown",
        color = "Unknown",
        brand = "",
        dateTime = 0L,
        location = Pair(52.37930763817003,-1.5614912710215834),
        description = "",
        timePosted = 0L,
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
        securityQuestion = "",
        securityQuestionAns = "",
        status = 0
    )


}