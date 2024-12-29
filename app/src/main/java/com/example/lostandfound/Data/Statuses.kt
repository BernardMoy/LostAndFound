package com.example.lostandfound.Data

val lostStatus: Map<Int, String> = mapOf(
    0 to "Not found",    // 0 is the default
    1 to "Claim pending approval",
    2 to "Item claimed"
)

val foundStatus: Map<Int, String> = mapOf(
    0 to "Not matched",
    1 to "Claim pending approval",
    2 to "Item claimed"
)