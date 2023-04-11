package psm.myapplication.Mart.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import psm.myapplication.Mart.AddStoreProduct;
import psm.myapplication.Product;
import psm.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView itemRecycleView;
    ArrayList<Product> itemModelArrayList;
    itemAdapter itemAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FloatingActionButton additembttn;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();


    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_item, container, false);


        additembttn = view.findViewById(R.id.additem_bttn);

        itemRecycleView = view.findViewById(R.id.itemrecycleview);
        itemRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemRecycleView.setHasFixedSize(true);
        itemModelArrayList = new ArrayList<>();

        itemRecycleView.setAdapter(itemAdapter);

        db = FirebaseFirestore.getInstance();

        itemAdapter = new itemAdapter(ItemFragment.this,itemModelArrayList);

        AddItemListener();
        EventItemListener();

        itemRecycleView.setAdapter(itemAdapter);
        return view;
    }

    private void AddItemListener() {

        additembttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddStoreProduct.class));
            }
        });
    }


    private void EventItemListener() {

        db.collection("Product")
                .whereEqualTo("user_ID",fAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                FirebaseFirestoreException error = null;

                if (error != null){

                    Log.e("Firestore Error",error.getMessage());
                    return;
                }
                itemModelArrayList.clear();

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                    if (dc.getType() == DocumentChange.Type.ADDED){

                        Product product = dc.getDocument().toObject(Product.class);
                        product.setProductID(dc.getDocument().getId());
                        itemModelArrayList.add(product);
                    }
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}