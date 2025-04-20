package com.example.lostandfound.ui.Settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.User
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.R

class SettingsViewModel : ViewModel() {
    val isAdmin: MutableState<Boolean> = mutableStateOf(false)

    // placeholder lost and found items used for developer settings
    var placeholderLostItem = LostItem(
        itemID = "Placeholder lost",
        user = User(
            userID = "userID",
            avatar = "",
            firstName = "placeholderFN",
            lastName = "placeholderLN"
        ),
        itemName = "Unknown",
        category = "Unknown",
        subCategory = "Unknown",
        color = listOf(),
        brand = "",
        dateTime = 0L,
        location = Pair(52.37930763817003, -1.5614912710215834),
        description = "",
        timePosted = 0L,
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
        status = 0
    )

    var placeholderFoundItem = FoundItem(
        itemID = "Placeholder found",
        user = User(
            userID = "userID",
            avatar = "",
            firstName = "placeholderFN",
            lastName = "placeholderLN"
        ),
        itemName = "Unknown",
        category = "Unknown",
        subCategory = "Unknown",
        color = listOf(),
        brand = "",
        dateTime = 0L,
        location = Pair(52.37930763817003, -1.5614912710215834),
        description = "",
        timePosted = 0L,
        image = "android.resource://com.example.lostandfound/" + R.drawable.placeholder_image,
        securityQuestion = "",
        securityQuestionAns = "",
        status = 0
    )

    // a async function to load if the user is admin
    // called during on create of the activity
    fun loadIsAdmin() {
        UserManager.isUserAdmin(object :
            UserManager.IsAdminCallback {
            override fun onComplete(result: Boolean) {
                isAdmin.value = result
            }
        })
    }
}