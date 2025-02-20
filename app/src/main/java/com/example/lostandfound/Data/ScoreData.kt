package com.example.lostandfound.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

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

) : Parcelable {
    // used to determine if their images are considered close match from the scores
    // if yes it is shown as close match in the found item previews
    // used to display in search activity
    fun isImageCloseMatch(): Boolean {
        if (imageScore == null) {
            return false
        } else return imageScore!! >= 1.5
    }

    fun isDetailsCloseMatch(): Boolean {
        // only brand is nullable
        if (brandScore == null) {
            return isCategoryCloseMatch() && isColorCloseMatch()

        } else {
            return isCategoryCloseMatch() && isColorCloseMatch() && isBrandCloseMatch()
        }
    }

    fun isLocationCloseMatch(): Boolean {
        // locations are considered close if they are around <83m apart
        // i.e. score larger than 2.5
        if (locationScore == null) {
            return false
        }
        return locationScore!! >= 2.5
    }

    // is category / color / brand match: Used to display in the view comparison activity
    fun isCategoryCloseMatch(): Boolean {
        return categoryScore == 3.0
    }

    fun isColorCloseMatch(): Boolean {
        return colorScore == 3.0
    }

    fun isBrandCloseMatch(): Boolean {
        if (brandScore == null) {
            return false
        }
        return brandScore!! == 3.0
    }

    // return the overall similarity in a percentage.
    fun getOverallSimilarity(): Double {
        val similarity = overallScore / 3.0
        val similarityRounded = String.format(Locale.UK, "%.3f", similarity)
            .toDouble()   // get the last 3 decimal digits
        return similarityRounded
    }

}
