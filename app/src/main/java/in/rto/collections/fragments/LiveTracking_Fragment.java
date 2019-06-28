package in.rto.collections.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class LiveTracking_Fragment extends Fragment/* implements OnMapReadyCallback */{

    private Context context;
//    private GoogleMap mMap;
    private WebView webview;
    private CarIqUserDetailsModel.ResultBean cariqdetails;
    private LatLng latLng;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_tracking, container, false);
        context = getActivity();

//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        init(rootView);
        getSessionDetails();
        setDefault();
        setEventListner();
        return rootView;
    }

    private void getSessionDetails() {
        try {
            UserSessionManager session = new UserSessionManager(context);
            String user_info = session.getCarIqUserDetails().get(
                    ApplicationConstants.CARIQ_LOGIN);
            CarIqUserDetailsModel pojoDetails = new Gson().fromJson(user_info, CarIqUserDetailsModel.class);

            ArrayList<CarIqUserDetailsModel.ResultBean> myCarList = new ArrayList<>();
            myCarList = pojoDetails.getResult();
            cariqdetails = myCarList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init(View rootView) {
        webview = rootView.findViewById(R.id.webview);
    }

    private void setDefault() {
//        addMapMarker();
        if (Utilities.isNetworkAvailable(context)) {
            new GetMakerList().execute();
        }
    }

    private void setEventListner() {
    }


    private class GetMakerList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    .url(ApplicationConstants.LIVETRACKINGAPI + "5057")
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
            try {
                if (!result.equals("")) {
                    JSONObject jsonObject = new JSONObject(result);
                    String trackingUrl = jsonObject.getString("url");

//                    wv_form.setWebViewClient(new MyWebViewClient());
                    webview.getSettings().setJavaScriptEnabled(true);
                    webview.loadUrl(trackingUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//    }
}
