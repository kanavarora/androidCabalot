package com.likwidskin.cabAgg.network;

/**
 * Created by kanavarora on 3/21/16.
 */
public class LyftResponse {
    public final float METERS_PER_MILE = 1609.344f;

    private float price;
    public boolean isLyftLineRouteValid;
    private StandardRide standardRide;
    private Directions directions;

// TODO: handle error

    public class StandardRide {
        public float lyftPrice;
        public float dynamicPricing;
        public String minimum;
        public String perMile;
        public String perMinute;
        public String pickup;
    }

    public class Directions {
        public float distanceMeters;
        public float durationSecs;
    }

    public float getStandardPrice () {
        if (this.standardRide == null) {
            return -1.0f;
        }
        if (standardRide.lyftPrice > 0) {
            return standardRide.lyftPrice/100.0f;
        }

        if (this.directions != null && this.standardRide != null) {
            float minimum = Float.parseFloat(this.standardRide.minimum.substring(1));
            float perMile = Float.parseFloat(this.standardRide.perMile.substring(1));
            float perMinute = Float.parseFloat(this.standardRide.perMile.substring(1));
            float pickup = Float.parseFloat(this.standardRide.pickup.substring(1));
            float insurance = 1.50f;

            float cost = pickup + (perMile * (this.directions.distanceMeters/METERS_PER_MILE))
                    + (perMinute * (this.directions.durationSecs/60.0f));
            cost =  Math.max(minimum, cost);
            cost  = cost + (cost * (this.standardRide.dynamicPricing/100.0f));
            cost += insurance;
            return cost;
        }
        return -1.0f;
    }

    public float getDynPricing() {
        if (this.standardRide != null) {
            return this.standardRide.dynamicPricing;
        }
        return 0.0f;
    }

    public float getLyftLinePrice() {
        return this.price/100.0f;
    }
}
