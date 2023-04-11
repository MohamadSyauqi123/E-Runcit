package psm.myapplication.Mart;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import psm.myapplication.Order;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerOrderItem extends Activity {

    RecyclerView orderDetailRecycleview;
    ArrayList<Map<String,Object>> customerProductDetailList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    CustomerOrderDetailAdapter customerOrderDetailAdapter;
    ImageButton back;

    Order order = new Order();
    User user = new User();
    TextView custname, contact, address,date, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_item);

        orderDetailRecycleview = findViewById(R.id.orderRecyleview);
        orderDetailRecycleview.setLayoutManager(new LinearLayoutManager(this));
        orderDetailRecycleview.setHasFixedSize(true);
        customerProductDetailList = new ArrayList<Map<String,Object>>();

        orderDetailRecycleview.setAdapter(customerOrderDetailAdapter);

        custname = findViewById(R.id.customerName);
        contact = findViewById(R.id.customerContact);
        address =  findViewById(R.id.customerAddress);
        date = findViewById(R.id.orderDate);
        time = findViewById(R.id.orderTime);


        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        EvenCustOrderDetailList();

       //order = (Order) getIntent().getSerializableExtra("order");
       String id = (String) getIntent().getSerializableExtra("order");
       displayDetail(id);


    }

    private void displayDetail(String id) {

        db.collection("Users")
                .whereEqualTo("userID",id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        QuerySnapshot queryDocumentSnapshot = task.getResult();

                       String custN= (String) queryDocumentSnapshot.getDocuments().get(0).get("userFullname");
                       String contactC = (String) queryDocumentSnapshot.getDocuments().get(0).get("userContact");
                        String AddressC= (String) queryDocumentSnapshot.getDocuments().get(0).get("userAddress");
                        String masa1 = getIntent().getSerializableExtra("masa").toString();
                        String tarikh1 = getIntent().getSerializableExtra("tarikh").toString();

                        Log.e(custN, "onComplete: ");
                        custname.setText(custN);
                        contact.setText(contactC);
                        address.setText(AddressC);


                    }

                });
        String masa1 = getIntent().getSerializableExtra("masa").toString();
        String tarikh1 = getIntent().getSerializableExtra("tarikh").toString();
        date.setText(masa1);
        time.setText(tarikh1);



    }

    private void EvenCustOrderDetailList() {

        String orderID = getIntent().getSerializableExtra("id").toString();
        String custID = getIntent().getSerializableExtra("order").toString();

        db.collection("Order")
                .document(orderID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        customerProductDetailList = (ArrayList<Map<String,Object>>) documentSnapshot.get("Product");
                        customerOrderDetailAdapter = new CustomerOrderDetailAdapter(customerProductDetailList, new CustomerOrderDetailAdapter.OnItemClickListener() {
                            @Override
                            public void OnItemClick(Order orderItem) {

                            }
                        });
                        orderDetailRecycleview.setAdapter(customerOrderDetailAdapter);
                        customerOrderDetailAdapter.notifyDataSetChanged();


                    }

                });




    }
}
