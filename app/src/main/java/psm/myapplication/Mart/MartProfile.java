package psm.myapplication.Mart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import psm.myapplication.R;

public class MartProfile extends AppCompatActivity {

    TextView storeName,storeownerName,storePassword,storeContact,storeEmail;
    Button editstorename,editownername,editstorecontact,editstorepass;
    ImageButton backtodashboard;
    ImageView profilepic;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String uid = mAuth.getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mart_profile);


        storeName = findViewById(R.id.storename);
        storeEmail = findViewById(R.id.storeemail);
        storeownerName = findViewById(R.id.storeownername);
        storeContact = findViewById(R.id.martcontact);
        storePassword = findViewById(R.id.storepassword);
        backtodashboard = findViewById(R.id.backtodash);
        editownername = findViewById(R.id.editmartownername);
        editstorename = findViewById(R.id.editstorename);
        editstorecontact = findViewById(R.id.editstorecontact);
        editstorepass = findViewById(R.id.editstorepass);
        profilepic = findViewById(R.id.profilepic);

        viewstoreprfile();


        backtodashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MartDashboard.class));



            }
        });

        editstorename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), EditStoreName.class));
            }
        });


        editownername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), EditStoreOwnerName.class));
            }
        });


        editstorecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditStoreContact.class));
            }
        });

        editstorepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), editStorePassword.class));
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MartProfile.this, "Tunggu sat", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void viewstoreprfile() {

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){

                        storeName.setText(documentSnapshot.getData().get("storeName").toString());
                        storeownerName.setText(documentSnapshot.getData().get("ownerStoreName").toString());
                        storePassword.setText(documentSnapshot.getData().get("password").toString());
                        storeContact.setText(documentSnapshot.getData().get("userContact").toString());
                        storeEmail.setText(documentSnapshot.getData().get("email").toString());
                        Picasso.get().load(documentSnapshot.getData().get("imageProfile").toString()).into(profilepic);

                    }else {

                    }
                }else{

                }

            }
        });
    }
}