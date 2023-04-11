package psm.myapplication.Mart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import psm.myapplication.R;

public class MartAddress extends AppCompatActivity {

    ImageButton back;
    Button savenewaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mart_address);

        savenewaddress= findViewById(R.id.savenewAddress);
    back= findViewById(R.id.backtodash);

    back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    });


    savenewaddress.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(),MartAddressDetail.class));
        }
    });



    }

}