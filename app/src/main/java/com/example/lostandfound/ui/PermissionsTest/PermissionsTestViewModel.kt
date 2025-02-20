package com.example.lostandfound.ui.PermissionsTest

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class PermissionsTestViewModel : ViewModel() {
    val image: MutableState<Uri?> = mutableStateOf(null)

    val currentLocation: MutableState<LatLng> = mutableStateOf(
        LatLng(52.37930763817003, -1.5614912710215834)
    )

    fun onImagePicked(uri: Uri?) {
        image.value = uri
    }


}