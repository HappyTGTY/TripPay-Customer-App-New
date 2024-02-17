package com.example.trippaycustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RechargeActivity extends AppCompatActivity {
    EditText cardid,amount;
    Button recharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        amount=findViewById(R.id.tvamount);
        cardid=findViewById(R.id.tvcardid);
        recharge=findViewById(R.id.btnRecharge);

        Intent intent = getIntent();
        if (intent != null) {
            String reccardid = intent.getStringExtra("cardid"); // -1 is the default value if the key is not found
           // Toast.makeText(this, "cardid "+cardid, Toast.LENGTH_SHORT).show();
            cardid.setText(reccardid);
        }

        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace 'your_api_url' with the actual URL of your PHP API
                String apiUrl = "https://trippay.in/postpayment.php";

                // Replace 'R1234' and '50' with the actual cardId and amount
                String cardId = cardid.getText().toString().trim();
                String amountt = amount.getText().toString().trim();

               if (cardId.isEmpty() || amountt.isEmpty()) {
                     Toast.makeText(RechargeActivity.this, "Please enter Amount", Toast.LENGTH_SHORT).show();
                } else {
                    sendPostRequest(apiUrl, cardId, amountt);
                }
            }

            private void sendPostRequest(String apiUrl, final String cardId, final String amount) {
                RequestQueue queue = Volley.newRequestQueue(RechargeActivity.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                showRechargeSuccessPopup(cardId, amount);
                             }

                            private void showRechargeSuccessPopup(String cardId, String amount) {
                                // Inflate the dialog layout
                                View view = getLayoutInflater().inflate(R.layout.activity_recharge_success, null);

                                // Retrieve views from the layout
                                TextView tvCardId = view.findViewById(R.id.tvCardId);
                                TextView tvAmount = view.findViewById(R.id.tvAmount);
                                TextView tvTransactionId = view.findViewById(R.id.tvTransactionId);

                                // Set data to views
                                tvCardId.setText("Card ID: " + cardId);
                                tvAmount.setText("Amount: " + amount);
                                tvTransactionId.setText("Transaction ID: " + generateRandomTransactionId());

                                // Create and show the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                                builder.setView(view)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Handle OK button click if needed
                                            }
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

// Add any other methods or logic you need

                            private String generateRandomTransactionId() {
                                // Your logic to generate a random transaction ID
                                return "TRX" + System.currentTimeMillis();
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle errors that occurred during the request
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Set POST parameters
                        Map<String, String> params = new HashMap<>();
                        params.put("cardid", cardId);
                        params.put("amount", amount);
                        return params;
                    }
                };

                // Add the request to the RequestQueue
                queue.add(stringRequest);
            }
        });

    }
}