package psm.myapplication.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import psm.myapplication.AddToCart;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerCart extends AppCompatActivity {

    RecyclerView addtocartView;

    TextView item_name,item_price,item_quantity;
    ImageView item_image;
    ImageButton back,removeitem;
    Button checkout;

    double serviceCharge=0,tax=0,total=0,totalpay=0;
    int itemtotal = 0;

    CustomerCartAdapter customerCartAdapter;
    ArrayList<AddToCart> cartArrayList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    AddToCart product = new AddToCart();
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

//        double jarak2 = (double) getIntent().getSerializableExtra("jarak2");
//        Log.e(String.valueOf(jarak2), "onCreate: " );

        back = findViewById(R.id.button_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerCart.super.onBackPressed();
            }
        });

        customerCartAdapter = new CustomerCartAdapter(cartArrayList);

        EventAddTocartListener();

        checkout = findViewById(R.id.checkoutitem);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(getApplicationContext(), CustomerPayment.class);
                intent.putExtra("cart",totalpay);
                startActivity(intent);
            }
        });




    }



    private void EventAddTocartListener() {

        addtocartView = findViewById(R.id.itemaddtocartRecycleview);
        addtocartView.setLayoutManager(new LinearLayoutManager(this));
        addtocartView.setHasFixedSize(true);


        addtocartView.setAdapter(customerCartAdapter);

        db = FirebaseFirestore.getInstance();



        db.collection("AddToCart")
                .whereEqualTo("userID",mAuth.getCurrentUser().getUid()).whereEqualTo("orderStatus","AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().size() != 0){
                        task.getResult().getDocuments().get(0).getReference().collection("ProductList").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {



                                cartArrayList.clear();
                                List<DocumentSnapshot> productView = value.getDocuments();


                                //tambah ntuk remove
                                serviceCharge = 0;
                                tax = 0;
                                totalpay = 0;
                                total = 0;


                                itemtotal = productView.size();

                                for (DocumentSnapshot d : productView) {


                                    Log.e("onSuccess: ", d.getData().toString());
                                    AddToCart product = d.toObject(AddToCart.class);
                                    product.setProductID(d.getId());
                                    product.setDocumentReference(d.getReference());
                                    cartArrayList.add(product);
                                    total += Double.parseDouble(product.getProductQuantity())* Double.parseDouble(product.getProductPrice());

                                }

                                serviceCharge = total * 0.06;
                                tax = total * 0.06;
                                totalpay = serviceCharge + tax + total;

                                ((TextView) findViewById(R.id.totalitemprice)).setText(String.format("%,.2f",total));
//                                ((TextView) findViewById(R.id.deliveryservice)).setText(String.format("%,.2f", serviceCharge));
                                ((TextView) findViewById(R.id.tax)).setText(String.format("%,.2f", tax));
                                ((TextView) findViewById(R.id.totalPayment)).setText(String.format("%,.2f", totalpay));

                                customerCartAdapter.notifyDataSetChanged();
                            }

                        });
                    }

                }
            }
        });

    }
}