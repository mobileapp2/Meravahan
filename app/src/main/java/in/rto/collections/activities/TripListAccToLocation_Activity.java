package in.rto.collections.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.models.TripDetailsListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;

public class TripListAccToLocation_Activity extends AppCompatActivity {

    private Context context;
    private LinearLayout ll_parent, ll_nothingtoshow;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_carlist;
    private String selectedDate;
    private CarIqUserDetailsModel.ResultBean cariqdetails;
    private String enabledCarIq;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triplist_acctolocation);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = TripListAccToLocation_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_carlist = findViewById(R.id.rv_carlist);
        rv_carlist.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setDefault() {
        selectedDate = getIntent().getStringExtra("selectedDate");

        if (enabledCarIq == null) {
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_carlist.setVisibility(View.GONE);
        } else {
            if (Utilities.isNetworkAvailable(context)) {
                new GetTripList().execute();
            } else {
                Utilities.showSnackBar(ll_parent, "Please check your internet connection");
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_carlist.setVisibility(View.GONE);
            }
        }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (enabledCarIq == null) {
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_carlist.setVisibility(View.GONE);
                } else {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetTripList().execute();
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please check your internet connection");
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_carlist.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private class GetTripList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
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
                    .url(ApplicationConstants.TRIPSDETAILSAPI + selectedDate + " 00:00:00" + "/" + selectedDate + " 23:59:59" + "/" + enabledCarIq)
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
            try {
                if (!result.equals("")) {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    ArrayList<TripDetailsListModel.ItsLastLocationListBean> tripsList = new ArrayList<>();
                    TripDetailsListModel pojoDetails = new Gson().fromJson(result, TripDetailsListModel.class);

                    tripsList = pojoDetails.getItsLastLocationList();

                    ArrayList<ArrayList<TripDetailsListModel.ItsLastLocationListBean>> arrayLists = new ArrayList<>();

                    ArrayList<TripDetailsListModel.ItsLastLocationListBean> arratTripList = new ArrayList<>();
                    for (int i = 0; i < tripsList.size(); i++) {

                        if (i == 0) {
                            arratTripList.add(tripsList.get(i));
                        } else {
                            Log.i("TIME DIFF", (((format.parse(tripsList.get(i).getItsTimeStamp()).getTime() - format.parse(tripsList.get(i - 1).getItsTimeStamp()).getTime()) / (60 * 1000) % 60)) + "");

                            if (((format.parse(tripsList.get(i).getItsTimeStamp()).getTime() - format.parse(tripsList.get(i - 1).getItsTimeStamp()).getTime()) / (60 * 1000) % 60) > 15) {
//                                arratTripList.add(tripsList.get(i));
                                arrayLists.add(arratTripList);
                                arratTripList = new ArrayList<>();
                            } else {
                                arratTripList.add(tripsList.get(i));
                                if (i == tripsList.size() - 1) {
                                    arrayLists.add(arratTripList);
                                    arratTripList = new ArrayList<>();
                                }
                            }
                        }
                    }
                    if (arrayLists.size() == 0) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_carlist.setVisibility(View.GONE);
                    } else {
                        rv_carlist.setVisibility(View.VISIBLE);
                        ll_nothingtoshow.setVisibility(View.GONE);
                    }
                    rv_carlist.setAdapter(new GetTripDetailsListAdapter(context, arrayLists));
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_carlist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    public class GetTripDetailsListAdapter extends RecyclerView.Adapter<GetTripDetailsListAdapter.MyViewHolder> {

        private ArrayList<ArrayList<TripDetailsListModel.ItsLastLocationListBean>> resultArrayList;
        private Context context;
        private Geocoder geocoder;

        public GetTripDetailsListAdapter(Context context, ArrayList<ArrayList<TripDetailsListModel.ItsLastLocationListBean>> resultArrayList) {
            this.context = context;
            this.resultArrayList = resultArrayList;
            geocoder = new Geocoder(context, Locale.getDefault());
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_tripdetails, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final ArrayList<TripDetailsListModel.ItsLastLocationListBean> tripDetails = resultArrayList.get(position);

            if (tripDetails.size() > 0) {
                holder.tv_start_time.setText(changeDateFormat(tripDetails.get(0).getItsTimeStamp()));
                holder.tv_end_time.setText(changeDateFormat(tripDetails.get(tripDetails.size() - 1).getItsTimeStamp()));
                try {
                    List<Address> starLocation =
                            geocoder.getFromLocation(Double.parseDouble(tripDetails.get(0).getLatitude()),
                                    Double.parseDouble(tripDetails.get(0).getLongitude()), 1);
                    List<Address> endLocation =
                            geocoder.getFromLocation(Double.parseDouble(tripDetails.get(tripDetails.size() - 1).getLatitude()),
                                    Double.parseDouble(tripDetails.get(tripDetails.size() - 1).getLongitude()), 1);

                    holder.tv_start_location.setText(starLocation.get(0).getAddressLine(0));
                    holder.tv_end_location.setText(endLocation.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                holder.tv_viewonmap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(context, TripDetailsOnMap_Activity.class)
                                .putExtra("tripDetails", tripDetails));
                    }
                });

            }



        }

        @Override
        public int getItemCount() {
            return resultArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            private TextView tv_start_time, tv_start_location, tv_end_time, tv_end_location, tv_viewonmap;

            public MyViewHolder(View view) {
                super(view);

                tv_start_time = view.findViewById(R.id.tv_start_time);
                tv_start_location = view.findViewById(R.id.tv_start_location);
                tv_end_time = view.findViewById(R.id.tv_end_time);
                tv_end_location = view.findViewById(R.id.tv_end_location);
                tv_viewonmap = view.findViewById(R.id.tv_viewonmap);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private String changeDateFormat(String dateString) {
        String result = "";
        if (dateString.equals("")) {
            return "";
        }
        SimpleDateFormat formatterOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatterNew = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date date = null;
        try {
            date = formatterOld.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            result = formatterNew.format(date);
        }
        return result;
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Trip List");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
