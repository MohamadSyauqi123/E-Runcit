package psm.myapplication.Customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import psm.myapplication.LoginActivity;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener, LocationListener {

    static final float END_SCALE = 0.7f;
    ImageButton menu, addtocartbbtn;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout contentView;
    TextView textnoti ,userName;
    Button logout;

    //Shop List view
    RecyclerView shopRecycleView;
    ArrayList<User> shopModelArrayList;
    shopViewAdapter shopViewAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    NotificationBadge badge;
    CardView cardView;

    ImageView martImage;

    int AddTocartCount =10 ;


    private FusedLocationProviderClient mlocationClient;
    boolean isPermissionGranted;
    private int GPS_REQUEST_CODE = 9001;
    GoogleMap mGooglemap;
    MarkerOptions markerOptions = new MarkerOptions();

    Map<Marker,String> markerdata =  new HashMap<>();
    LocationManager locationManager;
    Location mylocation ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        menu = findViewById(R.id.menu_button);
        addtocartbbtn = findViewById(R.id.addtocart_bttn);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.content);
        logout = findViewById(R.id.custLogout);
        textnoti = findViewById(R.id.text_noti1);
        cardView =   findViewById(R.id.cardcart);
        userName =  findViewById(R.id.myName);


        checkMyPermission();
       // intMap();
        mlocationClient = new FusedLocationProviderClient(this);

        if (isPermissionGranted){

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            supportMapFragment.getMapAsync((OnMapReadyCallback) this);

        }
        FirebaseFirestore.getInstance()
                .collection("AddToCart")
                .whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size() != 0){
                    task.getResult().getDocuments().get(0).getReference().collection("ProductList").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            if (value.getDocuments().size()!= 0){

                                cardView.setVisibility(View.VISIBLE);
                                textnoti.setText(String.valueOf( value.getDocuments().size()));

                            }
                            else{

                                cardView.setVisibility(View.INVISIBLE);

                            }
                        }
                    });
                }

            }
        });

        addtocart();
        navigationDrawer();
        //EventShopListener();
        shopViewAdapter = new shopViewAdapter(shopModelArrayList, new shopViewAdapter.OnItemClickListener(){

                    @Override
                    public void onItemClick(User user) {
//                        Log.e( "onItemClick: ",user.getUserID() );
                        Intent intent = new Intent(CustomerDashboard.this,CustomerViewShopItem.class);
                        intent.putExtra("user",user);
                        startActivity(intent);
                    }
                }, CustomerDashboard.this);
    }


    private void intMap() {
        if (isPermissionGranted){

            if (isGPSenable()){

                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                supportMapFragment.getMapAsync(this);
            }
        }
    }

    private void checkMyPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(CustomerDashboard.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package",getPackageName(),"");
//                intent.setData(uri);
//                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

//                permissionToken.continuePermissionRequest();
            }
        }).check();
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


    private void addtocart() {

        addtocartbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDashboard.this,CustomerCart.class);
                startActivity(intent);

            }
        });
    }


    private void EventShopListener() {



        //shopRecycleView = findViewById(R.id.shoprecycleview);
        shopRecycleView.setLayoutManager(new LinearLayoutManager(this));
        shopRecycleView.setHasFixedSize(true);
        shopModelArrayList= new ArrayList<>();

        shopRecycleView.setAdapter(shopViewAdapter);

        db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("usertype", "Mart")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> shopView = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d:shopView){
                    User shop = d.toObject(User.class);
                    shopModelArrayList.add(shop);
                }
                shopViewAdapter.notifyDataSetChanged();
            }
        });

    }




    private void navigationDrawer() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.profile_nav);
        View v = navigationView.getHeaderView(0);
        TextView userName = (TextView ) v.findViewById(R.id.myName);
        db.collection("Users").whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        String nama =task.getResult().getDocuments().get(0).get("userFullname").toString();
                        Log.e(nama, "onComplete: " );
                        userName.setText(nama);
                    }
                });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navigationView.getContext();



                }
                else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        animateNavigationDrawer();


    }

    private void animateNavigationDrawer() {

        drawerLayout.setScrimColor(getResources().getColor(R.color.cardview_dark_background));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                final float diffScaleOffset = slideOffset *(1-END_SCALE);
                final float offsetScale = 1 - diffScaleOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);


                final float xOffset = drawerView.getWidth()*slideOffset;
                final float xOffsetDiff = contentView.getWidth()*diffScaleOffset/2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }



        });



    }

    @Override
    public void onBackPressed(){

        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        MenuItem profile = navigationView.getMenu().findItem(R.id.profilecust_nav);
        if (item.equals(profile)){
            startActivity(new Intent(getApplicationContext(),CustomerProfile.class));

        }

        MenuItem order = navigationView.getMenu().findItem(R.id.ordercust_nav);
        if (item.equals(order)){
            startActivity(new Intent(getApplicationContext(),CustomerOrder.class));

        }


        MenuItem address = navigationView.getMenu().findItem(R.id.addresscust_nav);
        if (item.equals(address)){
            startActivity(new Intent(getApplicationContext(),CustomerAddress.class));

        }


        MenuItem settings = navigationView.getMenu().findItem(R.id.custSetting);
        if (item.equals(settings)){
            startActivity(new Intent(getApplicationContext(),CustomerSettings.class));

        }


        MenuItem term = navigationView.getMenu().findItem(R.id.termNconCust);
        if (item.equals(term)){
            startActivity(new Intent(getApplicationContext(),CustomerTermNCond.class));

        }


        MenuItem logout = navigationView.getMenu().findItem(R.id.custLogout);
        if (item.equals(logout)){
            logout();
        }





        return true;
    }



    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Logout");

        builder.setMessage("Are you sure want to Logout?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerDashboard.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent );
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.show();
    }



    public void redirectActivity(Activity activity,Class aClass) {

        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(intent);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {


        mGooglemap = googleMap;
        mGooglemap.setMyLocationEnabled(true);

        displayMarker();



    }

    private void displayMarker() {



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

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {



    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

        mylocation = location;
        db.collection("Users")
                .whereEqualTo("usertype","Mart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LatLng location = new LatLng(document.getDouble("longitude"),document.getDouble("latitude"));

//                                Map<String, Object> pin =  document.getData();
                                String name = document.getString("storeName");
                                String storeId = document.getId();

//                                Log.e(String.valueOf(location.latitude),String.valueOf(location.longitude));

                                DecimalFormat df = new DecimalFormat();
                                df.setMaximumFractionDigits(3);

                                float[] results = new float[1];

                                double distance = SphericalUtil.computeDistanceBetween(new LatLng(location.latitude,location.longitude), new LatLng(mylocation.getLatitude(),mylocation.getLongitude()));
                                Location.distanceBetween(location.latitude,location.longitude,mylocation.getLatitude(),mylocation.getLongitude(),results);

                                MarkerOptions markerOptions = new MarkerOptions().position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_buildingmarker_latest_foreground))
                                        .snippet(df.format(distance/1000) + "km")
                                        .title(name);
                                Marker marker = mGooglemap.addMarker(markerOptions);
                                markerdata.put(marker,storeId);

                                mGooglemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(@NonNull Marker marker) {

                                        double jarak = distance/1000;
                                        Log.e(String.valueOf(jarak), "onMarkerClick: " );


                                        mGooglemap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                            @Override
                                            public void onInfoWindowClick(@NonNull Marker marker) {

                                                Log.e( "onInfoWindowClick: ", storeId);
                                                Intent i = new Intent(CustomerDashboard.this, CustomerViewShopItem.class);
                                                i.putExtra("ID_kedai", markerdata.get(marker));
                                                i.putExtra("jarak",jarak);
                                                startActivity(i);

                                            }
                                        });
                                        return false;
                                    }
                                });

                            }
                        } else {

                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}