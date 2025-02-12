package com.example.lostandfound.ui.PermissionsTest

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PermissionsTestViewModel: ViewModel() {
    val image1: MutableState<Uri?> = mutableStateOf(null)

    val currentLat: MutableState<Double> = mutableDoubleStateOf(0.0)
    val currentLng: MutableState<Double> = mutableDoubleStateOf(0.0)

    fun onImagePicked(uri: Uri?){
        image1.value = uri
    }


}