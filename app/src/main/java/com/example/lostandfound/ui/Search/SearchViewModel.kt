package com.example.lostandfound.ui.Search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Utility.MatchCallback
import com.example.lostandfound.Utility.matchItems

interface Callback<T> {
    fun onComplete(result: T)
}

class SearchViewModel: ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

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

    var matchedFoundItems: MutableList<FoundItem> = mutableListOf()

    // initialise the list of matching found items to be displayed as empty list
    // return true if successful, false otherwise
    fun loadItems(callback: Callback<Boolean>){
        matchItems(lostItem = lostItem, object: MatchCallback{
            override fun onComplete(result: MutableList<FoundItem>?) {
                isLoading.value = false

                // load the items into matchedFoundItems
                if (result != null){
                    matchedFoundItems = result
                    callback.onComplete(true)

                } else {
                    callback.onComplete(false)
                }
            }
        })
    }
}