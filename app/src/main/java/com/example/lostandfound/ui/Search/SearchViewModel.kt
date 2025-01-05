package com.example.lostandfound.ui.ReportIssue

import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FoundItemList
import com.example.lostandfound.Data.LostItem

interface Callback<T> {
    fun onComplete(result: T)
}

class SearchViewModel: ViewModel() {
    // initialise the list of items to be displayed as empty list
    var itemList: FoundItemList = FoundItemList(listOf())


}