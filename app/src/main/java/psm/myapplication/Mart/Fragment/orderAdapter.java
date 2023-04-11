package psm.myapplication.Mart.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import psm.myapplication.Mart.CustomerOrderItem;
import psm.myapplication.Order;
import psm.myapplication.R;

public class orderAdapter extends RecyclerView.Adapter<orderAdapter.myorderholder>{


    ArrayList<Order> orderModelArrayList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Context context;
    Order order = new Order();
    private final OnItemClickListener orderListListerner;



    public orderAdapter(ArrayList<Order> orderHolder, OnItemClickListener orderListListerner) {

        this.context= context;
        this.orderListListerner = orderListListerner;
        this.orderModelArrayList = orderHolder;
        db = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();


    }





    @NonNull
    @Override
    public orderAdapter.myorderholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mart_order_view,parent,false);
        return new orderAdapter.myorderholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull orderAdapter.myorderholder holder, @SuppressLint("RecyclerView") int position ) {

       holder.dateOrder.setText(orderModelArrayList.get(position).getOrderDate());
       holder.timeOrder.setText(orderModelArrayList.get(position).getOrderTime());

        db.collection("Users")
                        .whereEqualTo("userID",orderModelArrayList.get(position).getUserID())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshots : task.getResult()){

                                String photo = queryDocumentSnapshots.getString("imageProfile");
                                Picasso.get().load(photo);
                            }

                        }
                    }
                });
       // Picasso.get().load(orderModelArrayList.get(position).getCustURL()).into(holder.customerPic);

        db.collection("Users")
                .whereEqualTo("userID",orderModelArrayList.get(position).getUserID())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshots : task.getResult()){

                                String name = queryDocumentSnapshots.getString("userFullname");
                                holder.custName.setText(name);
                            }
                        }
                    }
                });

        holder.recieveMethd.setText(orderModelArrayList.get(position).getReceiveMethod());

        holder.viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "View Item", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(v.getContext(), CustomerOrderItem.class);
                intent.putExtra("order",orderModelArrayList.get(position).getUserID());
                intent.putExtra("id",orderModelArrayList.get(position).getOrder_id());
                intent.putExtra("masa",orderModelArrayList.get(position).getOrderDate());
                intent.putExtra("tarikh",orderModelArrayList.get(position).getOrderTime());

                v.getContext().startActivity(intent);

            }
        });

        holder.orderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                builder1.setTitle("Reminder");
                builder1.setMessage("Order Complete ?");
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (orderModelArrayList.get(position).getReceiveMethod().equals("Pick-Up")){

                            db.collection("Order").document(orderModelArrayList.get(position).getOrder_id())
                                    .update("orderStatus","Ready To Pick-Up")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(v.getContext(), "Order Complete", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                        else {

                            db.collection("Order").document(orderModelArrayList.get(position).getOrder_id())
                                    .update("orderStatus","On Delivery")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(v.getContext(), "Order Complete", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
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

    @Override
    public int getItemCount() {return orderModelArrayList.size();
    }


    class myorderholder extends RecyclerView.ViewHolder{


        ImageView customerPic;
        TextView custName, dateOrder,timeOrder,recieveMethd;
        Button viewItem,orderStatus;


        public myorderholder(@NonNull View itemView) {
            super(itemView);

            recieveMethd = itemView.findViewById(R.id.recieveMethod);
            customerPic = itemView.findViewById(R.id.custImage);
            custName = itemView.findViewById(R.id.customer_Name);
            viewItem = itemView.findViewById(R.id.viewOrder);
            orderStatus = itemView.findViewById(R.id.martcomplete);
            dateOrder = itemView.findViewById(R.id.dateOrder);
            timeOrder = itemView.findViewById(R.id.timeOrder);



        }
    }

    public interface OnItemClickListener{
        void onItemClick(Order custOrder);
    }

}
