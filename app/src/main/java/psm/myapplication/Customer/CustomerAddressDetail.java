package psm.myapplication.Customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import psm.myapplication.R;

public class CustomerAddressDetail extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {
    TextInputEditText address,remark;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int GPS_REQUEST_CODE = 9001;
    boolean isPermissionGranted;
    private FusedLocationProviderClient mlocationClient;
    GoogleMap mGooglemap;
    MarkerOptions markerOptions = new MarkerOptions();

    ImageButton back;
    Button update;




    List<MarkerOptions> markerOptionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_address_detail);




        back = findViewById(R.id.backtoadd);
        address = findViewById(R.id.custAddress);
        update = findViewById(R.id.updateAddress);
        remark = findViewById(R.id.remarkAddress);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerAddressDetail.super.onBackPressed();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertAddressData();
                Intent i = new Intent(CustomerAddressDetail.this,CustomerAddress.class);
                startActivity(i);

            }
        });

        getUserAddress();

        checkMyPermission();
        intMap();
    }



    private void getUserAddress() {

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){

                        address.setText(documentSnapshot.getData().get("userAddress").toString());
                        remark.setText(documentSnapshot.getData().get("userRemark").toString());

                    }else {


                        Toast.makeText(CustomerAddressDetail.this,"Please enter the info",Toast.LENGTH_SHORT).show();

                    }
                }else{

                }

            }
        });
    }


    private void checkMyPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(CustomerAddressDetail.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
    public void onInfoWindowClick(@NonNull Marker marker) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mGooglemap = googleMap;
        mGooglemap.setMyLocationEnabled(true);

        mGooglemap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                //set Marker Position
                markerOptions.position(latLng);
                //set Latitude and Longitude On Marker
//                markerOptions.title(latLng.latitude,latLng.longitude);
                //Clear prevous click option
                mGooglemap.clear();
                //zoom marker
                mGooglemap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
                //Add marker on map
                mGooglemap.addMarker(markerOptions);


            }
        });






    }

    private void insertAddressData() {


        String uid = mAuth.getCurrentUser().getUid();

        db.collection("Users")
                .document(uid)
                .update("userAddress",address.getEditableText().toString(),"userRemark",remark.getEditableText().toString(),"longitude", markerOptions.getPosition().latitude,"latitude",markerOptions.getPosition().longitude)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(CustomerAddressDetail.this, "Address is updated.", Toast.LENGTH_SHORT).show();

                    }
                });

    }



}