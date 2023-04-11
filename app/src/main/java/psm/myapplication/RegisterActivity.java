package psm.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import psm.myapplication.Customer.CustomerProfile;
import psm.myapplication.Mart.MartProfile;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText username_register, email_register;
    private TextInputEditText password_register,confirmPassword_register;
    private Button cust_buttn , loginPage,mart_buttn;
    private FirebaseAuth fAuth;
    ProgressBar progressBar;
    private FirebaseFirestore fstore;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    TextView textView;
    String userID,usertype,StoreID;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username_register = findViewById(R.id.username_);
        email_register= findViewById(R.id.email_);
        password_register = findViewById(R.id.password);
        confirmPassword_register = findViewById(R.id.confirmpassword);
        cust_buttn = findViewById(R.id.registercustomer);
        loginPage = findViewById(R.id.loginpage1);
        mart_buttn = findViewById(R.id.registermart);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        //eventUserListener();

        /*

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

         */


        cust_buttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = username_register.getEditableText().toString().trim();
                String email = email_register.getEditableText().toString().trim();
                String password = password_register.getEditableText().toString().trim();
                String confirmPassword = confirmPassword_register.getEditableText().toString().trim();
                String address,poscod,city,imageUrl,contactNum;



                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this,"Please fill in the email.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this,"Please enter the username",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(RegisterActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.matches(emailPattern)){
                    email_register.setError("Fill the email correctly");
                    return;
                }
                if (password.length()< 6){
                    password_register.setError("Please enter password more 6 character");
                    return;
                }
                if (!confirmPassword.matches(password)){
                    confirmPassword_register.setError(("Please enter match password !"));
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "New account created successfully.", Toast.LENGTH_LONG).show();

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("Users").document(userID);
                            Map<String, Object> user = new HashMap<>();

                            user.put("userID",task.getResult().getUser().getUid());
                            user.put("email", email);
                            user.put("password", password);
                            user.put("username",username);
                            user.put("usertype", "Customer");
                            user.put("userFullname","Your fullname");
                            user.put("userAddress","Your Address");
                            user.put("userRemark","Remark...");
                            user.put("imageProfile","https://firebasestorage.googleapis.com/v0/b/e-runcit-7581d.appspot.com/o/202-2024792_user-profile-icon-png-download-fa-user-circle.png?alt=media&token=0d70fcd8-0b40-4c1e-a07d-9f0673836310");
                            user.put("userContact","01234567890");


                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess : user profile is created for " + userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), CustomerProfile.class));
                            finish();

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error!." + task.getException(), Toast.LENGTH_LONG).show();

                        }



                    }
                });

            }
        });


        mart_buttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = username_register.getEditableText().toString().trim();
                String email = email_register.getEditableText().toString().trim();
                String password = password_register.getEditableText().toString().trim();
                String confirmPassword = confirmPassword_register.getEditableText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this,"Please fill in the email.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this,"Please enter the username",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(RegisterActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.matches(emailPattern)){
                    email_register.setError("Fill the email correctly");
                    return;
                }
                if (password.length()< 6){
                    password_register.setError("Please enter password more 6 character");
                    return;
                }
                if (!confirmPassword.matches(password)){
                    confirmPassword_register.setError(("Please enter match password !"));
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "New account created successfully.", Toast.LENGTH_LONG).show();

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("Users").document(userID);
                            Map<String, Object> user = new HashMap<>();

                            user.put("userID",task.getResult().getUser().getUid());
                            user.put("email", email);
                            user.put("password", password);
                            user.put("username",username);
                            user.put("usertype", "Mart");
                            user.put("ownerStoreName","Your fullname");
                            user.put("storeName","");
                            user.put("userAddress","Your Address");
                            user.put("imageProfile","https://firebasestorage.googleapis.com/v0/b/e-runcit-7581d.appspot.com/o/202-2024792_user-profile-icon-png-download-fa-user-circle.png?alt=media&token=0d70fcd8-0b40-4c1e-a07d-9f0673836310");
                            user.put("userContact","01234567890");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess : user profile is created for " +userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MartProfile.class));
                            finish();

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error!." + task.getException(), Toast.LENGTH_LONG).show();

                        }



                    }
                });

            }
        });

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

            }
        });





    }


}