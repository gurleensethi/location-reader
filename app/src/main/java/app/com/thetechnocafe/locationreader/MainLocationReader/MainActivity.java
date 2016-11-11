package app.com.thetechnocafe.locationreader.MainLocationReader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import app.com.thetechnocafe.locationreader.Adapters.StoredLocationsAdapter;
import app.com.thetechnocafe.locationreader.R;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        MVPContracts.IView {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private CoordinatorLayout mCoordinatorLayout;
    private Button mStoreLocationButton;
    private EditText mLocationNameEditText;
    private TextInputLayout mLocationNameTextInputLayout;
    private LinearLayout mBottomSheetView;
    private ImageButton mBottomSheetOpenImageButton;
    private BottomSheetBehavior mBottomSheet;
    private RecyclerView mStoredLocationRecyclerView;
    private StoredLocationsAdapter mStoredLocationsAdapter;
    private MVPContracts.IPresenter mIPresenter;
    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference to XML views
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mLatitudeText = (TextView) findViewById(R.id.latitude_text_view);
        mLongitudeText = (TextView) findViewById(R.id.longitude_text_view);
        mStoreLocationButton = (Button) findViewById(R.id.store_location_button);
        mLocationNameEditText = (EditText) findViewById(R.id.location_name_edit_text);
        mLocationNameTextInputLayout = (TextInputLayout) findViewById(R.id.location_name_text_input_layout);
        mBottomSheetView = (LinearLayout) findViewById(R.id.saved_locations_bottom_sheet);
        mBottomSheetOpenImageButton = (ImageButton) findViewById(R.id.bottom_sheet_up_image_button);
        mStoredLocationRecyclerView = (RecyclerView) findViewById(R.id.stored_locations_recycler_view);

        mBottomSheet = BottomSheetBehavior.from(mBottomSheetView);

        //Create the presenter
        mIPresenter = new Presenter(this);

        setUpGoogleApiClient();
        setUpLocationRequest();
        checkPermissions();

        //Request Data
        mIPresenter.getLocationData();
    }

    @Override
    public void setUpView() {
        setUpOnClickListeners();
    }

    //Set on click listeners
    private void setUpOnClickListeners() {
        mStoreLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if location name field is not empty
                //If empty, show error, else save
                if (mLocationNameEditText.getText().toString().equals("")) {
                    mLocationNameTextInputLayout.setErrorEnabled(true);
                    mLocationNameTextInputLayout.setError(getString(R.string.name_required));
                } else {
                    mLocationNameTextInputLayout.setErrorEnabled(false);
                    mIPresenter.addLocation(
                            mLocationNameEditText.getText().toString(),
                            mLatitudeText.getText().toString(),
                            mLongitudeText.getText().toString()
                    );
                }
            }
        });

        //Close open bottom sheet
        mBottomSheetOpenImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mBottomSheet.getState()) {
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        mBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    }
                }
            }
        });

        mBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        mBottomSheetOpenImageButton.setImageResource(R.drawable.ic_down_button);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mBottomSheetOpenImageButton.setImageResource(R.drawable.ic_up_button);
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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
        updateLocation(getLastKnownLocation());
    }

    //Get last known location
    private Location getLastKnownLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
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

    @Override
    public void onLocationAdded(Boolean isSuccessful) {
        String successMessage;

        if (isSuccessful) {
            successMessage = getString(R.string.location_added_success);
            mIPresenter.getLocationData();
        } else {
            successMessage = getString(R.string.location_added_unsuccess);
        }

        Snackbar.make(mCoordinatorLayout, successMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onDataReceived(List<LocationModel> list) {
        setUpOrRefreshRecyclerView(list);
    }

    //Set up of refresh existing recycler view on data change
    private void setUpOrRefreshRecyclerView(List<LocationModel> list) {
        if (mStoredLocationsAdapter == null) {
            mStoredLocationsAdapter = new StoredLocationsAdapter(getApplicationContext(), list);

            //Configure recycler view
            mStoredLocationRecyclerView.setAdapter(mStoredLocationsAdapter);
            mStoredLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } else {
            mStoredLocationsAdapter.updateList(list);
            mStoredLocationsAdapter.notifyDataSetChanged();
        }
    }
}
