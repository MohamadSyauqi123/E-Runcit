package psm.myapplication.Mart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import psm.myapplication.Order;
import psm.myapplication.R;

public class MartOrder extends AppCompatActivity {

    //orderlist view
    RecyclerView martorderRecycleview;

    ArrayList<Order> orderMartArrayList;
    MartOrderAdapter martOrderAdapter ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth Auth = FirebaseAuth.getInstance();

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mart_order);

        orderviewListener();
        martOrderAdapter = new MartOrderAdapter(orderMartArrayList, new MartOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order mart) {

                Intent intent = new Intent(MartOrder.this,CustomerOrderItem.class);
                startActivity(intent);

            }
        }, MartOrder.this);

        martorderRecycleview.setAdapter(martOrderAdapter);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    private void orderviewListener() {

        martorderRecycleview = findViewById(R.id.ordercustomerRecycleview);
        martorderRecycleview.setLayoutManager(new LinearLayoutManager(this));
        martorderRecycleview.setHasFixedSize(true);
        orderMartArrayList =new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        Auth = FirebaseAuth.getInstance();
        martorderRecycleview.setAdapter(martOrderAdapter);

        db.collection("Order")
                .whereEqualTo("storeID",Auth.getCurrentUser().getUid())
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .orderBy("orderTime", Query.Direction.DESCENDING)
//                .whereNotEqualTo("orderStatus","Order Preparing")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> orderCust= queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : orderCust){


                    Order order = d.toObject(Order.class);
                    order.setOrder_id(d.getId());
                    orderMartArrayList.add(order);
                }
                martOrderAdapter.notifyDataSetChanged();
            }
        });
    }
}