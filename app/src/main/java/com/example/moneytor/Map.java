package com.example.moneytor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Going back returns user to previous page as there is no navigation bar
        btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * Initialises map and adds transactions onto the map with their store name and value
     * <p>
     * Uses transaction's latitude and longitude values
     * <p>
     * if-statement checks that transaction is one that should be plotted on map (money transfers and
     * card validations are not plotted on map)
     *
     * @param googleMap GoogleMap being used to display marker location
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setCompassEnabled(false);

        ArrayList<String> title = new ArrayList<>();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        ArrayList<String> amount = new ArrayList<>();


        for (int i = 0; i < FetchData.list.size(); i++) {
            if (FetchData.list.get(i).getLatitude() != 0.0 && FetchData.list.get(i).getAmount() != 0) {
                title.add(FetchData.list.get(i).getName());
                String convert = amountToPound("" + FetchData.list.get(i).getAmount());
                amount.add(convert);
                Double latitude = FetchData.list.get(i).getLatitude();
                Double longitude = FetchData.list.get(i).getLongitude();
                latLngs.add(new LatLng(latitude, longitude));
            }
        }

        for (int i = 0; i < title.size(); i++) {
            map.addMarker(new MarkerOptions().position(latLngs.get(i)).title(title.get(i)).snippet(amount.get(i)));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(i)));
        }
    }


    /**
     * Converts transaction value to pounds
     */
    public String amountToPound(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount) / 100;
        if (amount.charAt(0) == '-') {
            return "-£" + df.format(amountL).substring(1);
        }
        return "£" + df.format(amountL);
    }


}
