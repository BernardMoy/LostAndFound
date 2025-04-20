package com.example.lostandfound.Utility

import com.google.android.gms.maps.model.LatLng
import kotlin.math.cos
import kotlin.math.sqrt

object LocationManager {
    // stores the default location to be used when location is null
    val DEFAULT_LOCATION = LatLng(52.37930763817003, -1.5614912710215834)

    // get location description given a latlng value
    /*
    fun getLocationDescription(context: Context, latlng: LatLng): String {
        val geocoder = Geocoder(context, Locale.UK)
        val address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)

        return if (address == null) {
            "Unknown location"
        } else {
            address[0].getAddressLine(0).toString()
        }
    }

     */

    // convert a pair double value (Used for parcelable class) to LatLng object
    fun pairToLatlng(pair: Pair<Double, Double>): LatLng {
        return LatLng(
            pair.first, pair.second
        )
    }

    // convert a HASHMAP RETRIEVED FROM FIREBASE to pair double
    fun LocationToPair(location: HashMap<*, *>): Pair<Double, Double> {
        return Pair<Double, Double>(
            location["latitude"] as Double,
            location["longitude"] as Double
        )
    }

    // convert a latlng to pair (Provided the latlng is not null)
    fun latLngToPair(location: LatLng): Pair<Double, Double> {
        return Pair<Double, Double>(
            location.latitude,
            location.longitude
        )
    }

    // given two latlng pairs, return their distances
    // used to compare how close two locations are
    fun getDistanceBetweenLocations(
        location1: Pair<Double, Double>,
        location2: Pair<Double, Double>
    ): Double {
        val lat1Rad = Math.toRadians(location1.first)
        val lat2Rad = Math.toRadians(location2.first)
        val lon1Rad = Math.toRadians(location1.second)
        val lon2Rad = Math.toRadians(location2.second)

        val x = (lon2Rad - lon1Rad) * cos((lat1Rad + lat2Rad) / 2)
        val y = (lat2Rad - lat1Rad)
        return sqrt(x * x + y * y) * 6371 // Earth radius in km
    }
}