package com.example.lostandfound.MatchingFunctions

import java.util.Locale
import kotlin.math.abs

/*
Two words are similar if one can be constructed from the other by an addition, deletion, substitution or a swap of characters.
Time complexity: O(n)
 */
fun areWordsSimilar(word1: String, word2: String): Boolean {
    val word1trimmed = word1.trim().lowercase(Locale.UK)
    val word2trimmed = word2.trim().lowercase(Locale.UK)

    if (word1trimmed == word2trimmed) {
        return true
    }
    // if their length differs by more than 1, their edit distance is definitely not 1
    if (abs(word2trimmed.length - word1trimmed.length) > 1) {
        return false
    }

    // if one word have one length more than the other, then we need to delete a character of the longer word
    var numberOfEdits = 0
    if (abs(word2trimmed.length - word1trimmed.length) == 1) {
        // delete a character from the one with longer length, allowing at most one edit.
        var i = 0
        var j = 0
        while (i < word1trimmed.length && j < word2trimmed.length) {
            if (word1trimmed[i] != word2trimmed[j]) {
                if (numberOfEdits == 1) {
                    return false
                }
                numberOfEdits += 1

                // increment i if word1 is longer, j otherwise
                if (word1trimmed.length > word2trimmed.length) i++ else j++

                // after increment, if i and j are still not equal, then there definitely is more than one edit
                // in the next loop
            } else {
                i++
                j++
            }
        }

        // at the end check if there is an extra characters at the end
        if (i < word1trimmed.length || j < word2trimmed.length) {
            numberOfEdits += 1
        }

        // allow at most one edit
        return numberOfEdits == 1

    } else {
        // if they have the same length, then check if their difference is caused by a swap or sub
        // keep track of the first index
        var numberOfDifference = 0
        var firstDiffer = -1  // store the first different index

        for (i in word1trimmed.indices) {
            if (word1trimmed[i] != word2trimmed[i]) {
                numberOfDifference += 1
                if (firstDiffer == -1) {
                    firstDiffer = i
                }
            }
        }

        if (numberOfDifference > 2) {
            return false
        }

        // if equal 1 then it is caused by a substitution
        if (numberOfDifference == 1) {
            return true
        }

        // else check if the two differences are caused by a swap
        // firstDiffer will not be the last index else their number of difference will be exactly 1 (Captured above)
        if (word1trimmed[firstDiffer] == word2trimmed[firstDiffer + 1] && word1trimmed[firstDiffer + 1] == word2trimmed[firstDiffer]) {
            return true
        }

        // else return false
        return false
    }

    return false
}