package com.likwidskin.cabAgg;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final float ZOOM_FACTOR = 2.5f;
    private static final double METERS_PER_MILE = 1609.344;

    private GoogleMap mMap;
    private MainViewStep step;
    private LocationManager mLocationManager;
    private Location mCurrentUserLocation;
    private Location pickupLocation;
    private Location destinationLocation;
    private Marker pickupMarker;
    private Marker destinationMarker;

    // view outlets
    private RelativeLayout bottomBar;
    private Button actionButton;
    private ImageButton myLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MyMapFragment mapFragment = (MyMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.step = MainViewStep.MainViewStepSetPickup;

        // initialize views
        this.bottomBar = (RelativeLayout) findViewById(R.id.bottomBar);
        this.actionButton = (Button) findViewById(R.id.actionButton);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonClicked();
            }
        });
        this.myLocationButton = (ImageButton) findViewById(R.id.myLocationButton);
        this.myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentUserLocation == null) {
                    return;
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLng(Utility.latLngFromLocation(mCurrentUserLocation)));
            }
        });


        // setup stuff
        this.setupActionButton();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            startActivity(new Intent(this, ShareActivity.class));
            return true;
        }
        if (id == R.id.action_faq) {
            startActivity(new Intent(this, FAQActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(37.8, -122.4);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker on Sydney"));

        mMap.setPadding(0, 0, 0, Utility.dpToPx(getApplicationContext(), 110));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

    }

    private void removeMarkerIfNeeded(Marker m) {
        if (m != null) {
            m.remove();
        }
    }

    // type 0 - for pickup , else destination
    private void initMarker (Location loc, int type) {
        BitmapDescriptor des = BitmapDescriptorFactory.defaultMarker();

        LatLng latLng = Utility.latLngFromLocation(loc);
        if (type == 0) {
            if (pickupMarker == null) {
                pickupMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(120.0f)));
            } else {
                pickupMarker.setPosition(latLng);
            }
        } else {
            if (destinationMarker == null) {
                destinationMarker = mMap.addMarker(new MarkerOptions()
                        .position(Utility.latLngFromLocation(loc))
                        .icon(BitmapDescriptorFactory.defaultMarker(0.0f)));
            } else {
                destinationMarker.setPosition(latLng);
            }
        }
    }

    private void updateMarkers() {
        switch (this.step) {
            case MainViewStepSetPickup:
            {
                removeMarkerIfNeeded(pickupMarker);
                removeMarkerIfNeeded(destinationMarker);
                break;
            }
            case MainViewStepSetDest:
            {
                initMarker(pickupLocation, 0);
                removeMarkerIfNeeded(destinationMarker);
                break;
            }
            case MainViewStepSetOptimize:
            {
                initMarker(pickupLocation, 0);
                initMarker(destinationLocation, 1);
                break;
            }
        }
    }

    private void updatePickupLocation(Location pickupLocation) {
        this.step = MainViewStep.MainViewStepSetDest;
        this.pickupLocation = pickupLocation;
        setupActionButton();
        updateMarkers();
    }

    private void updateDestinationLocation(Location destinationLocation) {
        this.step = MainViewStep.MainViewStepSetOptimize;
        this.destinationLocation = destinationLocation;
        setupActionButton();
        updateMarkers();
    }

    // region Camera Move
    protected void centerMapOnLocation(Location loc) {
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        CameraPosition cameraPosition = new CameraPosition(latLng, ZOOM_FACTOR,0,0);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    protected void centerMapToIncludeLocations(Location loc1, Location loc2) {
        double northLat = Math.min(loc1.getLatitude(), loc2.getLatitude());
        double southLat = Math.max(loc1.getLatitude(), loc2.getLatitude());
        double eastLng = Math.max(loc1.getLongitude(), loc2.getLongitude());
        double westLng = Math.min(loc1.getLongitude(), loc2.getLongitude());
        LatLng ne = new LatLng(northLat,eastLng);
        LatLng sw = new LatLng(southLat, westLng);
        LatLngBounds latLngBounds = new LatLngBounds(sw, ne);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 20));
    }
    // endregion

    // region LocationListener
    @Override
    public void onLocationChanged(Location location) {
        mCurrentUserLocation = location;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    // endregion

    protected void setupActionButton() {
        switch (this.step) {
            case MainViewStepSetPickup:
            {
                this.actionButton.setText(R.string.action_button_set_pickup);
                this.actionButton.setBackgroundColor
                        (getResourceColor(R.color.actionButtonPickupColor));
                this.actionButton.setTextColor(
                        getResourceColor(R.color.actionButtonTextPickupColor));
                break;
            }
            case MainViewStepSetDest:
            {
                this.actionButton.setText(R.string.action_button_set_destination);
                this.actionButton.setBackgroundColor
                        (getResourceColor(R.color.actionButtonDestinationColor));
                this.actionButton.setTextColor(
                        getResourceColor(R.color.actionButtonTextDestinationColor));
                break;
            }
            case MainViewStepSetOptimize:
            {
                this.actionButton.setText(R.string.action_button_optimize);
                this.actionButton.setBackgroundColor
                        (getResourceColor(R.color.actionButtonOptimize));
                this.actionButton.setTextColor(
                        getResourceColor(R.color.actionButtonTextOptimizeColor));
                break;
            }
        }
    }

    private void actionButtonClicked () {
        Location mapLocation = centeredLocation();
        switch (step) {
            case MainViewStepSetPickup: {
                updatePickupLocation(mapLocation);
                break;
            }
            case MainViewStepSetDest: {
                updateDestinationLocation(mapLocation);
                break;
            }
            case MainViewStepSetOptimize: {
                break;
            }
        }
    }

    protected int getResourceColor(int id) {
        return ContextCompat.getColor(this, id);
    }

    protected Location centeredLocation () {
        return Utility.locationFromLatLng(mMap.getCameraPosition().target);
    }
}
