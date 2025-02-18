package com.example.lostandfound.ui.ImageComparison

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lostandfound.TFLiteManager.ImageClassifier

class ImageComparisonViewModel: ViewModel() {
    val image1: MutableState<Uri?> = mutableStateOf(null)
    val image2: MutableState<Uri?> = mutableStateOf(null)
    val distance: MutableState<Float> = mutableFloatStateOf(0.0f)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    fun onImage1Picked(uri: Uri?){
        image1.value = uri
    }

    fun onImage2Picked(uri: Uri?){
        image2.value = uri
    }
}