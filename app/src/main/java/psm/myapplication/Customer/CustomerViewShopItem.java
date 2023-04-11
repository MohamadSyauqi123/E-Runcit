package psm.myapplication.Customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import psm.myapplication.Product;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerViewShopItem extends AppCompatActivity {


    RecyclerView customeritemView;
    CustomerViewShopItemAdapter.RecyclerViewClickListener productListener;
    ArrayList<Product> productModelArrayList;

    CustomerViewShopItemAdapter customerViewShopItemAdapter;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    TextView textnoti,mart_name;
    CardView cardView;
    ImageButton addtocartbbtn,back;

    Product product = new Product();
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_view_shop_item);

        double jarak1 = (double) getIntent().getSerializableExtra("jarak");
        Log.e(String.valueOf(jarak1), "Jarak kedai: " );

        CartView();
        addtocart();
        EventCustCategoryItemViewListener();

        customerViewShopItemAdapter = new CustomerViewShopItemAdapter(productModelArrayList, new CustomerViewShopItemAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Product product) {

                        Intent intent =  new Intent(CustomerViewShopItem.this,CustomerProductDetail.class);
                        intent.putExtra("product",product);
                        intent.putExtra("jarak",jarak1);
                        startActivity(intent);
                    }
                }, CustomerViewShopItem.this);

        customeritemView.setAdapter(customerViewShopItemAdapter);

        back = findViewById(R.id.backtoshop);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerViewShopItem.super.onBackPressed();
            }
        });

//        String nama  = (String) getIntent().getSerializableExtra("namaKedai");
//        mart_name.setText(nama);
        mart_name= findViewById(R.id.shopname_Cust);

        displayMart();

    }

    private void CartView() {

        textnoti = findViewById(R.id.text_noti1);
        cardView =   findViewById(R.id.cardcart);

        FirebaseFirestore.getInstance().collection("AddToCart").whereEqualTo("userID",fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size() != 0){
                    task.getResult().getDocuments().get(0).getReference().collection("ProductList").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

//                            Log.e( "onSuccess: ",String.valueOf(value.getDocuments().size()) );
                            if (value.getDocuments().size()!= 0){
//                                Log.e( "onSuccess: ","                                tgetDocuments().size()));" );
                                cardView.setVisibility(View.VISIBLE);
                                textnoti.setText(String.valueOf( value.getDocuments().size()));
                            }
                            else{
//                                Log.e( "onSuccess: ","                                textnoti.setText(String.valueOf( queryDocumentSnapshots.getDocuments().size()));" );
                                cardView.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }

            }
        });

    }
    private void addtocart() {


        double jarak1 = (double) getIntent().getSerializableExtra("jarak");
        Log.e(String.valueOf(jarak1), "Jarak kedai: " );

        addtocartbbtn = findViewById(R.id.addtocart_bttn);
        addtocartbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerViewShopItem.this,CustomerCart.class);
                intent.putExtra("jarak2",jarak1);
                startActivity(intent);

            }
        });
    }


    private void displayMart() {


        String storeID  = getIntent().getSerializableExtra("ID_kedai").toString();

        db.collection("Users")
                .whereEqualTo("userID",storeID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentSnapshots = task.getResult();
                if (task.isSuccessful()){

                    mart_name.setText((documentSnapshots.getDocuments().get(0).get("storeName").toString()));

                }
            }
        });

    }


    private void EventCustCategoryItemViewListener() {

        customeritemView = findViewById(R.id.customeritemshopview_);
        customeritemView.setLayoutManager(new LinearLayoutManager(this));
        customeritemView.setHasFixedSize(true);
        productModelArrayList=new ArrayList<Product>();

        customeritemView.setAdapter(customerViewShopItemAdapter);


        Intent i = getIntent();
        String storeID = (String) i.getSerializableExtra("ID_kedai");

        db = FirebaseFirestore.getInstance();


        db.collection("Product")
                .whereEqualTo("user_ID",storeID)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> itemView = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d:itemView){

                    Product item = d.toObject(Product.class);
                    item.setProductID(d.getId());
                    productModelArrayList.add(item);

                }
                customerViewShopItemAdapter.notifyDataSetChanged();
            }
        });

        return;
    }


}