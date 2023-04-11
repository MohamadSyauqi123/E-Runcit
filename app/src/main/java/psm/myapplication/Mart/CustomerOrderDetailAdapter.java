package psm.myapplication.Mart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import psm.myapplication.Order;
import psm.myapplication.R;

public class CustomerOrderDetailAdapter extends RecyclerView.Adapter<CustomerOrderDetailAdapter.CustomerOrderDetailHolder> {

    ArrayList<Map<String,Object>> customerOrderDetailList;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Context context;
    private final OnItemClickListener customerItemdetailListener;



    public CustomerOrderDetailAdapter(ArrayList<Map<String,Object>> customerOrderDetailList, OnItemClickListener customerItemdetailListener) {
        this.customerOrderDetailList = customerOrderDetailList;
        this.customerItemdetailListener = customerItemdetailListener;
        this.context = context;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public CustomerOrderDetailAdapter.CustomerOrderDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_product_order_detail,parent,false);
        return new CustomerOrderDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerOrderDetailAdapter.CustomerOrderDetailHolder holder, int position) {


        Map<String,Object> order = customerOrderDetailList.get(position);

        holder.productName.setText(customerOrderDetailList.get(position).get("productName").toString());
        holder.productTotalPrice.setText(customerOrderDetailList.get(position).get("productTotalPrice").toString());
        holder.quantity.setText(customerOrderDetailList.get(position).get("productQuantity").toString());
        Picasso.get().load(customerOrderDetailList.get(position).get("productUrl").toString()).into(holder.productImage);
//        holder.date.setText(tarikh);
//        holder.time.setText(masa);
//

    }


    @Override
    public int getItemCount() {
        return customerOrderDetailList.size();
    }

    class CustomerOrderDetailHolder extends RecyclerView.ViewHolder{

        TextView productName,productTotalPrice,quantity,time,date;
        ImageView productImage;



        public CustomerOrderDetailHolder(@NonNull View itemView) {
            super(itemView);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            productName = itemView.findViewById(R.id.productName);
            productTotalPrice= itemView.findViewById(R.id.totalPrice);
            quantity = itemView.findViewById(R.id.productQuantity);
            productImage =  itemView.findViewById(R.id.productimage);



        }

        public void bind(final Order order,final OnItemClickListener customerItemdetailListener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customerItemdetailListener.OnItemClick(order);
                }
            });

        }
    }

    public interface OnItemClickListener{
        void OnItemClick(Order orderItem);
    }
}
