package com.example.lostandfound.Data

import com.example.lostandfound.R

val lostStatusText: Map<Int, String> = mapOf(
    0 to "Not found",    // 0 is the default
    1 to "Claim pending approval",
    2 to "Item claimed"
)

val statusColor: Map<Int, Int> = mapOf(
    0 to R.color.status0,
    1 to R.color.status1,
    2 to R.color.status2
)

val foundStatusText: Map<Int, String> = mapOf(
    0 to "Not matched",
    1 to "Claim pending approval",  // can have more then one claim pending approval
    2 to "Item claimed"
)