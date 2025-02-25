package com.example.lostandfound.ui.ItemComparison

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem

class ItemComparisonViewModel : ViewModel() {
    val lostItemID: MutableState<String> = mutableStateOf("")
    val foundItemID: MutableState<String> = mutableStateOf("")

    val lostItem: LostItem = LostItem()
    val foundItem: FoundItem = FoundItem()

    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val comparisonResult: MutableState<String> = mutableStateOf("")

    fun loadLostItem(){

    }

    fun loadFoundItem(){

    }

    fun compare(){

    }
}