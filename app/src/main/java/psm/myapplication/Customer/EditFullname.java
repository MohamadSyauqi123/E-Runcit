package psm.myapplication.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import psm.myapplication.R;

public class EditFullname extends AppCompatActivity {

    private ImageButton back;
    private Button save;
    EditText fullnamecustomer;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fullname);

        back = findViewById(R.id.backtoeditProfile);
        save = findViewById(R.id.saveFullname);
        fullnamecustomer = findViewById(R.id.fullnameCustomer);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CustomerProfile.class));
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updatefullname();


            }
        });



    }

    private void updatefullname() {

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("Users")
                .document(uid)
                .update("userFullname",fullnamecustomer.getEditableText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(EditFullname.this, "Profile is updated.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),CustomerProfile.class));
                    }
                });

    }
}