package com.example.mjexco.isspasstimes.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mjexco.isspasstimes.R;
import com.example.mjexco.isspasstimes.adapters.IssPassTimesListAdapter;
import com.example.mjexco.isspasstimes.helpers.DataHelper;
import com.example.mjexco.isspasstimes.objects.Response;

import java.util.List;

public class HomeScreenActivity extends AppCompatActivity  implements LocationListener {
    private Location userLocation = null;
    private DataHelper dataHelper;

    private TextView status, address;
    private RecyclerView list;
    private FloatingActionButton refreshButton;
    private LinearLayout addressLayout;
    private RelativeLayout loadingLayout;
    private String userAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        status = findViewById(R.id.status_text);
        address = findViewById(R.id.address);
        list = findViewById(R.id.pass_times_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        addressLayout = findViewById(R.id.address_layout);
        loadingLayout = findViewById(R.id.loading_layout);
        loadingLayout.setVisibility(View.VISIBLE);

        refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        refreshButton.setVisibility(View.GONE);

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
                //status.setText(R.string.loading_status_text);
                loadingLayout.setVisibility(View.VISIBLE);
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
        refresh();
    }

    /*
    Reloads displayed data
     */
    private void refresh(){
        userLocation = null;
        status.setVisibility(View.GONE);
        dataHelper.checkPermissions(this);
        loadingLayout.setVisibility(View.VISIBLE);
        addressLayout.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        refreshButton.setVisibility(View.GONE);
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
            if(userLocation == null){
                //first retrieval so set new location and request times
                userLocation = location;
                //retrieve address from location
                userAddress = dataHelper.getAddressFromLocation(getApplicationContext(), userLocation.getLatitude(),
                        userLocation.getLongitude());
                //use datahelper class to make service call to get pass times
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
        //status.setText(R.string.success_status_text);
        status.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        address.setText(userAddress);
        addressLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        refreshButton.setVisibility(View.VISIBLE);
    }

    /*
    Displays error message for user
     */
    private void showError(int message){
        status.setText(message);
        status.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        addressLayout.setVisibility(View.GONE);
        refreshButton.setVisibility(View.VISIBLE);
    }
}
