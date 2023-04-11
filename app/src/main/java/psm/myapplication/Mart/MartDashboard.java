package psm.myapplication.Mart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import psm.myapplication.LoginActivity;
import psm.myapplication.Mart.Fragment.ItemFragment;
import psm.myapplication.Mart.Fragment.orderFragment;
import psm.myapplication.R;

public class MartDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static final float END_SCALE=0.7f;
    DrawerLayout drawerLayout_mart;
    NavigationView navigationView;
    ImageButton martmenu_bttn;
    LinearLayout contentView;
    ImageView martprofile;
    TextView martName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mart_dashboard);

        drawerLayout_mart = findViewById(R.id.drawerlayout_mart);
        navigationView = findViewById(R.id.martnavigationview);

        martmenu_bttn = findViewById(R.id.martmenu);
        contentView = findViewById(R.id.content);
        martprofile = findViewById(R.id.martphoto);
        martName = findViewById(R.id.martNama);

        db.collection("Users")
                        .whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        QuerySnapshot queryDocumentSnapshots = task.getResult();

                        String mart =  queryDocumentSnapshots.getDocuments().get(0).get("storeName").toString();
                        Log.e(mart, "onComplete: " );
                        //martName.setText(mart);

                    }
                });

        martProfile();
        navigationDrawemart();


        BottomNavigationView bottomNavigationView = findViewById(R.id.dashboard_navi);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ItemFragment()).commit();


    }

    private void martProfile() {

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){

                        Picasso.get().load(documentSnapshot.getData().get("imageProfile").toString()).into(martprofile);

                    }else {

                    }
                }else{

                }

            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedfFragment = null;

                    switch ((item.getItemId())) {
                        case R.id.bttn_item:
                            selectedfFragment = new ItemFragment();
                            break;
                        case R.id.bttn_order:
                            selectedfFragment =new orderFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedfFragment).commit();
                    return true;
                }
            };

    private void navigationDrawemart() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.profile_nav);
        View v = navigationView.getHeaderView(0);
        TextView userName = (TextView ) v.findViewById(R.id.martNama);
        db.collection("Users").whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        String nama =task.getResult().getDocuments().get(0).get("storeName").toString();
                        Log.e(nama, "onComplete: " );
                        userName.setText(nama);
                    }
                });
        martmenu_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout_mart.isDrawerVisible(GravityCompat.START)){
                    drawerLayout_mart.closeDrawer(GravityCompat.START);
                }
                else {
                    drawerLayout_mart.openDrawer(GravityCompat.START);
                }
            }
        });

        animateNavigationDrawerMart();
    }

    private void animateNavigationDrawerMart() {

        drawerLayout_mart.setScrimColor(getResources().getColor(R.color.black));
        drawerLayout_mart.addDrawerListener(new DrawerLayout.DrawerListener() {
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
    public void onBackPressed() {

        if (drawerLayout_mart.isDrawerVisible(GravityCompat.START)){
            drawerLayout_mart.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

        super.onBackPressed();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



            MenuItem profile = navigationView.getMenu().findItem(R.id.profile_nav);
            if (item.equals(profile)){
                startActivity(new Intent(getApplicationContext(), MartProfile.class));

            }

            MenuItem order = navigationView.getMenu().findItem(R.id.order_nav);
            if (item.equals(order)){
                startActivity(new Intent(getApplicationContext(), MartOrder.class));

            }


            MenuItem address = navigationView.getMenu().findItem(R.id.address_nav);
            if (item.equals(address)){
                startActivity(new Intent(getApplicationContext(), MartAddressDetail.class));

            }


            MenuItem settings = navigationView.getMenu().findItem(R.id.martSetting);
            if (item.equals(settings)){
                startActivity(new Intent(getApplicationContext(), MartSettings.class));

            }


            MenuItem term = navigationView.getMenu().findItem(R.id.trmNconMart);
            if (item.equals(term)){
                startActivity(new Intent(getApplicationContext(), MartTermNCon.class));

            }


            MenuItem logout = navigationView.getMenu().findItem(R.id.martLogout);
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
                Intent intent = new Intent(MartDashboard.this, LoginActivity.class);
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




}

