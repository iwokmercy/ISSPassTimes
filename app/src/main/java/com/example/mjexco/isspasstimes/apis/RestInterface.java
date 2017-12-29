package com.example.mjexco.isspasstimes.apis;

import com.example.mjexco.isspasstimes.objects.IssPassTimesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestInterface {
    @GET("/iss-pass.json")
    Call<IssPassTimesResponse> getIssPassTimes(@Query("lat") double latitude, @Query("lon") double longitude);
}
