package com.example.lostandfound.MatchingFunctions

import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem

fun getCategoryScore(lostItem: LostItem, foundItem: FoundItem): Double{
    /*
    3 if both category and subcategory match
    2 if category match and subcategory dont match
    0 if both dont match
    1.5 if one of the category is "Others".
     */
    if (lostItem.category == "Others" || foundItem.category == "Others"){
        return 1.5

    } else if (lostItem.category == foundItem.category){
        if (lostItem.subCategory == foundItem.subCategory){
            return 3.0
        } else{
            return 2.0
        }
        
    } else{
        return 0.0
    }
}