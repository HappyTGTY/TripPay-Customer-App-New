package com.example.trippaycustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {
    private CardView cardViewCardInfo;
    private TextView textViewCardId, textViewCardBalance,name;
    private RecyclerView recyclerViewRidingData;
    private SwipeRefreshLayout swipeRefreshLayout;
    int totalspend;
    TextView spend;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        recyclerViewRidingData = findViewById(R.id.recyclerViewRidingData);
spend=findViewById(R.id.spend);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        Intent intent = getIntent();
        if (intent != null) {
            String userEmail = intent.getStringExtra("email");
            fetchData(userEmail);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String userEmail = intent.getStringExtra("email");
                fetchData(userEmail);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchData(String email) {
        String url = "https://trippay.in/getcardinfo.php?email="+email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<RidingData> ridingDataList = new ArrayList<>();
                        ridingDataList.clear();
                        totalspend=0;
                        try {
                            String cardId = response.getString("card_id");
                            String cardBalance = response.getString("card_balance");
                            String uname=response.getString("name");

                            // Set card information in the views
//                            textViewCardId.setText("Card ID: " + cardId);
//                            name.setText("Account Holder : " + uname);
//                            textViewCardBalance.setText("Card Balance: " + cardBalance+"$");


                            // Extract riding data array
                            JSONArray ridingDataArray = response.getJSONArray("riding_data");

                            // Create a list to hold riding data objects


                            // Iterate through the riding data array
                            for (int i = 0; i < ridingDataArray.length(); i++) {
                                JSONObject ridingDataObject = ridingDataArray.getJSONObject(i);

                                // Extract data from the JSON object
                                String startPoint = ridingDataObject.getString("startpoint");
                                String startPointWithoutComma = startPoint.replaceAll(",", "\n");
                                startPoint=startPointWithoutComma;
                                String endPoint = ridingDataObject.getString("endpoint");
                                startPointWithoutComma = endPoint.replaceAll(",", "\n");
                                endPoint=startPointWithoutComma;
                                    int tf = ridingDataObject.getInt("fair");
                                    String totalFare= String.valueOf(ridingDataObject.getInt("fair"));
                                    totalspend += tf;
                                    spend.setText(String.valueOf("₹" + totalspend));
                                int distancecover1 = ridingDataObject.getInt("distance");
                                String driverName = ridingDataObject.getString("deviceid");
                                String logtime = ridingDataObject.getString("logtime");
                                String drivername = ridingDataObject.getString("full_name");
                                String startep=ridingDataObject.getString("startep");
                                String endep=ridingDataObject.getString("endep");
                                long logtimeSeconds = Long.parseLong(String.valueOf(1702767030)) / 1000;

                                Instant instant = Instant.ofEpochSecond(logtimeSeconds);
                                LocalDate logDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                                LocalDate currentDate = LocalDate.now();
//                                Toast.makeText(TransactionActivity.this, "logDate : "+logDate, Toast.LENGTH_SHORT).show();
//                                Toast.makeText(TransactionActivity.this, "currentDate : "+currentDate, Toast.LENGTH_SHORT).show();

                                if (logDate.equals(currentDate)) {
                                 //   Toast.makeText(TransactionActivity.this, "today", Toast.LENGTH_SHORT).show();
                                } else {
                                  //  Toast.makeText(TransactionActivity.this, "NOT today", Toast.LENGTH_SHORT).show();
                                }


                                //    Toast.makeText(MainActivity.this, "start ep"+startep.toString(), Toast.LENGTH_SHORT).show();
                                // Create a RidingData object and add it to the list
                                TransactionActivity.RidingData ridingData = new TransactionActivity.RidingData(startPoint, endPoint, totalFare, driverName, logtime, drivername,startep,endep, distancecover1);
                                ridingDataList.add(ridingData);
                            }

                            // Set up the RecyclerView with the adapter
                            TransactionActivity.RidingDataAdapter adapter = new TransactionActivity.RidingDataAdapter(ridingDataList);
                            recyclerViewRidingData.setLayoutManager(new LinearLayoutManager(TransactionActivity.this));
                            recyclerViewRidingData.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(TransactionActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    // Define a data model for riding data
    private static class RidingData {
        private final String startPoint;
        private final String endPoint;
        private final String totalFare;
        private final String driverName;
        private final String logtime;
        private final String drivername;
        private final String startep;
        private final String endep;
        private final int distancecover;


        public RidingData(String startPoint, String endPoint, String totalFare, String driverName, String logtime, String drivername, String sep, String eep, int distancecover1) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.totalFare = totalFare;
            this.driverName = driverName;
            this.logtime = logtime;
            this.drivername = drivername;
            this.startep=sep;
            this.endep=eep;
            this.distancecover = distancecover1;
        }
    }

    // Adapter for the RecyclerView
    private class RidingDataAdapter extends RecyclerView.Adapter<TransactionActivity.RidingDataAdapter.ViewHolder> {

        private final List<TransactionActivity.RidingData> ridingDataList;

        public RidingDataAdapter(List<TransactionActivity.RidingData> ridingDataList) {
            this.ridingDataList = ridingDataList;
        }

        @NonNull
        @Override
        public TransactionActivity.RidingDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riding_data, parent, false);
            return new TransactionActivity.RidingDataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionActivity.RidingDataAdapter.ViewHolder holder, int position) {
            TransactionActivity.RidingData ridingData = ridingDataList.get(position);
            holder.textViewDriverName.setText(ridingData.drivername+" ["+ ridingData.driverName+"]");
            holder.textViewStartPoint.setText(ridingData.startPoint);
            holder.textViewEndPoint.setText(ridingData.endPoint);

            holder.textViewTotalFare.setText(ridingData.totalFare+"$/"+ridingData.distancecover+"km");
          //  holder.textViewTotalDistance.setText("Total Distance: " + ridingData.totalDistance+"Km");
            holder.textViewstep.setText(ridingData.startep);
            holder.textVieweep.setText(ridingData.endep);


            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

            try {
                long logtimeEpoch = Long.parseLong(ridingData.logtime);
                Date date = new Date(logtimeEpoch * 1000); // Convert epoch time to milliseconds
                String formattedLogTime = outputFormat.format(date);
                holder.textViewLogTime.setText(formattedLogTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.textViewLogTime.setText("Log Time: Error formatting date");
            }

            try {
                long logtimeEpoch = Long.parseLong(ridingData.startep);
                Date date = new Date(logtimeEpoch * 1000); // Convert epoch time to milliseconds
                String formattedLogTime = outputFormat.format(date);
                holder.textViewstep.setText(formattedLogTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.textViewstep.setText("Log Time: Error formatting date");
            }
            try {
                long logtimeEpoch = Long.parseLong(ridingData.endep);
                Date date = new Date(logtimeEpoch * 1000); // Convert epoch time to milliseconds
                String formattedLogTime = outputFormat.format(date);
                holder.textVieweep.setText(formattedLogTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.textVieweep.setText("Log Time: Error formatting date");
            }




            // Set a click listener for the RecyclerView item
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click, e.g., navigate to another activity
                    navigateToDetailsActivity(ridingData);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ridingDataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewStartPoint, textViewEndPoint, textViewTotalFare, textViewTotalDistance,textViewLogTime,textViewDriverName,textViewstep,textVieweep;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewStartPoint = itemView.findViewById(R.id.textViewStartPoint);
                textViewEndPoint = itemView.findViewById(R.id.textViewEndPoint);
                textViewTotalFare = itemView.findViewById(R.id.textViewTotalFare);
                textViewTotalDistance = itemView.findViewById(R.id.textViewTotalDistance);
                textViewDriverName=itemView.findViewById(R.id.textViewDriverName);
                textViewLogTime=itemView.findViewById(R.id.textViewLogTime);
                textViewstep=itemView.findViewById(R.id.sep);
                textVieweep=itemView.findViewById(R.id.eep);

            }
        }
    }

    // Method to navigate to the details activity
    private void navigateToDetailsActivity(TransactionActivity.RidingData ridingData) {
        Intent intent = new Intent(TransactionActivity.this, DetailActivity.class);

        String spp = ridingData.startPoint;
        String spf = spp.replaceAll("\n", ",");
        intent.putExtra("startPoint", spf);

        String epp = ridingData.endPoint;
        String epf = epp.replaceAll("\n", ",");


        intent.putExtra("endPoint", epf);
        intent.putExtra("totalFare", ridingData.totalFare);
        intent.putExtra("totalDistance", ridingData.driverName);
        startActivity(intent);
    }

    private String formatEpochTime(long epochTime) {
        Date date = new Date(epochTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }}