package in.rto.collections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.models.CariqAlertSettingsModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;
import static in.rto.collections.utilities.Utilities.splitCamelCase;

public class CariqCarAlertSettings_Activity extends AppCompatActivity {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_alertlist;
    private LinearLayout ll_parent, ll_nothingtoshow;
    private String user_id;
    private static CarIqUserDetailsModel.ResultBean cariqdetails;
    private UserSessionManager session;
    private ArrayList<CariqAlertSettingsModel.TypesBean> alertSettingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cariqcar_alertsettings);

        init();
        getSessionDetails();
        setDefaults();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = CariqCarAlertSettings_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_alertlist = findViewById(R.id.rv_alertlist);
        rv_alertlist.setLayoutManager(new LinearLayoutManager(context));
        alertSettingsList = new ArrayList<>();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaults() {

        if (cariqdetails == null) {
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_alertlist.setVisibility(View.GONE);
        } else {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAlertsList().execute();
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                swipeRefreshLayout.setRefreshing(false);
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_alertlist.setVisibility(View.GONE);
            }
        }
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetAlertsList().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_alertlist.setVisibility(View.GONE);
                }
            }
        });
    }

    private class GetAlertsList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_alertlist.setVisibility(View.GONE);
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
                    .url(ApplicationConstants.CARIQALERTSETTINGSAPI + "/" + getIntent().getStringExtra("carId"))
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
                swipeRefreshLayout.setRefreshing(false);
                if (!result.equals("")) {
                    CariqAlertSettingsModel pojoDetails = new Gson().fromJson(result, CariqAlertSettingsModel.class);
                    alertSettingsList = new ArrayList<>();
                    alertSettingsList = pojoDetails.getTypes();
                    if (alertSettingsList.size() > 0) {
                        rv_alertlist.setVisibility(View.VISIBLE);
                        ll_nothingtoshow.setVisibility(View.GONE);
                        rv_alertlist.setAdapter(new AlertSettingsListAdapter());
                    } else {
                        rv_alertlist.setVisibility(View.GONE);
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                rv_alertlist.setVisibility(View.GONE);
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }
    }

    private class AlertSettingsListAdapter extends RecyclerView.Adapter<AlertSettingsListAdapter.MyViewHolder> {

        private AlertSettingsListAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_alert_settings, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final CariqAlertSettingsModel.TypesBean cardetails = alertSettingsList.get(position);

            holder.tv_alert.setText(splitCamelCase(cardetails.getType()));
            holder.sw_onoff.setChecked(cardetails.isIsOn());

            holder.sw_onoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new SetAlertSettings().execute(cardetails.getType(), getIntent().getStringExtra("carId"), String.valueOf(holder.sw_onoff.isChecked()));
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return alertSettingsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_alert;
            private Switch sw_onoff;

            public MyViewHolder(View view) {
                super(view);
                tv_alert = view.findViewById(R.id.tv_alert);
                sw_onoff = view.findViewById(R.id.sw_onoff);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class SetAlertSettings extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Saving changes...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();

            JsonObject regObj = new JsonObject();
            regObj.addProperty("isOn", Boolean.valueOf(params[2]));

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, regObj.toString());
            Request request = new Request.Builder()
                    .url(ApplicationConstants.CARIQSETALERTSETTINGSAPI + "/" + params[0] + "/" + params[1])
                    .put(body)
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
            pd.dismiss();
            super.onPostExecute(result);

        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Alert Settings");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
