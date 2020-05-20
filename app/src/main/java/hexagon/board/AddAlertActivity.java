package hexagon.board;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;


public class AddAlertActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent mainIntent;

    private EditText etSubject;
    private EditText etContent;
    private EditText etLocation;
    private Button bCreateAlert;
    private Button bDiscardAlert;

    private FirebaseUser mUser;
    private DatabaseReference mFirebaseDatabase;

    private AlertPOJO alertPOJO;

    private AlertDialog.Builder builder;

    private String subject, content, location;
    private LatLng latLng;
    private Button btnPickLocation;
    int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String latitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);

        etSubject = (EditText) findViewById(R.id.etSubject);
        etContent = (EditText) findViewById(R.id.etContent);
        etLocation = (EditText) findViewById(R.id.etLocation);
        bCreateAlert = (Button) findViewById(R.id.bCreateAlert);
        bDiscardAlert = (Button) findViewById(R.id.bDiscardAlert);
        btnPickLocation = (Button) findViewById(R.id.btnPickLocation);
        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }
        });


        // Init Firebase Authentication
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Set onClickListener for all the button
        bCreateAlert.setOnClickListener(this);
        bDiscardAlert.setOnClickListener(this);

    }

    private void createNewAlert(){

        subject = etSubject.getText().toString();
        content = etContent.getText().toString();
        location = etLocation.getText().toString();

        // Check for user's input, whether it is validating any rules
        if(!validateForm(subject, content)){
            return;
        }

        saveNewAlert(mUser.getUid(), subject, content, location);
        backMain();

    }

    private void saveNewAlert(String uid, String subject, String content, String location){
//        latLng = convertAddressToLatLng(location);
        latLng = new LatLng(16.063527, 108.246575);
        alertPOJO = new AlertPOJO(uid, subject, content, latLng);
        mFirebaseDatabase = mFirebaseDatabase.child("alerts").push();
        mFirebaseDatabase.setValue(alertPOJO);
        //saveNewValidate(mFirebaseDatabase.getKey().toString());
    }

    private LatLng convertAddressToLatLng(String location) {
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(location, 5);
            if (address == null) {
                return null;
            }
            Address newLocation = address.get(0);
            newLocation.getLatitude();
            newLocation.getLongitude();

            p1 = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;

    }

    private boolean validateForm(String subject, String content){

        // Init assuming that the inputs are valid
        boolean valid = true;

        // Use if condition to check for the general rules
        if(TextUtils.isEmpty(subject) || TextUtils.isEmpty(content)){

            // Create/Init builder to show error
            builder = new AlertDialog.Builder(AddAlertActivity.this);
            builder.setMessage("Please enter all fields")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
            valid = false;
        }
        return valid;

    }

    private void backMain(){
        mainIntent = new Intent(AddAlertActivity.this, MainActivity.class);
        AddAlertActivity.this.startActivity(mainIntent);
    }


    // Override method for all the user's action
    @Override
    public void onClick(View v){
        int i =  v.getId();
        if(i == R.id.bCreateAlert)
            createNewAlert();
        if(i == R.id.bDiscardAlert)
            backMain();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                AddAlertActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                AddAlertActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                etLocation.setText(latitude + "," +longitude);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

