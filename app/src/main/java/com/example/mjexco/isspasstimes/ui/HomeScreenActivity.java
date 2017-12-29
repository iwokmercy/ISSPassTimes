package com.example.mjexco.isspasstimes.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mjexco.isspasstimes.R;
import com.example.mjexco.isspasstimes.adapters.IssPassTimesListAdapet;
import com.example.mjexco.isspasstimes.helpers.DataHelper;
import com.example.mjexco.isspasstimes.objects.Response;

import java.util.List;

public class HomeScreenActivity extends AppCompatActivity  implements LocationListener {
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1000;
    private static final int PERMISSIONS_REQUEST_INTERNET = 1001;
    private Location userLocation = null;
    private DataHelper dataHelper;

    private TextView status;
    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        status = findViewById(R.id.status_text);
        list = findViewById(R.id.pass_times_list);
        list.setLayoutManager(new LinearLayoutManager(this));

        //check user permission
        checkLocationPermission();
        //initialize data helper
        dataHelper = new DataHelper();
    }

    /*
    Method that checks if the user has provided Location permission.
    If permission has not been granted, it will be requested
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //request user permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            //permission already granted, now check internet permission
            checkInternetPermission();
        }
    }

    /*
    Method that checks if the user has provided Internet permission.
    If permission has not been granted, it will be requested
     */
    private void checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            //request user permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST_INTERNET);
        } else {
            //permission already granted, call method to request user location
            retrieveUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                boolean locationPermissionGranted = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(locationPermissionGranted){
                    //check internet permission is granted
                    checkInternetPermission();
                }
                break;
            }
            case PERMISSIONS_REQUEST_INTERNET: {
                boolean internetPermissionGranted = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(internetPermissionGranted){
                    //call method to request user location
                    retrieveUserLocation();
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //todo retrieve pass times
        if (location != null) {
            if(userLocation == null){
                //first retrieval so set location and request times
                userLocation = location;
                //use datahelper class to make service call to get pass times
                //also set datahelper listener to receive data
                dataHelper.getIssPassTimes(location.getLatitude(),
                        location.getLongitude(), new DataHelper.DataHelperListener() {
                            @Override
                            public void onDataRetrieved(List<Response> responseList) {
                                if(responseList != null){
                                    //call method to display retrieved data
                                    showList(responseList);
                                } else {
                                    //call method to show error message
                                    showError();
                                }
                            }

                            @Override
                            public void onDataFailed() {
                                //call method to show error message
                                showError();
                            }
                        });
            }
        } else {
            //call method to show error message
            showError();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    /*
    Uses Location Manager to request device current location
     */
    @SuppressLint("MissingPermission")
    private void retrieveUserLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            showError();
        }
    }

    /**
     * Displays the retrieved pass times on the screen
     * @param passTimesList list of pass times returned for user's location
     */
    private void showList(List<Response> passTimesList) {
        list.setAdapter(new IssPassTimesListAdapet(passTimesList, R.layout.recycler_view_item));
        status.setText(R.string.success_status_text);
        list.setVisibility(View.VISIBLE);
    }

    /*
    Displays error message for user
     */
    private void showError(){
        status.setText(R.string.error_status_text);
        list.setVisibility(View.GONE);
    }
}
