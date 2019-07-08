package in.rto.collections.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.adapters.GetMyCarListAdapter;
import in.rto.collections.models.MyCarListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class VehicleForTracking_Fragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_carlist;
    private LinearLayout ll_nothingtoshow;
    private String user_id;
    private UserSessionManager session;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vehicle_fortracking, container, false);
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
        rv_carlist = rootView.findViewById(R.id.rv_carlist);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
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
    }

    private void setDefault() {
        rv_carlist.setLayoutManager(new LinearLayoutManager(context));

        if (Utilities.isNetworkAvailable(context)) {
            new GetCarList().execute(user_id);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_carlist.setVisibility(View.GONE);
        }
    }

    private void setEventListner() {
//        fab_add_car.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(context, AddMyCar_Activity.class)
//                        .putExtra("cariqdetails", cariqdetails));
//            }
//        });

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getDetails"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.VEHICLETRACKINGAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
                rv_carlist.setVisibility(View.VISIBLE);
                if (!result.equals("")) {

                    ArrayList<MyCarListModel.ResultBean> myCarList = new ArrayList<>();
                    MyCarListModel pojoDetails = new Gson().fromJson(result, MyCarListModel.class);
                    type = pojoDetails.getType();
                    if (type.equalsIgnoreCase("success")) {

                        myCarList = pojoDetails.getResult();

                        if (myCarList.size() == 0) {
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            rv_carlist.setVisibility(View.GONE);
                        } else {
                            rv_carlist.setVisibility(View.VISIBLE);
                            ll_nothingtoshow.setVisibility(View.GONE);
                        }
                        rv_carlist.setAdapter(new GetMyCarListAdapter(context, myCarList, "2"));
                    } else {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_carlist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
