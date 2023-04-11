package psm.myapplication.Mart.Fragment;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import psm.myapplication.Product;
import psm.myapplication.R;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.myitemholder>{

    ArrayList<Product> itemModelArrayList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    public itemAdapter(ItemFragment itemFragment, ArrayList<Product> itemHolder) {

        this.itemModelArrayList = itemHolder;
        db = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public itemAdapter.myitemholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview,parent,false);
        return new myitemholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull itemAdapter.myitemholder holder, @SuppressLint("RecyclerView") int position) {


        Product product = itemModelArrayList.get(position);

        Picasso.get().load(itemModelArrayList.get(position).getProductURL()).into(holder.product_photo);
        holder.product_name.setText(itemModelArrayList.get(position).getProductName());
        holder.product_price.setText(itemModelArrayList.get(position).getProductPrice());
        holder.product_unit.setText(itemModelArrayList.get(position).getProductUnit());
        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                builder1.setTitle("Reminder");
                builder1.setMessage("Confirm Delete ?");
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection("Product")
                                .document(itemModelArrayList.get(position).getProductID())
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            itemModelArrayList.remove(itemModelArrayList.get(position));
                                            notifyDataSetChanged();
                                        }
                                        Toast.makeText(v.getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();

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

    @Override
    public int getItemCount() {
        return itemModelArrayList.size();
    }

    class myitemholder extends RecyclerView.ViewHolder{

        ImageView product_photo;
        TextView product_name, product_price, product_unit;
        ImageButton removeItem;

        public myitemholder(@NonNull View itemView) {
            super(itemView);


            product_photo = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.prdct_name);
            product_price = itemView.findViewById(R.id.prdct_price);
            product_unit = itemView.findViewById(R.id.prdct_unit);
            removeItem = itemView.findViewById(R.id.removeItemStore);
        }
    }


}
