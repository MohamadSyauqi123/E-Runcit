package psm.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import psm.myapplication.Customer.CustomerOrder;
import psm.myapplication.Customer.CustomerPayment;

public class CheckoutActivity extends AppCompatActivity {

    // 10.0.2.2 is the Android emulator's alias to localhost
    // 192.168.1.6 If you are testing in real device with usb connected to same network then use your IP address
    private static final String BACKEND_URL = "http://192.168.0.114:4242/"; //4242 is port mentioned in server i.e index.js
    EditText amountText;

    CardInputWidget cardInputWidget;
    Button payButton;
    ImageButton back;

    Order order = new Order();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth ;

    // we need paymentIntentClientSecret to start transaction
    private String paymentIntentClientSecret;
    //declare stripe
    private Stripe stripe;

    Double amountDouble=null;

    private OkHttpClient httpClient;

    static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        amountText = findViewById(R.id.amount_id);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        payButton = findViewById(R.id.payButton);
        back=findViewById(R.id.backactivity);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in progress");
        progressDialog.setCancelable(false);
        httpClient = new OkHttpClient();

        Intent i = getIntent();
        double payment = getIntent().getDoubleExtra("totalpayment",0);
        amountText.setText(String.valueOf(payment));


        //Initialize
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51LBNDaFgRSO8av5vBHtlh0ss7BO4vcMXBsNICqIM2Qh1TKgzwmbWI5CgzP044Aqpo1JCOVIUgnYSpKR9jLMMUbsL00j95vrU1n")
        );


        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //get Amount
                amountDouble = Double.valueOf(amountText.getText().toString());
                //call checkout to get paymentIntentClientSecret key
                progressDialog.show();
                startCheckout();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void startCheckout() {
        {
            // Create a PaymentIntent by calling the server's endpoint.
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//        String json = "{"
//                + "\"currency\":\"usd\","
//                + "\"items\":["
//                + "{\"id\":\"photo_subscription\"}"
//                + "]"
//                + "}";
            double amount= amountDouble*100.0;
            Map<String,Object> payMap=new HashMap<>();
            Map<String,Object> itemMap=new HashMap<>();
            List<Map<String,Object>> itemList =new ArrayList<>();
            payMap.put("currency","MYR");
            itemMap.put("id","photo_subscription");
            itemMap.put("amount",amount);
            itemList.add(itemMap);
            payMap.put("items",itemList);
            String json = new Gson().toJson(payMap);
            RequestBody body = RequestBody.create( mediaType,json);
            Request request = new Request.Builder()
                    .url(BACKEND_URL + "create-payment-intent")
                    .post(body)
                    .build();
            httpClient.newCall(request)
                    .enqueue(new PayCallback(CheckoutActivity.this));
            Log.e( "startCheckout: ", request.body().toString());

        }
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<CheckoutActivity> activityRef;
        PayCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            progressDialog.dismiss();
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }
        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");

        //once you get the payment client secret start transaction
        //get card detail
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            //now use paymentIntentClientSecret to start transaction
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            //start payment
            stripe.confirmPayment(CheckoutActivity.this, confirmParams);
        }
        Log.i("TAG", "onPaymentSuccess: "+paymentIntentClientSecret);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));

    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<CheckoutActivity> activityRef;
        PaymentResultCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        //If Payment is successful
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            progressDialog.dismiss();
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Toast toast =Toast.makeText(activity, "Ordered Successful", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();


                insertOrderData();
                startActivity(new Intent(getApplicationContext(), CustomerOrder.class));

                //startActivity(new Intent(getApplicationContext(), CustomerOrder.class));


            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
                startActivity(new Intent(getApplicationContext(), CustomerPayment.class));
            }
        }
        //If Payment is not successful
        @Override
        public void onError(@NonNull Exception e) {
            progressDialog.dismiss();
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }



    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }


    private void insertOrderData() {


        db.collection("AddToCart").whereEqualTo("userID",mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot q=task.getResult();
                task.getResult().getDocuments().get(0).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        q.getDocuments().get(0).getReference().collection("ProductList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList< Map<String,Object>> product = new ArrayList<>();
                                Map<String,Object> data = new HashMap<>();


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
                                data.put("totalPayment",amountDouble);
                                data.put("paymentMethod","TnGo eWallet");
                                db.collection("Order").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        Toast.makeText(getApplicationContext(),"Order Placed.",Toast.LENGTH_SHORT).show();

                                    }
                                });




                            }
                        });

                    }
                });
            }
        });


    }
}