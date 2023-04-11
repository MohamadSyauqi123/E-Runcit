package psm.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import psm.myapplication.Customer.CustomerDashboard;
import psm.myapplication.Mart.MartDashboard;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private Button mloginbtn,msignup;
    private EditText mpassword,memail;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    TextView textView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mloginbtn = findViewById(R.id.loginbtn);
        msignup = findViewById(R.id.signup);
        mpassword = findViewById(R.id.password);
        memail = findViewById(R.id.email);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = memail.getText().toString();
                String password = mpassword.getText().toString();

                textView=(TextView)findViewById(R.id.username_);
                textView=(TextView)findViewById(R.id.email);

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"Please fill in the blank.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Please fill in the blank.",Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            db.collection("Users").document(task.getResult().getUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.get("usertype").equals("Mart")){
                                        startActivity(new Intent(LoginActivity.this, MartDashboard.class));
                                        Toast.makeText(LoginActivity.this, "Account successfully login.", Toast.LENGTH_LONG).show();

                                    } if (documentSnapshot.get("usertype").equals("Customer")){
                                        startActivity(new Intent(LoginActivity.this, CustomerDashboard.class));
                                        Toast.makeText(LoginActivity.this, "Account successfully login.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            Toast.makeText(LoginActivity.this, "Account successfully login.", Toast.LENGTH_LONG).show();

                        } else{

                            Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this,LoginActivity.class));

                        }
                    }
                });
            }
        });

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });




    }
}