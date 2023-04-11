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

public class EditStoreContact extends AppCompatActivity {

    ImageButton back;
    EditText updateNumber;
    Button save;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String uid = mAuth.getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store_contact);

        back =findViewById(R.id.backtoprifole);
        updateNumber = findViewById(R.id.editcontact);
        save =findViewById(R.id.saveeditmartcontact_buttn);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatestoreContact();
            }
        });
    }

    private void updatestoreContact() {


        db.collection("Users")
                .document(uid)
                .update("userContact",updateNumber.getEditableText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(EditStoreContact.this, "Phone Number is updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MartProfile.class));
                    }
                });
    }
}