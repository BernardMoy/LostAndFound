package com.example.lostandfound.Data

import com.example.lostandfound.R

val lostStatusText: Map<Int, String> = mapOf(
    0 to "This item has not been found",    // 0 is the default
    1 to "This item has a claim pending approval",
    2 to "This item has claimed another item"
)

val statusColor: Map<Int, Int> = mapOf(
    0 to R.color.status0,
    1 to R.color.status1,
    2 to R.color.status2
)

val foundStatusText: Map<Int, String> = mapOf(
    0 to "This found item has not been claimed",
    1 to "Someone has claimed this item (Pending approval)",  // can have more then one claim pending approval
    2 to "This found item has been claimed and approved"
)