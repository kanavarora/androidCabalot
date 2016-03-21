package com.likwidskin.cabAgg;

import android.location.Location;

/**
 * Created by kanavarora on 3/21/16.
 */
public class ResultInfo {

    public float mActLowEstimate;
    public float mActHighEstimate;
    public float mActSurgeMultiplier;
    public float mBestLowEstimate;
    public float mBestHighEstimate;
    public float mBestSurgeMultiplier;
    public boolean mIsRouteValid;
    public Location mBestStartLoc;
    public boolean mIsDone;

    public ResultInfo (Location startLoc) {
        mIsRouteValid = true;
        mActLowEstimate = mBestLowEstimate = -1.0f;
        mActHighEstimate = mBestHighEstimate = -1.0f;
        mActSurgeMultiplier = mBestSurgeMultiplier = 0.0f;
        mBestStartLoc = startLoc;
        mIsDone = false;
    }

    public void updateWithActual(float actLowEstimate, float actHighEstimate, float actSurgeMultiplier) {
        mActLowEstimate = actLowEstimate;
        mActHighEstimate = actHighEstimate;
        mActSurgeMultiplier = actSurgeMultiplier;
    }

    public void updateWithActual(float actPrice, float actSurgeMultiplier) {
        updateWithActual(actPrice, actPrice, actSurgeMultiplier);
    }
    public void updateWithActual(float actPrice) {
        updateWithActual(actPrice, actPrice, 0.0f);
    }
    public void invalidateRoute() {
        mIsRouteValid = false;
    }

    public void markDone () {
        mIsDone = true;
    }
}
