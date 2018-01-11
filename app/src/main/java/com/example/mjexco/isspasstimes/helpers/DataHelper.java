package com.example.mjexco.isspasstimes.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.mjexco.isspasstimes.backend.RestClient;
import com.example.mjexco.isspasstimes.backend.RestInterface;
import com.example.mjexco.isspasstimes.objects.IssPassTimesResponse;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Helper class that retrieves ISS pass times for given location and returns the list to the view for update
 * It also helps the view check user permissions and returns the result for further action
 */

public class DataHelper {
    private static final int PERMISSIONS_REQUEST = 1000;
    private List<com.example.mjexco.isspasstimes.objects.Response> responseList = null;

    //Interface to be implemented by view to facilitate communication with Helper
    public interface DataHelperListener {
        //used to pass retrieved data to view
        void onDataRetrieved(List<com.example.mjexco.isspasstimes.objects.Response> responseList);
        //used to notify view that data retrieval failed
        void onDataFailed();
        //used to notify view that user permissions have been granted
        void onPermissionsGranted();
        //used to notify view that user permissions have been denied
        void onPermissionsDenied();
    }

    //listener that will be used to return data to the view
    private DataHelperListener listener;

    /**
     * Makes the service call to retrieve ISS pass times for given location
     * @param latitude location latitiude
     * @param longitude location longitude
     */
    public void getIssPassTimes(double latitude, double longitude) {
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

    /**
     * Used by view to set listener for communication
     * @param dataHelperListener listener passed by view
     */
    public void setListener(DataHelperListener dataHelperListener){
        listener = dataHelperListener;
    }

    public DataHelperListener getListener(){
        return listener;
    }

    /**
     * Checks if user has granted necessary permissions. If permissions have not been
     * granted, they will be requested
     * @param context Activity context
     */
    public void checkPermissions(Context context){
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)) {

            //request user permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST);
        } else {
            //notify view that permission has been granted
            listener.onPermissionsGranted();
        }
    }

    /*
    Handles permissions request results
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                boolean locationPermissionGranted = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(locationPermissionGranted){
                    //notify view that permission has been granted
                    listener.onPermissionsGranted();
                } else {
                    //notify view that permission has been denied
                    listener.onPermissionsDenied();
                }
                break;
            }
        }
    }

    /**
     * Retrieves the address of the given location
     * @param context application context
     * @param latitude location latitude
     * @param longitude location longitude
     * @return location address
     */
    public String getAddressFromLocation(@NonNull Context context, @NonNull double latitude, @NonNull double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        String address = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i <= fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                }

                address = strAddress.toString();

            } else {
                address = "Searching Current Address...";
            }

        } catch (IOException e) {
            e.printStackTrace();
            address = "Unable to retrieve address";
        }
        return address;
    }

}
