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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.adapters.GetNotificationCarIqListAdapter;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.models.CariqNotificationModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;

public class NotificationsCariq_Activity extends Fragment {
    private Context context;
    private RecyclerView rv_productlist;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_nothingtoshow;
    private LinearLayout ll_parent;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private String user_id;
    private ArrayList<CariqNotificationModel.RowsBean> notificationInfoList;
    private CarIqUserDetailsModel.ResultBean cariqdetails;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifi_meravahan, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
//        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_productlist = rootView.findViewById(R.id.rv_productlist);
        ll_parent = rootView.findViewById(R.id.ll_parent);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(context);
        rv_productlist.setLayoutManager(layoutManager);

        notificationInfoList = new ArrayList<>();
    }

    private void getSessionData() {
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

            if (user_info == null) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_productlist.setVisibility(View.GONE);
                return;
            }


            CarIqUserDetailsModel pojoDetails = new Gson().fromJson(user_info, CarIqUserDetailsModel.class);

            ArrayList<CarIqUserDetailsModel.ResultBean> myCarList = new ArrayList<>();
            myCarList = pojoDetails.getResult();
            cariqdetails = myCarList.get(0);

            if (Utilities.isNetworkAvailable(context)) {
                new GetNotificationsInfoList().execute();
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                swipeRefreshLayout.setRefreshing(false);
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_productlist.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetNotificationsInfoList().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_productlist.setVisibility(View.GONE);
        }
    }

    private void setEventHandlers() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {

                    if (cariqdetails == null) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_productlist.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }


                    new GetNotificationsInfoList().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_productlist.setVisibility(View.GONE);
                }
            }
        });
    }

    private class GetNotificationsInfoList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_productlist.setVisibility(View.GONE);
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
                    .url(ApplicationConstants.CARIQNOTIFICATIONAPI + "All/1/100")
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
                    CariqNotificationModel pojoDetails = new Gson().fromJson(result, CariqNotificationModel.class);
                    notificationInfoList = new ArrayList<>();
                    notificationInfoList = pojoDetails.getRows();
                    if (notificationInfoList.size() > 0) {
                        rv_productlist.setVisibility(View.VISIBLE);
                        ll_nothingtoshow.setVisibility(View.GONE);
                        rv_productlist.setAdapter(new GetNotificationCarIqListAdapter(context, notificationInfoList));
                    } else {
                        rv_productlist.setVisibility(View.GONE);
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                rv_productlist.setVisibility(View.GONE);
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }
    }


}
