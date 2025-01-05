package com.example.lostandfound.ui.ReportIssue

import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.LostItem

interface Callback<T> {
    fun onComplete(result: T)
}

class SearchViewModel: ViewModel() {
    // placeholder function for lost item
    var lostItem: LostItem = LostItem(
        itemID = "Unknown",
        userID = "Unknown",
        itemName = "Unknown",
        category = "Unknown",
        subCategory = "Unknown",
        color = "Unknown",
        brand = "",
        dateTime = 0L,
        location = Pair(52.37930763817003,-1.5614912710215834),
        description = "",
        status = 0,
        timePosted = 0L,
        image = ""
    )

    // initialise the list of matching found items to be displayed as empty list


}