package com.example.lostandfound.ui.Search

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.FirebaseManagers.ItemManager.getMatchItemsFromLostItem

interface Callback<T> {
    fun onComplete(result: T)
}

class SearchViewModel : ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    // placeholder function for lost item
    // both are loaded on create
    var lostItem: LostItem = LostItem()
    var claimedItem: Claim = Claim()

    val selectedOrderingOption: MutableState<String> = mutableStateOf("")

    var matchedFoundItems: MutableList<Pair<FoundItem, ScoreData>> = mutableListOf()

    // initialise the list of matching found items to be displayed as empty list
    // return true if successful, false otherwise
    fun loadItems(context: Context, callback: Callback<Boolean>) {
        getMatchItemsFromLostItem(
            context = context,
            lostItem = lostItem,
            object : ItemManager.MatchFoundCallback {
                override fun onComplete(result: MutableList<Pair<FoundItem, ScoreData>>?) {
                    // result == null indicates error
                    if (result == null) {
                        callback.onComplete(false)
                        return
                    }

                    matchedFoundItems = result

                    // also load the claimed item if the lost status != 0 (Have claimed already)
                    if (lostItem.status != 0) {
                        ItemManager.getClaimFromLostId(
                            lostItem.itemID,
                            object : ItemManager.LostClaimCallback {
                                override fun onComplete(claim: Claim?) {
                                    isLoading.value = false

                                    if (claim == null) {
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
                }
            })
    }

    // to be called when the "We will track it for you" button is clicked
    // return true if successful, false otherwise
    fun onWeWillTrackClicked(
        callback: Callback<Boolean>
    ) {
        ItemManager.updateIsTracking(
            lostItem.itemID,
            true,  // set it to is tracking
            object : ItemManager.UpdateLostCallback {
                override fun onComplete(result: Boolean) {
                    if (!result) {
                        callback.onComplete(false)
                        return
                    }

                    callback.onComplete(true)
                }
            }
        )
    }
}