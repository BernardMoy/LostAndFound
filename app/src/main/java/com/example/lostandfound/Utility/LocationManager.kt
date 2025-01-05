package com.example.lostandfound.Utility

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

object LocationManager {
    // get location description given a latlng value
    fun getLocationDescription(context: Context, latlng: LatLng): String {
        val geocoder = Geocoder(context, Locale.UK)
        val address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)

        return if (address == null){
            "Unknown location"
        } else {
            address[0].getAddressLine(0).toString()
        }
    }

    // convert a pair double value (Used for parcelable class) to LatLng object
    fun pairToLatlng(pair: Pair<Double, Double>): LatLng{
        return LatLng(
            pair.first, pair.second
        )
    }

    // convert a HASHMAP RETRIEVED FROM FIREBASE to pair double
    fun LocationToPair(location: HashMap<*, *>): Pair<Double, Double>{
        return Pair<Double, Double>(
            location["latitude"] as Double,
            location["longitude"] as Double
        )
    }

    // convert a latlng to pair
    fun latLngToPair(location: LatLng): Pair<Double, Double>{
        return Pair<Double, Double>(
            location.latitude,
            location.longitude
        )
    }
}