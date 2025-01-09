package com.example.lostandfound.ui.ViewClaimList

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.Claim
import com.example.lostandfound.Data.ClaimPreview
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.FirebaseManagers.ItemManager

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewClaimListViewModel : ViewModel() {
    val isLoading: MutableState<Boolean> = mutableStateOf(true)

    // default found item placeholder data, loaded upon activity creation
    var foundItem = FoundItem()

    // default claim preview list, to be updated in getAllData()
    var claimPreviewList: MutableList<ClaimPreview> = mutableListOf()


    /*
    function to get all claim previews given a found item,
    called everytime the screen is reloaded
    return true if data are successfully retrieved (or no data), false otherwise
     */
    fun getAllData(callback: Callback<Boolean>){
        // first clear the item data array
        claimPreviewList.clear()

        // get all data from firebase with matching user ids
        ItemManager.getClaimsFromFoundId(foundItem.itemID, object: ItemManager.FoundClaimCallback{
            override fun onComplete(claimList: MutableList<Claim>) {
                // the list should not be empty
                if (claimList.isEmpty()){
                    callback.onComplete(false)
                    return
                }

                val claimListSize = claimList.size
                var fetchedItems = 0

                // for each item, get its claim item preview
                claimList.forEach { claim ->
                    ItemManager.getClaimPreviewFromClaim(claim, object : ItemManager.ClaimPreviewCallback{
                        override fun onComplete(claimPreview: ClaimPreview?) {
                           if (claimPreview == null){
                               callback.onComplete(false)
                               return
                           }

                            // add it to the result
                            claimPreviewList.add(claimPreview)

                            fetchedItems ++

                            // return true when all items have been fetched
                            if (fetchedItems == claimListSize){
                                callback.onComplete(true)
                            }
                        }
                    })
                }
            }
        })
    }
}