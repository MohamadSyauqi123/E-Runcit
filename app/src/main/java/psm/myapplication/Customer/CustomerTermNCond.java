package psm.myapplication.Customer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import psm.myapplication.R;

public class CustomerTermNCond extends AppCompatActivity {

    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_term_ncond);

        back = findViewById(R.id.back_more);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerTermNCond.super.onBackPressed();
            }
        });




    }
}
