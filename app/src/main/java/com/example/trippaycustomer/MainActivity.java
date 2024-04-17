package com.example.trippaycustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView username,totalBalance,Trips,spend,totaltravel;
    private SwipeRefreshLayout swipeRefreshLayout;
    int totals=0;
    int totald=0;
    String emailREC,deviceidREC;
    private ImageView photo,logout,referButton,rewardsButton,transactionHistory,chatSupport,rechargeHistory,helpButton,settingButton,shareButton;
    private Button rechargeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        referButton = findViewById(R.id.btnRefer);
        rewardsButton = findViewById(R.id.btnRewards);
        transactionHistory = findViewById(R.id.history);
        chatSupport = findViewById(R.id.btnChatSupport);
        rechargeHistory = findViewById(R.id.btnRechargeHistory);
        helpButton = findViewById(R.id.btnHelp);
        settingButton = findViewById(R.id.btnSettings);
        shareButton = findViewById(R.id.btnShare);

        rechargeButton = findViewById(R.id.button_recharge);

        username=findViewById(R.id.username);
        totalBalance=findViewById(R.id.balance);
        Trips=findViewById(R.id.Trips);
        spend=findViewById(R.id.spend);
        totaltravel=findViewById(R.id.totaltravel);

        Intent intent = getIntent();

        //Adds the On click listener to all the bottom images and adds intent to start the activity
        initializeAllActivityIntents();


        //Performs logout on tapping logout icon
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });


        photo = findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new activity for user Details and add it to here
            }
        });


        //Update all the information
        if (intent != null) {
            int userId = intent.getIntExtra("userid", -1); // -1 is the default value if the key is not found
            String userEmail = intent.getStringExtra("email");
            //            Toast.makeText(this, "id : "+userId, Toast.LENGTH_SHORT).show();
            //            Toast.makeText(this, "email : "+userEmail, Toast.LENGTH_SHORT).show();
            emailREC=userEmail;
            fetchData(userEmail);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData(emailREC);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this,"Details Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initializeAllActivityIntents() {

        referButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReferActivity.class);
                startActivity(intent);
            }
        });

        transactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                intent.putExtra("email", emailREC);
                startActivity(intent);
            }
        });

        rechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RechargeActivity.class);
                intent.putExtra("cardid", deviceidREC);
                startActivity(intent);
            }
        });

        rewardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RewardsActivity.class);
                startActivity(intent);
            }
        });

        chatSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatSupportActivity.class);
                startActivity(intent);
            }
        });

        rechargeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RechargeHistory.class);
                intent.putExtra("deviceid", deviceidREC);
                startActivity(intent);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Were meant to start the help Activity
//                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
//                startActivity(intent);

                //Currently transfering the page to gmail with default email
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"help@trippay.in"});
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Email"));
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //Here the URL to the app needs to be added
                sendIntent.putExtra(Intent.EXTRA_TEXT, "URL");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }

    private void fetchData(String email) {
        String url = "https://trippay.in/getcardinfo.php?email="+email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalFare = 0;
                            int totalDistance = 0;
                            totald = 0;
                            totals = 0;
                            String cardBalance = response.getString("card_balance");
                            String uname = response.getString("name");
                            String cardid = response.getString("card_id");
                            deviceidREC = cardid;
                            username.setText(uname);
                            totalBalance.setText(cardBalance + "₹");


                            // Extract riding data array
                             JSONObject jsonResponse = new JSONObject(String.valueOf(response));
                            if (jsonResponse.has("riding_data")) {
                                JSONArray ridingDataArray = response.getJSONArray("riding_data");

                                for (int i = 0; i < ridingDataArray.length(); i++) {
                                JSONObject ridingDataObject = ridingDataArray.getJSONObject(i);
                                if (ridingDataObject != null) {
                                    try {
                                        totalFare = ridingDataObject.getInt("fair");
                                        totalDistance = ridingDataObject.getInt("distance");


                                        totald += totalDistance;
                                        totals += totalFare;
                                        spend.setText(String.valueOf("₹" + totals + " Spent"));
                                        Trips.setText(String.valueOf((i + 1) + " Trips\nTaken"));
                                        totaltravel.setText(String.valueOf(totald + "km \nTravelled"));
                                    } catch (NumberFormatException e) {
                                        // Handle the exception, e.g., log it or show an error message
                                        Log.d("ERROR", String.valueOf(e));
                                    }

                                } else {
                                    spend.setText(String.valueOf("$" + 0 + " Spent"));
                                    Trips.setText(String.valueOf(0 + "Trips Taken"));
                                    totaltravel.setText(String.valueOf(0 + "km \nTravelled"));
                                }
                            }
                        }
                            else {
                                spend.setText(String.valueOf("$" + 0 + " Spent"));
                                Trips.setText(String.valueOf(0 + "Trips Taken"));
                                totaltravel.setText(String.valueOf(0 + "km \nTravelled"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        showLogoutConfirmationDialog();
        super.onBackPressed();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Okay, perform logout and move to login activity
                        performLogout();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel, dismiss the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}