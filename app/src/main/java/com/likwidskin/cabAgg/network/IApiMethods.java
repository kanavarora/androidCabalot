package com.likwidskin.cabAgg.network;

import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.Callback;

/**
 * Created by kanavarora on 3/21/16.
 */
public interface IApiMethods {
    @GET("/api/v1/lyft")
    void getLyftResponse(
            @Query("startLat") double lat,
            @Query("startLon") double lng,
            @Query("endLat") double endLat,
            @Query("endLon") double endLon,
            @Query("bestDynPricing") double bestDynPricing,
            Callback<LyftResponse> cb
    );
}
