package com.example.lostandfound.MatchingFunctions

import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem

fun getCategoryScore(lostCategory: String, lostSubCategory: String, foundCategory: String, foundSubcategory: String): Double{
    /*
    3 if both category and subcategory match
    2 if category match and subcategory dont match
    0 if both dont match
    1.5 if one of the category is "Others".
     */
    if (lostCategory == "Others" || foundCategory == "Others"){
        return 1.5

    } else if (lostCategory == foundCategory){
        if (lostSubCategory == foundSubcategory){
            return 3.0
        } else{
            return 2.0
        }
        
    } else{
        return 0.0
    }
}