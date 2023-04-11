package psm.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import psm.myapplication.Customer.CustomerDashboard;
import psm.myapplication.Mart.MartDashboard;

public class MainActivity extends AppCompatActivity {

    Button start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore.getInstance().collection("Users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    if (Objects.equals(documentSnapshot.getString("usertype"), "Customer")){

                        startActivity(new Intent(MainActivity.this, CustomerDashboard.class));
                    }else{
                        startActivity(new Intent(MainActivity.this, MartDashboard.class));
                    }
                });

        }
        else{
            (   (ProgressBar) findViewById(R.id.progressbar)).setVisibility(View.INVISIBLE);
            (   (LinearLayout) findViewById(R.id.frontlyt)).setVisibility(View.VISIBLE);
        }


        start_button = findViewById(R.id.getstart);
        start_button.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }


}