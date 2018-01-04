package com.example.mjexco.isspasstimes.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mjexco.isspasstimes.R;
import com.example.mjexco.isspasstimes.adapters.IssPassTimesListAdapter;
import com.example.mjexco.isspasstimes.helpers.DataHelper;
import com.example.mjexco.isspasstimes.objects.Response;

import java.util.List;

public class HomeScreenActivity extends AppCompatActivity  implements LocationListener {
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

        DataHelper.DataHelperListener listener = new DataHelper.DataHelperListener() {
            @Override
            public void onDataRetrieved(List<Response> responseList) {
                if(responseList != null){
                    //call method to display retrieved data
                    showList(responseList);
                } else {
                    //call method to show error message
                    showError(R.string.error_status_text);
                }
            }

            @Override
            public void onDataFailed() {
                //call method to show error message
                showError(R.string.error_status_text);
            }

            @Override
            public void onPermissionsGranted() {
                //call method to retrieve user location
                status.setText(R.string.loading_status_text);
                retrieveUserLocation();
            }

            @Override
            public void onPermissionsDenied() {
                //call method to show error message
                showError(R.string.no_permission_status_text);
            }
        };

        //initialize data helper
        dataHelper = new DataHelper();
        dataHelper.setListener(listener);
    }

    @Override
    public void onPostResume(){
        //check user permission
        dataHelper.checkPermissions(this);
        super.onPostResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //send results to helper for processing
        dataHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(Location location) {
        //retrieve pass times
        if (location != null) {
            if(userLocation == null || userLocation != location){
                //first retrieval so set new location and request times
                userLocation = location;
                //use datahelper class to make service call to get pass times
                //also set datahelper listener to receive data
                dataHelper.getIssPassTimes(location.getLatitude(),
                        location.getLongitude());
            }
        } else {
            //call method to show error message
            showError(R.string.error_status_text);
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
            showError(R.string.error_status_text);
        }
    }

    /**
     * Displays the retrieved pass times on the screen
     * @param passTimesList list of pass times returned for user's location
     */
    private void showList(List<Response> passTimesList) {
        list.setAdapter(new IssPassTimesListAdapter(passTimesList, R.layout.recycler_view_item));
        status.setText(R.string.success_status_text);
        list.setVisibility(View.VISIBLE);
    }

    /*
    Displays error message for user
     */
    private void showError(int message){
        status.setText(message);
        list.setVisibility(View.GONE);
    }
}
