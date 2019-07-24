package in.rto.collections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.adapters.GetMyCarListAdapter;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.models.MyCarListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;

public class CarIqCarsList_Activity extends AppCompatActivity {

    private static Context context;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerView rv_carlist;
    private TextView tv_resourcename, tv_mobile, tv_email;
    private LinearLayout ll_parent;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_car;
    private static String user_id;
    private static CarIqUserDetailsModel.ResultBean cariqdetails;
    private UserSessionManager session;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cariq_carslist);
        activity = this;
        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = CarIqCarsList_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_carlist = findViewById(R.id.rv_carlist);
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
        fab_add_car = findViewById(R.id.fab_add_car);
        tv_resourcename = findViewById(R.id.tv_resourcename);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_email = findViewById(R.id.tv_email);
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

            String enabledCarIq = session.getEnableCarTrackingDetails().get(
                    ApplicationConstants.CARIQ_ENABLED_CARID);

            if (enabledCarIq == null) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetCarList().execute(user_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        cariqdetails = (CarIqUserDetailsModel.ResultBean) getIntent().getSerializableExtra("cariqdetails");

        if (cariqdetails == null) {

            if (Utilities.isNetworkAvailable(context)) {
                new CheckUserRegistration().execute(user_id);
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            }
        } else {
            tv_resourcename.setText(cariqdetails.getFirst_name() + " " + cariqdetails.getLast_name() + " (" + cariqdetails.getUser_name() + ")");
            tv_mobile.setText(cariqdetails.getCell_number());
            tv_email.setText(cariqdetails.getEmail());
        }

        rv_carlist.setLayoutManager(new LinearLayoutManager(context));

        if (Utilities.isNetworkAvailable(context)) {
            new GetCarList().execute(user_id);
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_carlist.setVisibility(View.GONE);
        }
    }

    private void setEventHandler() {
        fab_add_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddMyCar_Activity.class)
                        .putExtra("cariqdetails", cariqdetails));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetCarList().execute(user_id);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_carlist.setVisibility(View.GONE);
                }
            }
        });
    }

    public static class GetCarList extends AsyncTask<String, Void, String> {

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
                            rv_carlist.setAdapter(new GetMyCarListAdapter(context, myCarList, "1"));
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

    private class CheckUserRegistration extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getuser"));
            param.add(new ParamsPojo("user_id", params[0]));
//            param.add(new ParamsPojo("user_id", "10"));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.USECARIQRAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {

                    ArrayList<CarIqUserDetailsModel.ResultBean> myCarList = new ArrayList<>();
                    CarIqUserDetailsModel pojoDetails = new Gson().fromJson(result, CarIqUserDetailsModel.class);
                    type = pojoDetails.getType();
                    if (type.equalsIgnoreCase("success")) {
                        session.createCarIqSession(result);
                        myCarList = pojoDetails.getResult();
                        cariqdetails = myCarList.get(0);
                        tv_resourcename.setText(cariqdetails.getFirst_name() + " " + cariqdetails.getLast_name() + " (" + cariqdetails.getUser_name() + ")");
                        tv_mobile.setText(cariqdetails.getCell_number());
                        tv_email.setText(cariqdetails.getEmail());
                    } else {
                        startActivity(new Intent(context, CariqUserRegistration_Activity.class));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("My Vehicles");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
