package psm.myapplication.Mart;

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

public class editStorePassword extends AppCompatActivity {

    ImageButton back;
    Button savepassword;
    EditText password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String uid = mAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store_password);

        back = findViewById(R.id.backtoeditProfile);
        savepassword = findViewById(R.id.save_password);
        password = findViewById(R.id.editpassword);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        savepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 updatestorePassword();

            }
        });
    }

    private void updatestorePassword() {

        db.collection("Users")
                .document(uid)
                .update("password",password.getEditableText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(editStorePassword.this, "Password is updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MartProfile.class));
                    }
                });
    }
}