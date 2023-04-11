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

import psm.myapplication.Product;
import psm.myapplication.R;

public class CustomerViewShopItemAdapter extends RecyclerView.Adapter<CustomerViewShopItemAdapter.CustomerViewShopItemHolder> {

    ArrayList<Product> productModelArrayList;
    FirebaseFirestore firebaseFirestore;
    Context context;
    private final OnItemClickListener productlistListener;





    public CustomerViewShopItemAdapter(ArrayList<Product> productModelArrayList, OnItemClickListener productlistListener, Context context){
        this.productModelArrayList = productModelArrayList;
        this.productlistListener=productlistListener;
        firebaseFirestore= FirebaseFirestore.getInstance();
        this.context = context;

    }


    @NonNull
    @Override
    public CustomerViewShopItemAdapter.CustomerViewShopItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.porductperview_cust,parent,false);
        return new CustomerViewShopItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewShopItemHolder holder, int position) {

        Product product = productModelArrayList.get(position);

       // holder.itemCategory_name.setText(productModelArrayList.get(position).getProductCategories());

        holder.productname.setText(productModelArrayList.get(position).getProductName());
        holder.productprice.setText(productModelArrayList.get(position).getProductPrice());
        holder.productunit.setText(productModelArrayList.get(position).getProductUnit());
        Picasso.get().load(productModelArrayList.get(position).getProductURL()).into(holder.productimage);

        holder.bind(productModelArrayList.get(position),productlistListener);


    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }


    public interface RecyclerViewClickListener{
        void onClick(View v,int position);

    }

    public static class CustomerViewShopItemHolder extends RecyclerView.ViewHolder {

        TextView itemCategory_name,productname,productprice,productunit;
        ImageView productimage;

        public CustomerViewShopItemHolder(@NonNull View itemView) {
            super(itemView);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            itemCategory_name = itemView.findViewById(R.id.productcategorycust);
            productname = itemView.findViewById(R.id.productname_cust);
            productprice = itemView.findViewById(R.id.productprice_cust);
            productunit = itemView.findViewById(R.id.productunit_cust);
            productimage = itemView.findViewById(R.id.productimage_cust);


        }

        public void bind(final Product product, final OnItemClickListener productlistListener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productlistListener.onItemClick(product);
                }
            });
        }


    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }



}
