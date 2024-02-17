package com.example.trippaycustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RechargeHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    String deviceid;
    private PaymentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_history);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        Intent intent = getIntent();
        if (intent != null) {
            deviceid = intent.getStringExtra("deviceid");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<PaymentItem> paymentItems = new ArrayList<>();
        adapter = new PaymentAdapter(paymentItems); // Initialize the adapter here
        recyclerView.setAdapter(adapter);

        fetchdata();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                adapter = new PaymentAdapter(paymentItems); // Initialize the adapter here
                recyclerView.setAdapter(adapter);

                fetchdata();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
    private void fetchdata() {
            String url = "https://trippay.in/payment.php?cardid=" + deviceid;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Clear the existing items before adding new ones
                            adapter.paymentItems.clear();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String cardId = jsonObject.getString("cardid");
                                    String payment = jsonObject.getString("payment");
                                    String createdAt = jsonObject.getString("created_at");

                                    PaymentItem paymentItem = new PaymentItem(cardId, payment, createdAt);
                                    adapter.paymentItems.add(paymentItem);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Notify the adapter that the data has changed
                            adapter.notifyDataSetChanged();

                            // Complete the swipe-to-refresh animation
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                            // Complete the swipe-to-refresh animation
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonArrayRequest);
        }



    public static class PaymentItem {
        private String cardId;
        private String payment;
        private String createdAt;

        public PaymentItem(String cardId, String payment, String createdAt) {
            this.cardId = cardId;
            this.payment = payment;
            this.createdAt = createdAt;
        }

        public String getCardId() {
            return cardId;
        }

        public String getPayment() {
            return payment;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    public static class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
        private List<PaymentItem> paymentItems;

        public PaymentAdapter(List<PaymentItem> paymentItems) {
            this.paymentItems = paymentItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.payment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PaymentItem paymentItem = paymentItems.get(position);

            holder.paymentTextView.setText(paymentItem.getPayment() + " ₹");
            holder.cardIdTextView.setText("Card ID: " + paymentItem.getCardId());
            holder.createdAtTextView.setText(paymentItem.getCreatedAt());
        }

        @Override
        public int getItemCount() {
            return paymentItems.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView paymentTextView;
            public TextView cardIdTextView;
            public TextView createdAtTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                paymentTextView = itemView.findViewById(R.id.paymentTextView);
                cardIdTextView = itemView.findViewById(R.id.cardIdTextView);
                createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
            }
        }
    }
}