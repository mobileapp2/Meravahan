package in.rto.collections.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import in.rto.collections.R;
import in.rto.collections.models.TripDetailsListModel.ItsLastLocationListBean;

public class TripDetailsOnMap_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private Context context;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    ArrayList<ItsLastLocationListBean> tripDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripdetails_onmap);

        init();
        setUpToolbar();

    }

    private void init() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = TripDetailsOnMap_Activity.this;

        tripDetails = (ArrayList<ItsLastLocationListBean>)
                getIntent().getSerializableExtra("tripDetails");
    }

    private void setDefault() {
        MarkerOptions startMarker = new MarkerOptions();
        LatLng startLatLng = new LatLng(Double.parseDouble(tripDetails.get(0).getLatitude()), Double.parseDouble(tripDetails.get(0).getLongitude()));
        startMarker.icon(BitmapDescriptorFactory.defaultMarker());
        startMarker.position(startLatLng);
        googleMap.addMarker(startMarker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(tripDetails.get(0).getLatitude()), Double.parseDouble(tripDetails.get(0).getLongitude())))
                .zoom(15).build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        LatLng position = null;
        double lat = 0;
        double lng = 0;


        lineOptions = new PolylineOptions();
        // Fetching all the points in i-th route
        for (int j = 0; j < tripDetails.size(); j++) {
            points = new ArrayList<LatLng>();

            lat = Double.parseDouble(tripDetails.get(j).getLatitude());
            lng = Double.parseDouble(tripDetails.get(j).getLongitude());
            position = new LatLng(lat, lng);

            points.add(position);

            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(getResources().getColor(R.color.Ocean_Blue));
        }

        googleMap.addPolyline(lineOptions);


        MarkerOptions endMarker = new MarkerOptions();
        LatLng endLatLng = new LatLng(Double.parseDouble(tripDetails.get(tripDetails.size() - 1).getLatitude()), Double.parseDouble(tripDetails.get(tripDetails.size() - 1).getLongitude()));
        endMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        endMarker.position(endLatLng);
        googleMap.addMarker(endMarker);
    }

    @Override
    public void onMapReady(GoogleMap googleM) {
        googleMap = googleM;
        setDefault();
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Trip Map View");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
