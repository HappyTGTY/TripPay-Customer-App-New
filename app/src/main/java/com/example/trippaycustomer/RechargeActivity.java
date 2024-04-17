package com.example.trippaycustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    EditText cardid,amount,utr;
    Button recharge;
    ImageView copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        amount = findViewById(R.id.utr);
        cardid = findViewById(R.id.tvcardid);
        recharge = findViewById(R.id.btnRecharge);
        utr = findViewById(R.id.utr);
        copy = findViewById(R.id.ImageView_copy);

        // Add activity to the copy button
        {
            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Payment UPI", "7065740848@paytm");
                    // Set the clipboard's primary clip.
                    clipboard.setPrimaryClip(clipData);
                }
            });
        }

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
                String amountS = amount.getText().toString().trim();
                String utrS = amount.getText().toString().trim();

                //checks whether amount and utr are pure integers
                try {
                   Integer.parseInt(utrS);
                   Integer.parseInt(amountS);
                } catch (Exception e) {
                    Toast.makeText(RechargeActivity.this, "Invalid Entry", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cardId.isEmpty() || amountS.isEmpty() || utrS.isEmpty()) {
                    Toast.makeText(RechargeActivity.this, "Please enter Card Id", Toast.LENGTH_SHORT).show();
                } else {
                    sendPostRequest(apiUrl, cardId, amountS,utrS);
                }
            }

            private void sendPostRequest(String apiUrl, final String cardId, final String amount,final String utrS) {
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
                        params.put("utr",utrS);
                        return params;
                    }
                };

                // Add the request to the RequestQueue
                queue.add(stringRequest);
            }
        });

    }
}