package com.example.lostandfound.ui.ViewClaimList

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.ClaimList
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseManagers.UserManager
import com.example.lostandfound.Utility.ErrorCallback
import com.example.lostandfound.ui.ViewComparison.Callback

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewClaimListViewModel : ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLocationDialogShown: MutableState<Boolean> = mutableStateOf(false)

    // default claim placeholder data
    var claimDataList: ClaimList = ClaimList()

    // default lost item placeholder data
    var lostItemDataList: MutableList<LostItem> = mutableListOf()

    // default found item placeholder data
    var foundItemData = FoundItem()


}