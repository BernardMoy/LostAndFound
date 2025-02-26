package com.example.lostandfound.ui.ItemComparison

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.Data.ScoreData
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.MatchingFunctions.ScoreDataCallback
import com.example.lostandfound.MatchingFunctions.getMatchingScores

interface TestItemCallback {
    fun onComplete(success: Boolean)
}

class ItemComparisonViewModel : ViewModel() {
    val lostItemID: MutableState<String> = mutableStateOf("")
    val foundItemID: MutableState<String> = mutableStateOf("")

    var thisLostItem: LostItem = LostItem()
    var thisFoundItem: FoundItem = FoundItem()
    var thisScoreData: ScoreData = ScoreData()

    val isLoading: MutableState<Boolean> = mutableStateOf(false)
    val displayedString: MutableState<String> = mutableStateOf("")

    fun loadLostItem(callback: TestItemCallback) {
        ItemManager.getLostItemFromId(lostItemID.value, object : ItemManager.LostItemCallback {
            override fun onComplete(lostItem: LostItem?) {
                if (lostItem == null) {
                    callback.onComplete(false)
                    return
                }

                thisLostItem = lostItem
                callback.onComplete(true)
            }
        })
    }

    fun loadFoundItem(callback: TestItemCallback) {
        ItemManager.getFoundItemFromId(foundItemID.value, object : ItemManager.FoundItemCallback {
            override fun onComplete(foundItem: FoundItem?) {
                if (foundItem == null) {
                    callback.onComplete(false)
                    return
                }

                thisFoundItem = foundItem
                callback.onComplete(true)
            }
        })
    }

    fun loadData(context: Context, callback: TestItemCallback) {
        // using thisLost and thisFound, return the score data
        getMatchingScores(
            context = context,
            lostItem = thisLostItem,
            foundItem = thisFoundItem,
            object : ScoreDataCallback {
                override fun onScoreCalculated(scoreData: ScoreData) {
                    thisScoreData = scoreData
                    displayedString.value = thisScoreData.toString()
                    callback.onComplete(true)
                }
            }
        )
    }
}