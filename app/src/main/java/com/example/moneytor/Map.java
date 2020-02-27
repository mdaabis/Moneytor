package com.example.moneytor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        btnBack = (Button) findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng inter = new LatLng(52.381409, -1.557429);
        ArrayList<String> title = new ArrayList<>();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        ArrayList<String> amount = new ArrayList<>();

        for(int i=0;i<FetchData.list.size();i++) {
            if(FetchData.list.get(i).getLatitude()!=0.0) {
                title.add(FetchData.list.get(i).getName());
                String convert = amountToPound(""+FetchData.list.get(i).getAmount());
                amount.add(convert);
                Double latitude = FetchData.list.get(i).getLatitude();
                Double longitude= FetchData.list.get(i).getLongitude();
                latLngs.add(new LatLng(latitude, longitude));
            }
        }

        for(int i=0;i<title.size();i++){
            map.addMarker(new MarkerOptions().position(latLngs.get(i)).title(title.get(i)).snippet(amount.get(i)));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(i)));
        }
    }

    public String amountToPound(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount)/100;
        if(amount.charAt(0)=='-') {
            return "-£" + df.format(amountL).substring(1);
        }
        return "£" + df.format(amountL);
    }
}
