package com.example.lostandfound.MatchingFunctions

import com.example.lostandfound.Data.FoundItem
import com.example.lostandfound.Data.LostItem

fun getCategoryScore(lostCategory: String, lostSubCategory: String, foundCategory: String, foundSubcategory: String): Double{
    if (lostCategory == foundCategory){
        // if both are others, return a score of 3
        if (lostCategory == "Others"){
            return 3.0
        }

        // else depend on their subcategory
        if (lostSubCategory == foundSubcategory){
            return 3.0
        } else{
            return 0.0    // Unlikely to make mistakes here
        }
        
    } else{
        return 0.0
    }
}