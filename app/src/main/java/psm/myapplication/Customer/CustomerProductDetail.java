package psm.myapplication.Customer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import psm.myapplication.Product;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerProductDetail extends AppCompatActivity {


    TextView productquantity_cust;
    int totalQuantity = 1;
    double totalPrice ;


    ImageView productimage_cust;
    TextView productname_cust,productprice_cust,productunit_cust,productdesc_cust;
    Button addtocart_custbttn;
    ImageButton addbttun,removebttn,back;
    String mart_user,customer_ID;
    Product product = new Product();
    User user = new User();

    FirebaseAuth auth;
    FirebaseFirestore db;

    DocumentReference documentReference;
    RecyclerView productrecycleView;
    ArrayList<Product> productArrayList;

    boolean itemAda = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_detail);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();



        productimage_cust = findViewById(R.id.productimage_Cust);
        productdesc_cust = findViewById(R.id.productDesc_Cust);
        productname_cust= findViewById(R.id.productName_Cust);
        productprice_cust = findViewById(R.id.productPrice_Cust);
        productunit_cust = findViewById(R.id.productUnit_Cust);
        productquantity_cust = findViewById(R.id.productQuantity_Cust);

        addtocart_custbttn = findViewById(R.id.addtocart_bttn);
        addbttun = findViewById(R.id.addcart_bttn);
        removebttn = findViewById(R.id.removecart_bttn);
        back = findViewById(R.id.backtoproductlist);


        product =(Product) getIntent().getSerializableExtra("product");

        displayProduct(product);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerProductDetail.super.onBackPressed();
            }
        });

        addbttun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (totalQuantity < 10){
                    totalQuantity++;
                    productquantity_cust.setText(String.valueOf(totalQuantity));
                    if (itemAda){
                        documentReference.update("productQuantity",String.valueOf(totalQuantity));
                    }

                }

            }
        });
        removebttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e( "onClick: ",String.valueOf(totalQuantity) );
                if (totalQuantity > 1){ Log.e( "onClick: ",String.valueOf(totalQuantity) );
                    totalQuantity--;
                    productquantity_cust.setText(String.valueOf(totalQuantity));

                    if (itemAda){
                        documentReference.update("productQuantity",String.valueOf(totalQuantity));
                    }
                }


            }
        });




        addtocart_custbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemAda){
                    addtocart_custbttn.setText("Add To Cart");
                    documentReference.delete();
                    itemAda=false ;
                }else {
                    addtocart_custbttn.setText("Remove From Cart");
                    addtocart();
                }


            }
        });

        checkItem();

    }

    private void displayProduct(Product product) {

        String store_ID;

        Picasso.get().load(product.getProductURL()).into(productimage_cust);
        productdesc_cust.setText(product.getProductTitle());
        productname_cust.setText(product.getProductName());
        productprice_cust.setText(product.getProductPrice());
        productunit_cust.setText(product.getProductUnit());



    }

    private void checkItem(){



        FirebaseFirestore.getInstance().collection("AddToCart")
                .whereEqualTo("userID",auth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                if (queryDocumentSnapshots.getDocuments().size() != 0){

                    queryDocumentSnapshots.getDocuments().get(0).getReference().collection("ProductList").whereEqualTo("productID",product.getProductID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            itemAda=queryDocumentSnapshots.size() != 0 ;

                            if (queryDocumentSnapshots.size() != 0 ){

                                addtocart_custbttn.setText("Remove From Cart");

                                productquantity_cust.setText(queryDocumentSnapshots.getDocuments().get(0).getString("productQuantity"));
                                totalQuantity=Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).getString("productQuantity"));
                                documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                            }else {
                                addtocart_custbttn.setText("Add To Cart");
                            }


                        }
                    });
                }

            }
        });
    }





    private void addtocart() {

        Calendar calforDate = Calendar.getInstance();

        final HashMap<String,Object> cartMap = new HashMap<>();
        final HashMap<String,Object> orderNap = new HashMap<>();

        double jarak3 = (double) getIntent().getSerializableExtra("jarak");

        orderNap.put("userID",auth.getCurrentUser().getUid());
        orderNap.put("orderStatus","AddToCart");
        orderNap.put("storeID",product.getUser_ID());
        orderNap.put("storeDist",jarak3);

        cartMap.put("productName",productname_cust.getText().toString());
        cartMap.put("productPrice",productprice_cust.getText().toString());
        cartMap.put("productQuantity",productquantity_cust.getText().toString());
        cartMap.put("productTotalPrice",String.valueOf(Double.parseDouble(productprice_cust.getText().toString())*Integer.parseInt(productquantity_cust.getText().toString())));
        cartMap.put("productUrl",product.getProductURL());
        cartMap.put("productID",product.getProductID());
        cartMap.put("productUnit",product.getProductUnit());




        db.collection("AddToCart")
                .whereEqualTo("userID",auth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        db.collection("AddToCart")
                                .add(orderNap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {


                                if (task.isSuccessful()){
                                    DocumentReference documentReference= task.getResult();
                                    documentReference.collection("ProductList")
                                            .add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Toast.makeText(CustomerProductDetail.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                            checkItem();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else{
                        for (QueryDocumentSnapshot document: task.getResult()){
                            document.getReference().collection("ProductList").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                    Toast.makeText(CustomerProductDetail.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                    checkItem();

                                }
                            });
                        }
                    }
                }
            }
        });
    }


}

