package in.rto.collections.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.adapters.CarListForAlertSettingsAdapter;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.models.MyCarListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;

public class CarIqCarListAlertsSettings_Activity extends AppCompatActivity {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_carlist;
    private LinearLayout ll_parent;
    private LinearLayout ll_nothingtoshow;
    private String user_id;
    private CarIqUserDetailsModel.ResultBean cariqdetails;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cariq_alertssettings);

        init();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = CarIqCarListAlertsSettings_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_carlist = findViewById(R.id.rv_carlist);
        rv_carlist.setLayoutManager(new LinearLayoutManager(context));
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String user_info = session.getCarIqUserDetails().get(
                    ApplicationConstants.CARIQ_LOGIN);
            CarIqUserDetailsModel pojoDetails = new Gson().fromJson(user_info, CarIqUserDetailsModel.class);

            ArrayList<CarIqUserDetailsModel.ResultBean> myCarList = new ArrayList<>();
            myCarList = pojoDetails.getResult();
            cariqdetails = myCarList.get(0);

            if (cariqdetails == null) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_carlist.setVisibility(View.GONE);
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetCarList().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_carlist.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetCarList().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_carlist.setVisibility(View.GONE);
                }
            }
        });
    }

    private class GetCarList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_carlist.setVisibility(View.GONE);
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
                    .url(ApplicationConstants.CARIQCARLISTAPI)
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
                    JSONArray jsonarr = new JSONArray(result);
                    ArrayList<MyCarListModel> myCarList = new ArrayList<>();
                    if (jsonarr.length() > 0) {
                        for (int i = 0; i < jsonarr.length(); i++) {
                            MyCarListModel customerMainObj = new MyCarListModel();
                            JSONObject jsonObj = jsonarr.getJSONObject(i);
                            customerMainObj.setId(jsonObj.getString("id"));
                            customerMainObj.setMake(jsonObj.getString("make"));
                            customerMainObj.setModel(jsonObj.getString("model"));
                            customerMainObj.setFuelType(jsonObj.getString("fuelType"));
                            customerMainObj.setRegistrationNumber(jsonObj.getString("registrationNumber"));
                            myCarList.add(customerMainObj);
                        }
                        if (myCarList.size() != 0) {

                            rv_carlist.setVisibility(View.VISIBLE);
                            ll_nothingtoshow.setVisibility(View.GONE);
                            rv_carlist.setAdapter(new CarListForAlertSettingsAdapter(context, myCarList));
                        } else {
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            rv_carlist.setVisibility(View.GONE);
                        }

                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_carlist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Select Vehicle");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
