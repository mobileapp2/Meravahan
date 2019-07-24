package in.rto.collections.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;

public class LastSeenTracking_Fragment extends Fragment implements OnMapReadyCallback {


    private static Context context;
    private static UserSessionManager session;
    private static GoogleMap mMap;
    private static LinearLayout ll_nothingtoshow, ll_maplayout;
    private static ProgressBar progressBar;
    private static TextView tv_lastseen, tv_message;
    private static CarIqUserDetailsModel.ResultBean cariqdetails;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_last_seen, container, false);
        context = getActivity();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init(rootView);
        getSessionDetails();
//        setDefault();
        setEventListner();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        ll_maplayout = rootView.findViewById(R.id.ll_maplayout);
        progressBar = rootView.findViewById(R.id.progressBar);
        tv_lastseen = rootView.findViewById(R.id.tv_lastseen);
        tv_message = rootView.findViewById(R.id.tv_message);
    }

    public static void getSessionDetails() {
        try {
            String user_info = session.getCarIqUserDetails().get(
                    ApplicationConstants.CARIQ_LOGIN);
            CarIqUserDetailsModel pojoDetails = new Gson().fromJson(user_info, CarIqUserDetailsModel.class);

            ArrayList<CarIqUserDetailsModel.ResultBean> myCarList = new ArrayList<>();
            myCarList = pojoDetails.getResult();
            cariqdetails = myCarList.get(0);

            String enabledCarIq = session.getEnableCarTrackingDetails().get(
                    ApplicationConstants.CARIQ_ENABLED_CARID);

            if (enabledCarIq == null) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                ll_maplayout.setVisibility(View.GONE);
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    new startLiveTracking().execute(enabledCarIq);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventListner() {

    }

    private static class startLiveTracking extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();

            Request request = new Request.Builder()
                    .url(ApplicationConstants.LASTSEENAPI + params[0])
                    .addHeader("content-type", "application/json")
                    .header("Authorization", Credentials.basic(cariqdetails.getUser_name(), getMd5(cariqdetails.getPassword())))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            try {
                if (!result.equals("")) {
                    JSONObject jsonObject = new JSONObject(result);
                    String latitude = jsonObject.getString("latitude");
                    String longitude = jsonObject.getString("longitude");

                    if (latitude.equals("null") || longitude.equals("null")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        ll_maplayout.setVisibility(View.GONE);
                        tv_message.setText("Last seen location is not available");
                    } else {
                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        addMapMarker(latLng);
                        new GetAddress().execute(latitude, longitude);
                        ll_nothingtoshow.setVisibility(View.GONE);
                        ll_maplayout.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class GetAddress extends AsyncTask<String, Void, List<Address>> {

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
                tv_lastseen.setText(Html.fromHtml("<b>" + "Last Seen : " + "</b>" + addresses.get(0).getAddressLine(0)));
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private static void addMapMarker(LatLng latLng) {
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
}
