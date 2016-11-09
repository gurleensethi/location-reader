package app.com.thetechnocafe.locationreader.MainLocationReader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import app.com.thetechnocafe.locationreader.R;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private CoordinatorLayout mCoordinatorLayout;
    private static final int LOCATION_PERMISSION_REQUST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mLatitudeText = (TextView) findViewById(R.id.latitude_text_view);
        mLongitudeText = (TextView) findViewById(R.id.longitude_text_view);

        setUpOnClickListeners();
        setUpGoogleApiClient();
        setUpLocationRequest();
        checkPermissions();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Get last known location
        if (checkPermissions()) {
            updateWithLastKnownLocation();
            getLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(mCoordinatorLayout, getString(R.string.connection_error), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
        Log.d("OKOKOKO", "Location changed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Connect the Google Api Client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Disconnect GoogleApiClient if connected
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    //Set up the onClickListeners
    private void setUpOnClickListeners() {

    }

    //Configure the Google Api Client
    private void setUpGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Configure location request
    private void setUpLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //Update last known location
    private void updateWithLastKnownLocation() {
        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        updateLocation(lastKnownLocation);
    }

    //Update the UI with location
    private void updateLocation(Location location) {
        if (location != null) {
            mLatitudeText.setText(String.valueOf(location.getLatitude()));
            mLongitudeText.setText(String.valueOf(location.getLongitude()));
        }
    }

    //Listen to location updates
    private void getLocationUpdates() {
        //if (checkPermissions()) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //}
    }

    //Check for permissions
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUST_CODE) {
            if (permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateWithLastKnownLocation();
                } else {
                    //Ask for permissions again
                    checkPermissions();
                    getLocationUpdates();
                }
            }
        }
    }
}
