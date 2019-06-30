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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

public class LiveTracking_Fragment extends Fragment/* implements OnMapReadyCallback */ {

    private static Context context;
    //    private GoogleMap mMap;
    private static WebView webview;
    private static LinearLayout ll_nothingtoshow;
    private static ProgressBar progressBar;
    private static CarIqUserDetailsModel.ResultBean cariqdetails;
    private LatLng latLng;
    private static UserSessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_tracking, container, false);
        context = getActivity();

//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        init(rootView);
        getSessionDetails();
//        setDefault();
        setEventListner();
        return rootView;
    }


    private void init(View rootView) {
        session = new UserSessionManager(context);
        webview = rootView.findViewById(R.id.webview);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        progressBar = rootView.findViewById(R.id.progressBar);
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
                webview.setVisibility(View.GONE);
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
                    .url(ApplicationConstants.LIVETRACKINGAPI + params[0])
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
                    String trackingUrl = jsonObject.getString("url");

                    ll_nothingtoshow.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
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
