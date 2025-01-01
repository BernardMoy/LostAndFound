package com.example.lostandfound.ui.ViewLost

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel

interface Callback<T> {
    fun onComplete(result: T)
}

class ViewLostViewModel : ViewModel(){
    var itemID: String = ""

    // item data are stored here
    var userID: String = ""
    var itemName: String = ""
    var category: String = ""
    var subCategory: String = ""
    var color: String = ""
    var dateTime: Long = 0L
    var brand: String? = null
    var description: String? = null
    var status: Int = 0

    // image stored here
    var image: Uri? = null

    // function to get item data from firebase. Called only after itemID has been loaded from intent
    // return true if successful, false otherwise
    fun getItemData(callback: Callback<Boolean>){

    }
}