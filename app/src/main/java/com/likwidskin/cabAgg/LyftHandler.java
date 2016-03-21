package com.likwidskin.cabAgg;

import android.location.Location;

import com.likwidskin.cabAgg.network.IApiMethods;
import com.likwidskin.cabAgg.network.LyftResponse;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by kanavarora on 3/21/16.
 */
public class LyftHandler {

    private final String API_URL = "https://golden-context-82.appspot.com/";

    private IApiMethods mHttpMethods;
    private RestAdapter mRestAdapter;

    public Location mStart;
    public Location mEnd;

    public ResultInfo mLyftLineResult;
    public ResultInfo mLyftResult;


    public boolean mIsDone;

    private float mStartDisNeighbor;


    public LyftHandler() {
        // initialize network
        // TODO: Make it a singleton
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        mHttpMethods = mRestAdapter.create(IApiMethods.class);
    }


    private void getActual() {
        Callback<LyftResponse> cb = new Callback<LyftResponse>() {
            @Override
            public void success(LyftResponse lyftResponse, Response response) {
                if (!lyftResponse.isLyftLineRouteValid) {
                    mLyftLineResult.invalidateRoute();
                } else {
                    mLyftLineResult.updateWithActual(lyftResponse.getLyftLinePrice());
                }
                mLyftResult.updateWithActual(lyftResponse.getStandardPrice(), lyftResponse.getDynPricing());
                mLyftResult.markDone();
                mLyftLineResult.markDone();
            }

            @Override
            public void failure(RetrofitError error) {
                mLyftResult.markDone();
                mLyftLineResult.markDone();
            }
        };
        mHttpMethods.getLyftResponse(mStart.getLatitude(), mStart.getLongitude(),
                mEnd.getLatitude(), mEnd.getLongitude(), 0, cb);
    }

    public void optimize(Location start, Location end, float startDisNeighbor, float endDisNeigbor) {
        mIsDone = false;
        mStart = start;
        mEnd = end;
        mStartDisNeighbor = startDisNeighbor;
        mLyftLineResult = new ResultInfo(start);
        mLyftResult = new ResultInfo(start);
        getActual();
    }
}
