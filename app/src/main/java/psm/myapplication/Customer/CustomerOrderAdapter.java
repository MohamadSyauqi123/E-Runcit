package psm.myapplication.Customer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import psm.myapplication.Order;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.CustomerOrderHolder> {


    ArrayList<Order> OrderArrayList;
    ArrayList<User> userArrayList;
    CustomerOrderAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final OnItemClickListener orderListlistener;
    Context context;



    public CustomerOrderAdapter(ArrayList<Order> orderArrayList, OnItemClickListener orderListlistener,Context context) {
        this.context = context;
        this.OrderArrayList = orderArrayList;
        this.userArrayList = userArrayList;
        this.mAuth = mAuth;
        this.orderListlistener = orderListlistener;

    }

    @NonNull
    @Override
    public CustomerOrderAdapter.CustomerOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_view,parent,false);
        return  new CustomerOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerOrderAdapter.CustomerOrderHolder holder, @SuppressLint("RecyclerView") int position) {

        Order product = OrderArrayList.get(position);

        db.collection("Order")
                .whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .orderBy("orderTime", Query.Direction.DESCENDING)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();

                        db.collection("Users")
                                .whereEqualTo("userID",queryDocumentSnapshots.getDocuments().get(0).get("storeID"))
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        Log.e((String) queryDocumentSnapshots.getDocuments().get(position).get("storeID"), "onComplete: " );
//                                        Log.e((String) String.valueOf(queryDocumentSnapshots.getDocuments().get(position).get(("totalPayment"))), "onComplete: " );
//                                        Log.e(task.getResult().getDocuments().get(0).get("storeName").toString(), "onComplete: " );
//                                        Log.e(task.getResult().getDocuments().get(0).get("profileImage").toString(), "image: ");

                                        Log.e(queryDocumentSnapshots.getDocuments().get(position).get("orderDate").toString(), "onComplete: " );

                                        holder.userName.setText(task.getResult().getDocuments().get(0).get("storeName").toString());
                                        holder.totalPay.setText(String.format("%,.2f",queryDocumentSnapshots.getDocuments().get(position).get(("totalPayment"))));
//                                        Picasso.get().load(userArrayList.get(position).getUserProfile_image()).into(holder.userImage);
                                        holder.orderDate.setText(queryDocumentSnapshots.getDocuments().get(position).get("orderDate").toString());
                                        holder.orderTine.setText(queryDocumentSnapshots.getDocuments().get(position).get("orderTime").toString());
                                        holder.order_status.setText(queryDocumentSnapshots.getDocuments().get(position).get("orderStatus").toString());



                                        String status = queryDocumentSnapshots.getDocuments().get(position).get("orderStatus").toString();
                                        if (status.equals("Order Preparing") || status.equals("Order Complete")){

                                            holder.complete.setVisibility(View.INVISIBLE);

                                        }else {

                                            holder.complete.setVisibility(View.VISIBLE);
                                            holder.complete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                    builder1.setTitle("Reminder");
                                                    builder1.setMessage("Order Complete ?");
                                                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            db.collection("Order")
                                                                    .whereEqualTo("storeID",OrderArrayList.get(position).getStoreID())
                                                                    .whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            QuerySnapshot querySnapshot = task.getResult();

                                                                            querySnapshot.getDocuments().get(0).getReference()
                                                                                    .update("orderStatus","Order Complete")
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {

                                                                                            Toast.makeText(context, "Order Complete", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Log.e(e.getMessage(), "onFailure: " );
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    });
                                                    builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    builder1.show();
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                });


        holder.bind(OrderArrayList.get(position),orderListlistener);


    }

    @Override
    public int getItemCount() {
       return OrderArrayList.size();
    }

    public class CustomerOrderHolder extends RecyclerView.ViewHolder{


        TextView userName,totalPay,orderDate,orderTine,order_status;
        ImageView userImage;
        Button complete;

        public CustomerOrderHolder(@NonNull View itemView) {
            super(itemView);

            FirebaseFirestore db= FirebaseFirestore.getInstance();
            userName = itemView.findViewById(R.id.user_name);
            totalPay = itemView.findViewById(R.id.total_payment);
            userImage = itemView.findViewById(R.id.user_image);
            order_status = itemView.findViewById(R.id.orderstatus);
            complete = itemView.findViewById(R.id.complete);
            orderTine = itemView.findViewById(R.id.timeOrder);
            orderDate = itemView.findViewById(R.id.dateOrder);


            db.collection("Order")
                    .document("orderStatus")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            DocumentSnapshot documentSnapshot = task.getResult();

                        }
                    });



        }

        public void bind(final Order order,final OnItemClickListener orderListListener){

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderListListener.onItemClick(order);

                }

            });
        }


    }



    public interface OnItemClickListener
    {
        void onItemClick(Order user);
    }

}
