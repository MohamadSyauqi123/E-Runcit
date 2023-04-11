package psm.myapplication.Mart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import psm.myapplication.Order;
import psm.myapplication.R;

public class MartOrderAdapter  extends RecyclerView.Adapter<MartOrderAdapter.MartOrderHolder> {


    ArrayList<Order> orderMartArrayList;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth Auth = FirebaseAuth.getInstance();
    private final OnItemClickListener orderMartListListener;

    public MartOrderAdapter(ArrayList<Order> orderMartArrayList, OnItemClickListener orderMartListListener, MartOrder martOrder) {

        this.orderMartArrayList= orderMartArrayList;
        this.context = context;
        this.orderMartListListener = orderMartListListener;

    }

    @NonNull
    @Override
    public MartOrderAdapter.MartOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.martorderhistoryview,parent,false);
        return new MartOrderHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MartOrderAdapter.MartOrderHolder holder, int position) {

        holder.bind(orderMartArrayList.get(position),orderMartListListener);

        holder.orderstt.setText(orderMartArrayList.get(position).getOrderStatus());
        holder.totalPayment.setText(String.format("%.2f",orderMartArrayList.get(position).getTotalPayment()));

        db.collection("Users")
                .whereEqualTo("userID",orderMartArrayList.get(position).getUserID())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                                String name = queryDocumentSnapshot.get("userFullname").toString();
                                holder.custName.setText(name);

                            }
                        }
                    }
                });

        holder.time.setText(orderMartArrayList.get(position).getOrderTime());
        holder.date.setText(orderMartArrayList.get(position).getOrderDate());

    }

    @Override
    public int getItemCount() {
        return orderMartArrayList.size();
    }

    public class MartOrderHolder extends RecyclerView.ViewHolder {

        TextView custName,totalPayment,orderstt,time,date;
        ImageView custImage;

        public MartOrderHolder(View itemview) {
            super(itemview);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            custName = itemview.findViewById(R.id.user_name);
            custImage = itemview.findViewById(R.id.custImage);
            totalPayment = itemview.findViewById(R.id.total_payment);
            orderstt = itemview.findViewById(R.id.orderstatus);
            time = itemview.findViewById(R.id.timeOrder);
            date = itemview.findViewById(R.id.dateOrder);
        }

        public void bind(Order order, OnItemClickListener orderMartListListener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }


    public interface OnItemClickListener{
        void onItemClick(Order mart);
    }
}
