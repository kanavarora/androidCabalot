package com.likwidskin.cabAgg;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kanavarora on 3/9/16.
 */
public class Utility {

    public static Location locationFromLatLng(LatLng latLng) {
        Location loc = new Location("dummy");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    public static LatLng latLngFromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
