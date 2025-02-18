package com.example.lostandfound.Data

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.Parcelize

/*
A data class storing
The image score,
The category score,
The color score,
The brand score
and the location score

nullable values are those that users can leave the input empty and will not be considered


Used to be passed to view comparison activity to display the metrics
(Each score has a range of 0 to 3 inclusive)
 */
@Parcelize
data class ScoreData(
    var imageScore: Double? = null,
    var categoryScore: Double = 0.0,
    var colorScore: Double = 0.0,
    var brandScore: Double? = null,
    var locationScore: Double? = null,
    var overallScore: Double = 0.0
): Parcelable
