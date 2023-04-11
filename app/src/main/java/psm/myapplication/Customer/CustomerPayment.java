package psm.myapplication.Customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import psm.myapplication.AddToCart;
import psm.myapplication.Order;
import psm.myapplication.R;
import psm.myapplication.User;

public class CustomerPayment extends AppCompatActivity {

    ImageButton back;
    Button checkoutbttn;
    TextView totalPayment,productName, productPrice, productQuantity,totalPrice,deliCas;
    ImageView productImage;
    RadioButton creditpay,tnGopay,cashpay,pickpUpItem,deliveryItem;
    RadioGroup paymentMethod, serviceMethod;

    double payment = 0, pay = 0;
    AddToCart product = new AddToCart();
    User user = new User();

    QuerySnapshot q;
    Order order = new Order();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment);


        totalPayment = findViewById(R.id.total_payment);
        checkoutbttn = findViewById(R.id.confirmpaymentbttn);
        back = findViewById(R.id.back);
        creditpay = (RadioButton) findViewById(R.id.creditpaybttn);
        tnGopay = (RadioButton) findViewById(R.id.ewllaetpaybttn);
        cashpay = (RadioButton) findViewById(R.id.cashpaybttn);
        pickpUpItem = (RadioButton) findViewById(R.id.pickupbttn);
        deliveryItem = (RadioButton) findViewById(R.id.deliverybttn);
        paymentMethod = (RadioGroup) findViewById(R.id.payment_method);
        serviceMethod = (RadioGroup) findViewById(R.id.service_method);
        totalPrice = findViewById(R.id.subtotal);
        deliCas = findViewById(R.id.cas);


        Intent i = getIntent();
        double totalpay = getIntent().getDoubleExtra("cart", 0);
        totalPrice.setText(String.format("%,.2f", totalpay));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        methodchoice();

    }

    private void methodchoice() {

        RadioGroup rg = (RadioGroup) findViewById(R.id.payment_method);
        RadioGroup rg2 = (RadioGroup) findViewById(R.id.service_method);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rg2.clearCheck();
                deliCas.clearComposingText();
                switch(checkedId){
                    case R.id.creditpaybttn:
                        // do operations specific to this selection

                        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == R.id.pickupbttn){

                                    String pick1 = "Pick-Up";
                                    double deliCharge = 0;
                                    deliCas.setText(String.valueOf(deliCharge));
                                    checkoutitemCredit(pick1, deliCharge);

                                }
                                else if(checkedId == R.id.deliverybttn){

                                    String pick1 = "Delivery";
                                    db.collection("AddToCart")
                                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    double jarakKedai = (double) queryDocumentSnapshots.getDocuments().get(0).get("storeDist");

                                                    double deliCharge = jarakKedai * 5;
                                                    deliCas.setText(String.format("%,.2f",deliCharge));
                                                    checkoutitemCredit(pick1,deliCharge);
                                                }
                                            });



                                }
                            }
                        });

                        break;

                    case R.id.ewllaetpaybttn:
                        // do operations specific to this selection
                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerPayment.this);
                        builder.setTitle("Reminder");
                        builder.setMessage("Please provide a sufficient balance in your TnGo eWallet to make your payment at the store later.");
                        builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();

                        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == R.id.pickupbttn){
                                    Toast.makeText(CustomerPayment.this, "pickup tngo", Toast.LENGTH_SHORT).show();
                                    String pick2 = "Pick-Up";
                                    double deliCharge = 0;
                                    deliCas.setText(String.valueOf(deliCharge));
                                    checkoutitemTngo(pick2,deliCharge);
                                }
                                else if(checkedId == R.id.deliverybttn){

                                    Toast.makeText(CustomerPayment.this, "delivery tngo", Toast.LENGTH_SHORT).show();
                                    String pick2 = "Delivery";
                                    double deliCharge = 0;
                                    deliCas.setText(String.valueOf(deliCharge));
                                    checkoutitemTngo2();
                                }
                            }
                        });

                        break;

                    case R.id.cashpaybttn:
                        // do operations specific to this selection
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(CustomerPayment.this);
                        builder1.setTitle("Reminder");
                        builder1.setMessage("Please provide a sufficient Balances in your wallet to make your payment later.");
                        builder1.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder1.show();

                        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == R.id.pickupbttn){
                                    Toast.makeText(CustomerPayment.this, "pickup cash", Toast.LENGTH_SHORT).show();
                                    String pick3 = "Pick-Up";
                                    double deliCharge = 0;
                                    deliCas.setText(String.valueOf(deliCharge));
                                    checkitemcash(pick3,deliCharge);

                                }
                                else if(checkedId == R.id.deliverybttn){

                                    Toast.makeText(CustomerPayment.this, "delivery cash", Toast.LENGTH_SHORT).show();

                                    String pick3 = "Delivery";
                                    db.collection("AddToCart")
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    double jarakKedai = (double) queryDocumentSnapshots.getDocuments().get(0).get("storeDist");

                                                    double deliCharge = jarakKedai * 5;
                                                    deliCas.setText(String.format("%,.2f",deliCharge));
                                                    checkoutitemCredit(pick3,deliCharge);
                                                }
                                            });
                                }
                            }
                        });
                        break;
                }
            }
        });
    }




    private void checkoutitemCredit(String pick1,double deliCharge) {

        Intent i = getIntent();
        double totalpay = getIntent().getDoubleExtra("cart", 0);
        totalPrice.setText(String.format("%,.2f", totalpay));
        pay = deliCharge + totalpay;
        totalPayment.setText(String.format("%,.2f", pay));

        checkoutbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String terima = pick1;

                db.collection("AddToCart").whereEqualTo("userID",mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        q=task.getResult();
                        task.getResult().getDocuments().get(0).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                q.getDocuments().get(0).getReference().collection("ProductList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        ArrayList< Map<String,Object>> product = new ArrayList<>();
                                        Map<String,Object> data = new HashMap<>();
                                        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                                        String date = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());


                                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                                        {

                                            Map<String,Object> map = new HashMap<>();
                                            map =  queryDocumentSnapshots.getDocuments().get(i).getData();
                                            queryDocumentSnapshots.getDocuments().get(i).getReference().delete();
                                            product.add(map);
                                        }
                                        data.put("Product",product);
                                        data.put("userID",mAuth.getCurrentUser().getUid());
                                        data.put("orderStatus","Order Preparing");
                                        data.put("storeID",q.getDocuments().get(0).get("storeID"));
                                        data.put("totalPayment",pay);
                                        data.put("paymentMethod","Credit Card");
                                        data.put("receiveMethod",terima);
                                        data.put("orderDate",date);
                                        data.put("orderTime",time);

                                        db.collection("Order").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                Toast.makeText(getApplicationContext(),"Order Created.",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), CustomerOrder.class);
                                                intent.putExtra("totalpayment",pay);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void checkoutitemTngo(String pick2,Double deliCharge) {

        String terima2 = pick2;
        Intent i = getIntent();
        double totalpay = getIntent().getDoubleExtra("cart", 0);
        totalPrice.setText(String.format("%,.2f", totalpay));
        pay = deliCharge + totalpay;
        totalPayment.setText(String.format("%,.2f", pay));

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerPayment.this);
        builder.setTitle("Order Confirmation");
        builder.setMessage("Your Order will able to pick-up when your \n(Status: Order is Ready)");
        builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

        checkoutbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("AddToCart").whereEqualTo("userID",mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        q=task.getResult();
                        task.getResult().getDocuments().get(0).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                q.getDocuments().get(0).getReference().collection("ProductList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        ArrayList< Map<String,Object>> product = new ArrayList<>();
                                        Map<String,Object> data = new HashMap<>();
                                        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                                        String date = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());


                                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                                        {

                                            Map<String,Object> map = new HashMap<>();
                                            map =  queryDocumentSnapshots.getDocuments().get(i).getData();
                                            queryDocumentSnapshots.getDocuments().get(i).getReference().delete();
                                            product.add(map);
                                        }
                                        data.put("Product",product);
                                        data.put("userID",mAuth.getCurrentUser().getUid());
                                        data.put("orderStatus","Order Preparing");
                                        data.put("storeID",q.getDocuments().get(0).get("storeID"));
                                        data.put("totalPayment",pay);
                                        data.put("paymentMethod","TnGo eWallet");
                                        data.put("receiveMethod",terima2);
                                        data.put("orderDate",date);
                                        data.put("orderTime",time);

                                        db.collection("Order").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                Toast.makeText(getApplicationContext(),"Order Created.",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), CustomerDashboard.class);
                                                intent.putExtra("totalpayment",pay);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    private void checkoutitemTngo2() {

        Intent i = getIntent();
        double totalpay = getIntent().getDoubleExtra("cart", 0);
        totalPrice.setText(String.format("%,.2f", totalpay));

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerPayment.this);
        builder.setTitle("Reminder");
        builder.setMessage("We are not provide Delivery for this payment.");
        builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

        checkoutbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Delivery is no available for TnGo Ewallet,\nPlease choose another method !!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkitemcash(String pick3,Double deliCharge) {


        Intent i = getIntent();
        double totalpay = getIntent().getDoubleExtra("cart", 0);
        totalPrice.setText(String.format("%,.2f", totalpay));
        pay = deliCharge + totalpay;
        totalPayment.setText(String.format("%,.2f", pay));

        checkoutbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("AddToCart").whereEqualTo("userID",mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        q=task.getResult();
                        task.getResult().getDocuments().get(0).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                q.getDocuments().get(0).getReference().collection("ProductList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        ArrayList< Map<String,Object>> product = new ArrayList<>();
                                        Map<String,Object> data = new HashMap<>();
                                        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                                        String date = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());

                                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                                        {

                                            Map<String,Object> map = new HashMap<>();
                                            map =  queryDocumentSnapshots.getDocuments().get(i).getData();
                                            queryDocumentSnapshots.getDocuments().get(i).getReference().delete();
                                            product.add(map);
                                        }

                                        data.put("Product",product);
                                        data.put("userID",mAuth.getCurrentUser().getUid());
                                        data.put("orderStatus","Order Preparing");
                                        data.put("storeID",q.getDocuments().get(0).get("storeID"));
                                        data.put("totalPayment",pay);
                                        data.put("paymentMethod","Cash");
                                        data.put("receiveMethod",pick3);
                                        data.put("orderDate",date);
                                        data.put("orderTime",time);

                                        db.collection("Order").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                Toast.makeText(getApplicationContext(),"Order Created.",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), CustomerOrder.class);
                                                intent.putExtra("totalpayment",pay);
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                });

                            }
                        });
                    }
                });

            }
        });
    }

    private void checkitemcash2(String pick3, double deliCharge) {

        Intent i = getIntent();
        double totalpay = getIntent().getDoubleExtra("cart", 0);
        totalPrice.setText(String.format("%,.2f", totalpay));
        pay = deliCharge + totalpay;
        totalPayment.setText(String.format("%,.2f", pay));

        checkoutbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                db.collection("AddToCart").whereEqualTo("userID",mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        q=task.getResult();
                        task.getResult().getDocuments().get(0).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                q.getDocuments().get(0).getReference().collection("ProductList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        ArrayList< Map<String,Object>> product = new ArrayList<>();
                                        Map<String,Object> data = new HashMap<>();
                                        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                                        String date = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());

                                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                                        {

                                            Map<String,Object> map = new HashMap<>();
                                            map =  queryDocumentSnapshots.getDocuments().get(i).getData();
                                            queryDocumentSnapshots.getDocuments().get(i).getReference().delete();
                                            product.add(map);
                                        }
                                        data.put("Product",product);
                                        data.put("userID",mAuth.getCurrentUser().getUid());
                                        data.put("orderStatus","Order Preparing");
                                        data.put("storeID",q.getDocuments().get(0).get("storeID"));
                                        data.put("totalPayment",pay);
                                        data.put("paymentMethod","Cash");
                                        data.put("receiveMethod",pick3);
                                        data.put("orderDate",date);
                                        data.put("orderTime",time);

                                        db.collection("Order").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                Toast.makeText(getApplicationContext(),"Order Created.",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), CustomerOrder.class);
                                                intent.putExtra("totalpayment",pay);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });

                            }
                        });
                    }
                });
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.creditpaybttn:
                if(checked)

                break;
            case R.id.ewllaetpaybttn:
                if(checked)

                break;
            case R.id.cashpaybttn:
                if(checked)

                break;
        }
    }






}


