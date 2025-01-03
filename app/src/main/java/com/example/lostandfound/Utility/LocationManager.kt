package com.example.lostandfound.Utility

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

object LocationManager {
    fun getLocationDescription(context: Context, latlng: LatLng): String {
        val geocoder = Geocoder(context, Locale.UK)
        val address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)

        return if (address == null){
            "Unknown location"
        } else {
            address[0].getAddressLine(0).toString()
        }
    }
}