package com.example.lostandfound.ui.Search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.Utility.MatchCallback
import com.example.lostandfound.Utility.matchItems

interface Callback<T> {
    fun onComplete(result: T)
}

class SearchViewModel: ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    // placeholder function for lost item
    // both are loaded on create
    var lostItem: LostItem = LostItem()
    var claimedItem: Claim = Claim()

    var matchedFoundItems: MutableList<FoundItem> = mutableListOf()

    // initialise the list of matching found items to be displayed as empty list
    // return true if successful, false otherwise
    fun loadItems(callback: Callback<Boolean>){
        matchItems(lostItem = lostItem, object: MatchCallback{
            override fun onComplete(result: MutableList<FoundItem>?) {

                // load the items into matchedFoundItems
                if (result != null){
                    matchedFoundItems = result

                    // also load the claimed item if the lost status != 0 (Have claimed already)
                    if (lostItem.status != 0){
                        ItemManager.getClaimFromLostId(lostItem.itemID, object: ItemManager.LostClaimCallback{
                            override fun onComplete(claim: Claim?) {
                                isLoading.value = false

                                if (claim == null){
                                    callback.onComplete(false)
                                    return
                                }

                                claimedItem = claim
                                // return true
                                callback.onComplete(true)
                            }

                        })
                    } else {
                        isLoading.value = false
                        callback.onComplete(true)
                    }

                } else {
                    callback.onComplete(false)
                }
            }
        })
    }
}