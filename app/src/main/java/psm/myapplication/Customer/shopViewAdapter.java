package psm.myapplication.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import psm.myapplication.R;
import psm.myapplication.User;

public class shopViewAdapter extends RecyclerView.Adapter<shopViewAdapter.shopViewHolder> {

    ArrayList<User> shopModelArrayList;
    Context context;
    FirebaseFirestore firebaseFirestore;
    private final OnItemClickListener shoplistListener;



    public shopViewAdapter(ArrayList<User> shopModelArrayList, OnItemClickListener  shoplistListener, Context context) {
        this.shopModelArrayList = shopModelArrayList;
        this.shoplistListener = shoplistListener;
        this.context = context;

    }


    @NonNull
    @Override
    public  shopViewAdapter.shopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopview,parent,false);
        return new shopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull shopViewHolder holder, int position) {

        User shop = shopModelArrayList.get(position);

        Picasso.get().load(shopModelArrayList.get(position).getUserProfile_image()).into(holder.shopImageView);
        holder.shopName.setText(shopModelArrayList.get(position).getUsername());
        holder.bind(shopModelArrayList.get(position),shoplistListener);


    }



    @Override
    public int getItemCount() {
        return shopModelArrayList.size();
    }



    public class shopViewHolder extends RecyclerView.ViewHolder{

        TextView  shopName,shopID;
        ImageView shopImageView;

        public shopViewHolder(@NonNull View itemView) {
            super(itemView);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            shopName = itemView.findViewById(R.id.shopViewName);
            shopImageView = itemView.findViewById(R.id.store_image);


        }
        public void bind(final User user, final OnItemClickListener shoplistListener){

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoplistListener.onItemClick(user);
                }
            });
        }
    }

    public interface OnItemClickListener {


        void onItemClick(User user);
    }




}
