package psm.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MyMapsPermission extends AppCompatActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermissionGranted;
    GoogleMap mGooglemap;

    FloatingActionButton button;
    private FusedLocationProviderClient mlocationClient;
    private int GPS_REQUEST_CODE = 9001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_maps_permission);

        checkMyPermission();
        button = findViewById(R.id.button);
        
        intMap();

        mlocationClient = new FusedLocationProviderClient(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrLock();
            }
        });

        if (isPermissionGranted){

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            supportMapFragment.getMapAsync(this);

        }

    }

    private void intMap() {
        if (isPermissionGranted){

            if (isGPSenable()){

                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                supportMapFragment.getMapAsync(this);
            }
        }
    }

    private boolean isGPSenable(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnable){
            return true;
        }else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required app to work. Please enable GPS")
                    .setPositiveButton("Yes", (((dialog, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent,GPS_REQUEST_CODE);
                    })))
                    .setCancelable(false)
                    .show();

        }

        return false;
    }


    @SuppressLint("MissingPermission")
    private void getCurrLock() {

        mlocationClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                Location location = task.getResult();
                gotoLocation(location.getLatitude(),location.getLongitude());

            }
        });



    }

    private void gotoLocation(double latitude, double longitude) {

        LatLng latLng = new LatLng(latitude,longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,13);
        mGooglemap.moveCamera(cameraUpdate);
        mGooglemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }



    private void checkMyPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MyMapsPermission.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGooglemap = googleMap;
        mGooglemap.setMyLocationEnabled(true);

        LatLng utemLocation = new LatLng(2.200844, 102.240143);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(utemLocation)      // Sets the center of the map to utem
                .zoom(12)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));




        mGooglemap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                //Create Marker
                MarkerOptions markerOptions = new MarkerOptions();
                //set Marker Position
                markerOptions.position(latLng);
                //set Latitude and Longitude On Marker
                markerOptions.title(latLng.latitude+ ":" + latLng.longitude);
                //Clear prevous click option
                mGooglemap.clear();
                //zoom marker
                mGooglemap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                //Add marker on map
                mGooglemap.addMarker(markerOptions);
            }
        });

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnable){
                Toast.makeText(this,"GPS is enable ",Toast.LENGTH_SHORT).show();

            }else {

                Toast.makeText(this,"GPS is not Enanle",Toast.LENGTH_SHORT).show();
            }
        }
    }
}