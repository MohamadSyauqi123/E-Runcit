package psm.myapplication.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import psm.myapplication.R;

public class CustomerAddress extends AppCompatActivity {


    Button newaddress;
    ImageButton back;
    TextView address,remark;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_address);

        newaddress = findViewById(R.id.saveAddress);
        back = findViewById(R.id.backtodash);
        address = findViewById(R.id.userAddress);
        remark = findViewById(R.id.remark);


        newaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CustomerAddressDetail.class));

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(CustomerAddress.this,CustomerDashboard.class);
                startActivity(i);
            }
        });

        getUserAddress();


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

//                        Toast.makeText(CustomerAddress.this,"Please enter the info",Toast.LENGTH_SHORT).show();

                    }
                }else{

                }

            }
        });
    }
}