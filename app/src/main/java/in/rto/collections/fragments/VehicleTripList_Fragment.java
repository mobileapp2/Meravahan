package in.rto.collections.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.adapters.GetTripListAdapter;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.models.TripListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;

public class VehicleTripList_Fragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_triplist;
    private LinearLayout ll_nothingtoshow;
    private ProgressBar progressBar;
    private UserSessionManager session;
    private CarIqUserDetailsModel.ResultBean cariqdetails;
    private String enabledCarIq;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vehicle_trips, container, false);
        context = getActivity();
        init(rootView);
        getSessionDetails();
        setDefault();
        setEventListner();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_triplist = rootView.findViewById(R.id.rv_triplist);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        progressBar = rootView.findViewById(R.id.progressBar);
        rv_triplist.setLayoutManager(new LinearLayoutManager(context));
    }

    private void getSessionDetails() {

        try {
            String user_info = session.getCarIqUserDetails().get(
                    ApplicationConstants.CARIQ_LOGIN);
            CarIqUserDetailsModel pojoDetails = new Gson().fromJson(user_info, CarIqUserDetailsModel.class);

            ArrayList<CarIqUserDetailsModel.ResultBean> tripsList = new ArrayList<>();
            tripsList = pojoDetails.getResult();
            cariqdetails = tripsList.get(0);

            enabledCarIq = session.getEnableCarTrackingDetails().get(
                    ApplicationConstants.CARIQ_ENABLED_CARID);

            if (enabledCarIq == null) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rv_triplist.setVisibility(View.GONE);
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetTripList().execute();
                } else {
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    rv_triplist.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {

    }

    private void setEventListner() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetTripList().execute();
                } else {
                    Utilities.showMessageString(context, "Please check internet connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_triplist.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private class GetTripList extends AsyncTask<String, Void, String> {

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
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

            Request request = new Request.Builder()
                    .url(ApplicationConstants.TRIPSAPI + "/" + enabledCarIq + "/" + timeStamp + "/" + "100")
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
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            try {
                if (!result.equals("")) {

                    ArrayList<TripListModel> tripsList = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        TripListModel tripListModel = new TripListModel();
                        tripListModel.setTripDate(object.getString("tripDate"));
                        tripListModel.setKmCovered(object.getString("kmCovered"));
                        tripListModel.setAvgSpeed(object.getString("avgSpeed"));
                        tripsList.add(tripListModel);
                    }


                    if (tripsList.size() == 0) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_triplist.setVisibility(View.GONE);
                    } else {
                        rv_triplist.setVisibility(View.VISIBLE);
                        ll_nothingtoshow.setVisibility(View.GONE);
                    }
                    rv_triplist.setAdapter(new GetTripListAdapter(context, tripsList));
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_triplist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }


}
