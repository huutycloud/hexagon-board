package hexagon.board;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String DEBUGTAG = "Debug Tag";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng HAMBURG = new LatLng(53.558, 9.927);

    private GoogleMap mMap;
    private DatabaseReference mFirebaseDatabase;
    private Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;
    private LocationRequest mLocationRequest;

    private CalculateDistance calculator;
    private double distance;
    private double currentLat;
    private double currentLng;
    private double pinLat;
    private double pinLng;
    private LatLng currentLatLng;
    private LatLng pinLatLng;
    private MarkerOptions currentLocationPin;
    private MarkerOptions pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        Log.d(TAG, "Building Play services client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        mLocationPermissionGranted = getUserPermission();
        if (mLocationPermissionGranted)
            getDeviceLocation();
        else
            useDefaultLocation();
    }

    private boolean getUserPermission() {
        if (mMap == null) {
            return false;
        }

        /*
         * Pop-up requesting for user's location, depending of user's permission
         * application will follow suit.
         */

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        return mLocationPermissionGranted;
    }

    private void useDefaultLocation() {
        mMap.addMarker(new MarkerOptions().position(HAMBURG).title("Default"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 200));
    }

    private void getDeviceLocation() {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (currentLocation == null) {
            Log.d(DEBUGTAG, "currentLocation == null");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            showPOI(currentLocation);
        } else {
            Log.d(DEBUGTAG, "currentLocation != null");
            handleNewLocation(currentLocation);
            showPOI(currentLocation);
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
        currentLatLng = new LatLng(currentLat, currentLng);
        currentLocationPin = new MarkerOptions()
                .position(currentLatLng)
                .title("Bạn đang đứng ở đây!");
        mMap.addMarker(currentLocationPin);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 200));
    }

    private void showPOI(final Location location) {
        Log.d(TAG, "Doing showPOI");
        calculator = new CalculateDistance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("alerts");
        mFirebaseDatabase.orderByChild("latLng");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot value : dataSnapshot.getChildren()) {
                    pinLat = value.child("latLng").child("latitude").getValue(Double.class);
                    pinLng = value.child("latLng").child("longitude").getValue(Double.class);

                    distance = calculator.distance(
                            pinLat,
                            pinLng,
                            location.getLatitude(),
                            location.getLongitude());
                    Log.d(TAG, "distance from des to src : " + distance);
                    if (distance <= 5) {
                        pinLatLng = new LatLng(pinLat, pinLng);
                        pin = new MarkerOptions()
                                .position(pinLatLng)
                                .title(value.child("subject").getValue().toString());
                        mMap.addMarker(pin);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(currentLocation);
    }
}
