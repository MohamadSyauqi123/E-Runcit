package psm.myapplication.Mart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;

import psm.myapplication.R;
import psm.myapplication.User;

public class MartAddressDetail extends AppCompatActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    boolean isPermissionGranted;
    GoogleMap mGooglemap;
    private FusedLocationProviderClient mlocationClient;
    private int GPS_REQUEST_CODE = 9001;
    ImageButton back;
    Button save;
    TextInputEditText address;
    TextInputLayout storeAddress;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    Marker marker;

    User user = new User();
    String uid = fAuth.getCurrentUser().getUid();

    //Create Marker
    MarkerOptions markerOptions = new MarkerOptions();
    private Map<Marker, Map<String, Object>> markeractivity = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mart_address_detail);




        storeAddress = findViewById(R.id.storeAddress);
        address = findViewById(R.id.martAddress);
        save = findViewById(R.id.saveAddress);
        back = findViewById(R.id.backtoadd);


        getStoreAddress();



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertAddressData();
            }
        });

        checkMyPermission();
        intMap();

        mlocationClient = new FusedLocationProviderClient(this);



        if (isPermissionGranted){

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            supportMapFragment.getMapAsync(this);

        }
        

    }

    private void getStoreAddress() {

        String uid = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){

                        address.setText(documentSnapshot.getData().get("userAddress").toString());

                    }else {


                        Toast.makeText(MartAddressDetail.this,"Please enter the info",Toast.LENGTH_SHORT).show();

                    }
                }else{

                }

            }
        });

    }

    private void displayMarker() {


        db.collection("Users")
                .whereEqualTo("usertype","Mart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                Map<String, Object> pin =  document.getData();
                                LatLng location = new LatLng(document.getDouble("longitude"),document.getDouble("latitude"));
                                String name = document.getString("storeName");

                                Log.e(String.valueOf(location.latitude),String.valueOf(location.longitude));

                                mGooglemap.addMarker(new MarkerOptions().position(location)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                        .title(name));


//                                markeractivity.put(marker, pin);

                            }
                        } else {

                        }
                    }
                });


    }

    private void insertAddressData() {


        String uid = fAuth.getCurrentUser().getUid();

        db.collection("Users")
                .document(uid)
                .update("userAddress",address.getEditableText().toString(),"longitude", markerOptions.getPosition().latitude,"latitude",markerOptions.getPosition().longitude)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(MartAddressDetail.this, "Address is updated.", Toast.LENGTH_SHORT).show();

                    }
                });





    }

    private void checkMyPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MartAddressDetail.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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

//    @SuppressLint("MissingPermission")
//    private void getCurrLock() {
//
//        mlocationClient.getLastLocation().addOnCompleteListener(task -> {
//
//            if (task.isSuccessful()){
//                Location location = task.getResult();
//                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                gotoLocation(location.getLatitude(),location.getLongitude());
//
//            }
//        });
//
//
//
//    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 18);
        mGooglemap.moveCamera(cameraUpdate);
        mGooglemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {



        mGooglemap = googleMap;
        mGooglemap.setMyLocationEnabled(true);

        displayMarker();



        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();
                    Map<String, Object> pin =  documentSnapshot.getData();
                    LatLng location = new LatLng(documentSnapshot.getDouble("longitude"),documentSnapshot.getDouble("latitude"));


                    mGooglemap.addMarker(new MarkerOptions().position(location));
                    mGooglemap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                    //set market lat and long

                    Log.e("Nama", documentSnapshot.get("storeName").toString() );

                }
            }
        });





        mGooglemap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                //set Marker Position
                markerOptions.position(latLng);
                //set Latitude and Longitude On Marker
//                markerOptions.title("");
                //Clear prevous click option
//                mGooglemap.clear();
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

                Toast.makeText(this,"GPS is not Enable",Toast.LENGTH_SHORT).show();
            }
        }
    }



}