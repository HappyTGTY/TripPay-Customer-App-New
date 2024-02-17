package com.example.trippaycustomer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView textViewTotalDistance, textViewTotalFare;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
       // textViewTotalDistance = findViewById(R.id.textViewTotalDistance);
        textViewTotalFare = findViewById(R.id.spend);
        mapView = findViewById(R.id.mapView);

        // Initialize the MapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Retrieve data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String totalDistance = intent.getStringExtra("totalDistance");
            String totalFare = intent.getStringExtra("totalFare");

            // Set values to TextViews
           // textViewTotalDistance.setText("Total Distance: " + totalDistance+"Km");
            textViewTotalFare.setText("₹" +totalFare);
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        // Retrieve start point and end point from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String startPoint = intent.getStringExtra("startPoint");
            String endPoint = intent.getStringExtra("endPoint");
//            Toast.makeText(this, "start p "+startPoint, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "End P "+endPoint, Toast.LENGTH_SHORT).show();
            // Convert start point and end point strings to LatLng objects
            LatLng startLatLng = convertStringToLatLng(startPoint);
            LatLng endLatLng = convertStringToLatLng(endPoint);

            // Add markers for start point and end point
            if (startLatLng != null && endLatLng != null) {
                googleMap.addMarker(new MarkerOptions().position(startLatLng).title("Start Point"));
                googleMap.addMarker(new MarkerOptions().position(endLatLng).title("End Point"));

                // Move camera to include both markers with padding
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(startLatLng);
                builder.include(endLatLng);
                LatLngBounds bounds = builder.build();

                // Calculate a reasonable padding value based on the screen size
                int padding = calculatePadding();

                //
                // Adjust camera position to include both markers
                if (padding > 0) {
                    googleMap.setOnMapLoadedCallback(() -> {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    });
                } else {
                    // Handle the case where padding is not calculated correctly
                    // You may want to provide a default padding or handle it in a way that fits your requirements
                    Toast.makeText(this, "Unable to calculate padding", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Helper method to calculate a reasonable padding based on the screen size
    private int calculatePadding() {
        int paddingPercentage = 10; // You can adjust this percentage as needed
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Calculate padding as a percentage of the screen width
        int padding = (int) (screenWidth * (paddingPercentage / 100.0));

        // Ensure that padding is positive
        return Math.max(padding, 0);
    }


                // Helper method to convert a string with latitude and longitude to a LatLng object
    private LatLng convertStringToLatLng(String latLngString) {
        try {
            String[] latLngArray = latLngString.split(",");

            // Add logging to see the values
            Log.d("DetailsActivity", "latLngString: " + latLngString);
            Log.d("DetailsActivity", "latLngArray length: " + latLngArray.length);

            if (latLngArray.length >= 2) {
                double latitude = Double.parseDouble(latLngArray[0].trim());
                double longitude = Double.parseDouble(latLngArray[1].trim());
                return new LatLng(latitude, longitude);
            } else if (latLngArray.length == 1) {
                // Assume that the single value is either latitude or longitude
                double singleValue = Double.parseDouble(latLngArray[0].trim());
                return new LatLng(singleValue, 0.0); // Assuming 0.0 for longitude
            } else {
                Log.e("DetailsActivity", "Invalid latLngArray length for latLngString: " + latLngString);
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e("DetailsActivity", "Error converting string to LatLng. NumberFormatException", e);
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.e("DetailsActivity", "Error converting string to LatLng. ArrayIndexOutOfBoundsException", e);
            return null;
        }
    }

    // Lifecycle methods for the MapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}