package psm.myapplication.Mart.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import psm.myapplication.Order;
import psm.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link orderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class orderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView orderRecycleView;
    ArrayList<Order> orderModelArrayList;
    orderAdapter orderAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public orderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment orderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static orderFragment newInstance(String param1, String param2) {
        orderFragment fragment = new orderFragment();
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
        View view = inflater.inflate(R.layout.fragment_order, container, false);


        db= FirebaseFirestore.getInstance();

        orderRecycleView = view.findViewById(R.id.orderrecycleview);
        orderRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderRecycleView.setHasFixedSize(true);
        orderModelArrayList = new ArrayList<>();

        orderRecycleView.setAdapter(orderAdapter);

        EventItemListener();

        orderAdapter= new orderAdapter(orderModelArrayList, new orderAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(Order custOrder) {
//               Intent i = new Intent(getContext(), CustomerOrderItem.class);
//               startActivity(i);
           }
       });

        orderRecycleView.setAdapter(orderAdapter);
        return view;

    }

    private void EventItemListener() {

        db.collection("Order")
                .whereEqualTo("storeID", mAuth.getCurrentUser().getUid())
                .whereEqualTo("orderStatus","Order Preparing")
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .orderBy("orderTime", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        FirebaseFirestoreException error = null;

                        if (error != null){

                            Log.e("Firestore Error",error.getMessage());
                            return;
                        }
                        orderModelArrayList.clear();


                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                            if (dc.getType() == DocumentChange.Type.ADDED){

                                Log.e( "onSuccess: ",dc.getDocument().getId() );

                                Order order = dc.getDocument().toObject(Order.class);
                                order.setOrder_id(dc.getDocument().getId());
//                                order.setUserID(dc.getDocument().get("userID").toString());
                                orderModelArrayList.add(order);
                            }
                            orderAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }
}