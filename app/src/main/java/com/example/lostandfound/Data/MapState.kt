package com.example.lostandfound.Data

import com.google.maps.android.compose.MapProperties

data class MapState(
    val properties: MapProperties = MapProperties(
        isMyLocationEnabled = true,   // allow accessing device location
    )
)
