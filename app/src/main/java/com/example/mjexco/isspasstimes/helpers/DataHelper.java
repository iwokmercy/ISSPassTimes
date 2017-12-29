package com.example.mjexco.isspasstimes.helpers;

import android.util.Log;

import com.example.mjexco.isspasstimes.apis.RestClient;
import com.example.mjexco.isspasstimes.apis.RestInterface;
import com.example.mjexco.isspasstimes.objects.IssPassTimesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Helper class that retrieves ISS pass times for given location and returns the list to the view for update
 */

public class DataHelper {
    private List<com.example.mjexco.isspasstimes.objects.Response> responseList = null;

    //Interface to be implemented by view to facilitate communication with Helper
    public interface DataHelperListener {
        void onDataRetrieved(List<com.example.mjexco.isspasstimes.objects.Response> responseList);
        void onDataFailed();
    }

    //listener that will be used to return data to the view
    private DataHelperListener listener;

    /**
     * Makes the service call to retrieve ISS pass times for given location
     * @param latitude location latitiude
     * @param longitude location longitude
     * @param helperListener listener used to communicate with view
     */
    public void getIssPassTimes(double latitude, double longitude, DataHelperListener helperListener) {
        listener = helperListener;
        RestInterface apiService = RestClient.getClient().create(RestInterface.class);
        Call<IssPassTimesResponse> call = apiService.getIssPassTimes(latitude, longitude);

        call.enqueue(new Callback<IssPassTimesResponse>() {
            @Override
            public void onResponse(Call<IssPassTimesResponse>call, retrofit2.Response<IssPassTimesResponse> response) {
                responseList = response.body().getResponse();
                Log.e("Service Returned", "" + responseList.size() + " times for this location");
                //pass response to the view
                listener.onDataRetrieved(responseList);
            }

            @Override
            public void onFailure(Call<IssPassTimesResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e("Service Error", t.toString());
                listener.onDataFailed();
            }
        });
    }
}
