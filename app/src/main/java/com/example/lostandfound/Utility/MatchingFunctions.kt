package com.example.lostandfound.Utility

import android.net.Uri
import com.example.lostandfound.Data.FirebaseNames
import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem
import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager
import com.example.lostandfound.FirebaseManagers.FirestoreManager
import com.example.lostandfound.FirebaseManagers.ItemManager
import com.example.lostandfound.R

// given a lost item and a found item, determine whether they are matched
/*
    Preconditions are performed in the initial db query:
    1. Lost item user ID != Found item user ID
 */
fun isMatch(
    lostItem: LostItem,
    foundItem: FoundItem

): Boolean{
    /* TODO implement this */

    // currently, return true if their users are different
    return true
}


