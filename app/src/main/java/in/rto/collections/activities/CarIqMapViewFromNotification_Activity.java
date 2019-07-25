package in.rto.collections.activities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.rto.collections.R;

import static in.rto.collections.utilities.Utilities.splitCamelCase;

public class CarIqMapViewFromNotification_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private Context context;
    private GoogleMap mMap;
    private TextView tv_lastseen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cariq_mapview_fromnotification);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
        setUpToolbar();
    }

    private void init() {
        context = CarIqMapViewFromNotification_Activity.this;
        tv_lastseen = findViewById(R.id.tv_lastseen);
    }

    private void setDefaults() {

        LatLng latLng = new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")),
                Double.parseDouble(getIntent().getStringExtra("longitude")));
        addMapMarker(latLng);
        new GetAddress().execute(getIntent().getStringExtra("latitude"), getIntent().getStringExtra("longitude"));
    }

    private class GetAddress extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Address> doInBackground(String... params) {
            Geocoder geocoder;
            List<Address> addresses = null;

            try {
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(Double.parseDouble(params[0]), Double.parseDouble(params[1]), 1); // Here 1 represent max icon_location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);
            if (addresses != null && !addresses.isEmpty()) {
//                tv_lastseen.setText(Html.fromHtml("<b>" + "Detected on " + "</b>" + addresses.get(0).getAddressLine(0)));
                String s1 = "Detected on " + "<b>" + getIntent().getStringExtra("datetime") + "</br>";
                String s2 = " near " + "<b>" + addresses.get(0).getAddressLine(0) + "</br>";

                tv_lastseen.setText(Html.fromHtml(s1 + s2));
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setDefaults();

    }

    private void addMapMarker(LatLng latLng) {
        mMap.clear();
        if (latLng != null) {
            MarkerOptions marker = new MarkerOptions().position(latLng).title("");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));
            mMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(splitCamelCase(getIntent().getStringExtra("type")));
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
