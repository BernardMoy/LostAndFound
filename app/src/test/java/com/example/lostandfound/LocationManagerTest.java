package com.example.lostandfound;


import static java.lang.Math.abs;

import com.example.lostandfound.Utility.LocationManager;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import kotlin.Pair;

public class LocationManagerTest {
    private static final LatLng location1 = new LatLng(
            52.40187250782763,
            -1.5543257891147153
    );
    private static final LatLng location2 = new LatLng(
            52.379265564031826,
            -1.561483721025337
    );


    // test the distance between WESTWOOD and MEDICAL SCHOOL
    // obtained from https://www.distance.to/52.374552794297,%20-1.5505370226481643/52.38871254041315,%20-1.5598439802945556
    private static final double ACTUAL_DISTANCE = 1.70;

    @Test
    public void testGetDistanceBetweenLocations() {
        double calculatedDistance = LocationManager.INSTANCE.getDistanceBetweenLocations(
                new Pair<>(52.374552794297, -1.5505370226481643),
                new Pair<>(52.38871254041315, -1.5598439802945556)
        );
        assert abs(calculatedDistance - ACTUAL_DISTANCE) <= 1e-1;
    }

}
