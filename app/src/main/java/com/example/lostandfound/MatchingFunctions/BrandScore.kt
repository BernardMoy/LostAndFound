package com.example.lostandfound.MatchingFunctions

fun getBrandScore(brand1: String, brand2: String): Int{
    // if they are classified as similar using the word comparison algorithm, return 3
    // else 0
    return if (areWordsSimilar(brand1, brand2)) 3 else 0
}