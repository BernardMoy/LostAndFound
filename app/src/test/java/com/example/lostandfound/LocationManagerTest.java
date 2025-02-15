package com.example.lostandfound;


import static java.lang.Math.abs;

import com.example.lostandfound.Utility.LocationManager;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

public class LocationManagerTest {
    private static final LatLng location1 = new LatLng(
            52.40187250782763,
            -1.5543257891147153
    );
    private static final LatLng location2 = new LatLng(
            52.379265564031826,
            -1.561483721025337
    );

    private static final double ACTUAL_DISTANCE = 2.5603;

    @Test
    public void testGetDistanceBetweenLocations(){
        double calculatedDistance = LocationManager.INSTANCE.getDistanceBetweenLocations(location1, location2);
        assert abs(calculatedDistance - ACTUAL_DISTANCE) <= 1e-3;
    }

}
