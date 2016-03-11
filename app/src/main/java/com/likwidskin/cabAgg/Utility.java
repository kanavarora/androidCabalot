package com.likwidskin.cabAgg;

import android.content.Context;
import android.location.Location;
import android.util.DisplayMetrics;

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

    public static int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(Context ctx, int dp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
