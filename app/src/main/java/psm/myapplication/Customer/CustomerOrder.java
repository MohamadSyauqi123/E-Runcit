package psm.myapplication.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import psm.myapplication.Order;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerOrder extends AppCompatActivity {

    RecyclerView orderRecycleview;

    ArrayList<Order> orderArrayList;
    CustomerOrderAdapter customerOrderAdapter ;

    ImageButton back;
    Button checkoutbttn;

    ArrayList<User> userArrayList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Order order = new Order();
    User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        getOrderlist();
        customerOrderAdapter = new CustomerOrderAdapter(orderArrayList, new CustomerOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order user) {

            }
        },CustomerOrder.this);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CustomerDashboard.class));
                //onBackPressed();

            }
        });


        orderRecycleview.setAdapter(customerOrderAdapter);


    }

    private void getOrderlist() {


        orderRecycleview = findViewById(R.id.orderrecycleView);
        orderRecycleview.setLayoutManager(new LinearLayoutManager(this));
        orderRecycleview.setHasFixedSize(true);
        orderArrayList = new ArrayList<>();

        db= FirebaseFirestore.getInstance();
        orderRecycleview.setAdapter(customerOrderAdapter);

        db.collection("Order")
                .whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> orderView = value.getDocuments();
                        orderArrayList.clear();
                        for (DocumentSnapshot a : orderView){

                            Order order = a.toObject(Order.class);
                            order.setOrder_id(a.getId());
                            orderArrayList.add(order);

                        }
                        customerOrderAdapter.notifyDataSetChanged();
                    }
                });
    }
}