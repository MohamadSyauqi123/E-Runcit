package psm.myapplication.Customer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerProfile extends AppCompatActivity {

    public static final String TAG = "TAG";

    private static final int  CAMERA_REQUEST_CODE = 111;

    private ImageButton editFullname,editpassword,editcontact,backtodashboard,profilepicture;
    TextView fullname,useremail, userpassword,userphone;
    ImageView userimage;

    User user = new User();
    String uid = user.getUserID();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    String currentPhotoPath;

    Button editemail,saveprofile;
    private Uri mImageUri;


    private FirebaseStorage storage = FirebaseStorage.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        editFullname = findViewById(R.id.editfullname_button);
        editpassword = findViewById(R.id.editpassword_button);
        editcontact = findViewById(R.id.editcontact_button);
        backtodashboard = findViewById(R.id.backtohome);
        profilepicture = findViewById(R.id.edit_profilepic);
        saveprofile = findViewById(R.id.saveImage);

        fullname = findViewById(R.id.fullnameProfile);
        useremail = findViewById(R.id.userEmail);
        userpassword = findViewById(R.id.passwordProfile);
        userphone = findViewById(R.id.contactProfile);
        userimage = findViewById(R.id.user_image);

        getuserProfile();

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFileChooser();

            }
        });

        saveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImageProfile();

            }
        });

        backtodashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });



        editFullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),EditFullname.class));

            }
        });

        editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),EditPassword.class));
            }
        });

        editcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EditContact.class));
            }
        });

    }


    private void updateImageProfile()  {


        if (mImageUri != null){
            StorageReference fileReference = storage.getReference(uid + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            String url = task.getResult().toString();

//                                            db.collection("Product").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                @Override
//                                                public void onSuccess(DocumentReference documentReference) {
//
//                                                    Toast.makeText(AddStoreProduct.this,"Product is Added",Toast.LENGTH_SHORT).show();
//                                                    Intent intent = new Intent(AddStoreProduct.this, MartDashboard.class);
//                                                    startActivity(intent);
//
//                                                }
//                                            });

                                            db.collection("Users").document(uid)
                                                    .update("imageProfile",url);

                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CustomerProfile.this, "Profile updated failed!!", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void getuserProfile() {

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){

                        fullname.setText(documentSnapshot.getData().get("userFullname").toString());
                        useremail.setText(documentSnapshot.getData().get("email").toString());
                        userpassword.setText(documentSnapshot.getData().get("password").toString());
                        userphone.setText(documentSnapshot.getData().get("userContact").toString());
                        Picasso.get().load(documentSnapshot.getData().get("imageProfile").toString()).into(userimage);

                    }else {

                    }
                }else{

                }

            }
        });


    }


    private Object getFileExtension(Uri mImageUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(mImageUri));
    }


    private void openFileChooser() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CustomerProfile.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (ContextCompat.checkSelfPermission(CustomerProfile.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    //Take Permission
                    ActivityCompat.requestPermissions(CustomerProfile.this, new String[]{Manifest.permission.CAMERA},  CAMERA_REQUEST_CODE);

                } else {
                    Toast.makeText(CustomerProfile.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();
                }
            }
        })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent photoPickerIntent = new Intent();
                        photoPickerIntent.setType("image/*");
                        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(photoPickerIntent, 1);
                        Picasso.get().load(mImageUri).into(userimage);
                    }
                });
        final AlertDialog alert = dialog.create();
        alert.show();
    }


    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("TAG",ex.getMessage() );

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws  IOException{
        // Create an image file name
        String imageFileName = user.getUsername() + ".";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "imageFileName",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


}