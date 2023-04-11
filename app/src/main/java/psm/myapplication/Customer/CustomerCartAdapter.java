package psm.myapplication.Customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import psm.myapplication.AddToCart;
import psm.myapplication.R;

public class CustomerCartAdapter extends RecyclerView.Adapter<CustomerCartAdapter.CustomerCartHolder> {

    ArrayList<AddToCart> AddtoCartArrayList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Context context;


    public CustomerCartAdapter(ArrayList<AddToCart> AddCartArrayList){

        this.AddtoCartArrayList = AddCartArrayList;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CustomerCartAdapter.CustomerCartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addtocartitemview,parent,false);
        return new CustomerCartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerCartAdapter.CustomerCartHolder holder, @SuppressLint("RecyclerView") int position) {

        AddToCart product = AddtoCartArrayList.get(position);

        Picasso.get().load(AddtoCartArrayList.get(position).getProductUrl()).into(holder.Item_image);
        holder.Item_name.setText(AddtoCartArrayList.get(position).getProductName());
        holder.Item_price.setText(AddtoCartArrayList.get(position).getProductPrice());
        holder.Item_quantity.setText(AddtoCartArrayList.get(position).getProductQuantity());
        holder.Item_pricetotal.setText(String.valueOf(Double.parseDouble(product.getProductQuantity())* Double.parseDouble(product.getProductPrice())));
        holder.item_unit.setText(AddtoCartArrayList.get(position).getProductUnit());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("AddToCart").whereEqualTo("userID",mAuth.getCurrentUser().getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                       queryDocumentSnapshots.getDocuments().get(0).getReference().collection("ProductList").document

                    (AddtoCartArrayList.get(position).getProductID())
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            notifyDataSetChanged();

                                        }else {

                                        }
                                    }
                                });
                    }
                });
            }
        });

        holder.additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(AddtoCartArrayList.get(position).getProductQuantity())+1;
                AddtoCartArrayList.get(position).getDocumentReference().update("productQuantity",String.valueOf(value));
            }
        });


        holder.removeitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AddtoCartArrayList.get(position).getProductQuantity().equals("1")){

                    int value = Integer.parseInt(AddtoCartArrayList.get(position).getProductQuantity())-1;
                    AddtoCartArrayList.get(position).getDocumentReference().update("productQuantity",String.valueOf(value));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return AddtoCartArrayList.size();
    }

    public class CustomerCartHolder  extends RecyclerView.ViewHolder{

        TextView Item_name,Item_price,Item_quantity,Item_pricetotal,item_unit;
        ImageView Item_image;
        ImageButton remove, removeitem,additem;


        public CustomerCartHolder(@NonNull View itemView) {
            super(itemView);


            remove = itemView.findViewById(R.id.removeItem);
            Item_name = itemView.findViewById(R.id.item_name);
            Item_price = itemView.findViewById(R.id.item_price);
            Item_quantity = itemView.findViewById(R.id.itemquantity);
            Item_image = itemView.findViewById(R.id.productimage);
            Item_pricetotal = itemView.findViewById(R.id.totalpriceitem);
            removeitem = itemView.findViewById(R.id.removecart_bttn);
            additem = itemView.findViewById(R.id.addcart_bttn);
            item_unit = itemView.findViewById(R.id.unit);



        }
    }
}
