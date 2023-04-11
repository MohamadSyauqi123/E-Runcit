package psm.myapplication.Mart;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import psm.myapplication.Product;
import psm.myapplication.R;
import psm.myapplication.User;

public class AddStoreProduct extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    User user = new User();
    Product product = new Product();
    private TextInputEditText product_name,product_title,product_price;
    private ImageButton back;
    private Button savebttn;
    private ImageView photo_product;
    private Spinner product_category, product_unit;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private static final int  CAMERA_REQUEST_CODE = 111;
    String uid = mAuth.getCurrentUser().getUid();
    String currentPhotoPath;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store_product);

        product_name = findViewById(R.id.productname);
        product_title = findViewById(R.id.producttitle);
        product_category = (Spinner) findViewById(R.id.productcategory);
        product_unit = (Spinner) findViewById(R.id.productunit);
        product_price = findViewById(R.id.productprice);
        savebttn = findViewById(R.id.saveproduct);
        photo_product = findViewById(R.id.productphoto);
        back = findViewById(R.id.backbuttn);

        ArrayList<Map<String,String>> wishList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();

        photo_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        savebttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                insertproductData();
                Intent intent = new Intent(AddStoreProduct.this,MartDashboard.class);
                startActivity(intent);


            }
        });


        product_category= (Spinner) findViewById(R.id.productcategory);//fetch the spinner from layout file
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.categories));//setting the country_array to spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        product_category.setAdapter(adapter);
        product_category.setPrompt("Select your Categories");

        product_category.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this));

        //if you want to set any action you can do in this listener
        product_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        product_unit= (Spinner) findViewById(R.id.productunit);//fetch the spinner from layout file
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.unit));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        product_unit.setAdapter(adapter);
        product_unit.setPrompt("Select your Quantity");
        product_unit.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.unit_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this));

        //if you want to set any action you can do in this listener
        product_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




    }

    private void choosecategory() {

        Spinner spinner = findViewById(R.id.productcategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



    }

    private void insertproductData()  {

        String productname = product_name.getEditableText().toString();
        String producttitle = product_title.getEditableText().toString();
        String productunit = product_unit.getSelectedItem().toString();
        String productprice = product_price.getEditableText().toString();
        String productcategory = product_category.getSelectedItem().toString();


        String path = String.valueOf(System.nanoTime());

        if (mImageUri != null){
            StorageReference fileReference = storage.getReference(productname + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            String url = task.getResult().toString();


                                            product.setUser_ID(mAuth.getCurrentUser().getUid());
                                            product.setProductName(productname);
                                            product.setProductTitle(producttitle);
                                            product.setProductCategories(productcategory);
                                            product.setProductUnit(productunit);
                                            product.setProductPrice(productprice);
                                            product.setProductURL(url);


                                            db.collection("Product").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                    Toast.makeText(AddStoreProduct.this,"Product Added Successfully",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(AddStoreProduct.this,MartDashboard.class);
                                                    startActivity(intent);



                                                }
                                            });

                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddStoreProduct.this, "Product add failed!!", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }


    private void openFileChooser() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddStoreProduct.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (ContextCompat.checkSelfPermission(AddStoreProduct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                            //Take Permission
                            ActivityCompat.requestPermissions(AddStoreProduct.this, new String[]{Manifest.permission.CAMERA},  CAMERA_REQUEST_CODE);

                        } else {
                            Toast.makeText(AddStoreProduct.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
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
                    }
                });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private String getFileExtension(Uri mImageUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(mImageUri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case  CAMERA_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    File f = new File(currentPhotoPath);
                    photo_product.setImageURI(Uri.fromFile(f));
                    mImageUri = Uri.fromFile(f);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    mImageUri = data.getData();
                    Picasso.get().load(mImageUri).into(photo_product);
                }
                break;
        }
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
        String imageFileName = product_name + ".";
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void backToDisplayMyResepi(View view) {
        Intent intent = new Intent(AddStoreProduct.this ,MartDashboard.class);
        startActivity(intent);

    }
}