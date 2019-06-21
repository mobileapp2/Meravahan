package in.rto.collections.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.AddTyre_Activity;
import in.rto.collections.adapters.TyreListAdapter;
import in.rto.collections.models.TyreDetailsPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class Fragment_tyre extends Fragment {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView rto_tyre_list;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_tyre;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tyre, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }
    private void init(View rootView) {
        session = new UserSessionManager(context);
        fab_add_tyre = rootView.findViewById(R.id.fab_add_tyre);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rto_tyre_list = rootView.findViewById(R.id.rto_tyre_list);
        drawerlayout = getActivity().findViewById(R.id.drawerlayout);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        rto_tyre_list.setLayoutManager(layoutManager);
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            new Fragment_tyre.GetTyreList().execute(user_id);
        } else {
            Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rto_tyre_list.setVisibility(View.GONE);
        }
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
    }
    private void setEventHandlers() {
        fab_add_tyre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddTyre_Activity.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new Fragment_tyre.GetTyreList().execute(user_id);
                } else {
                    Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rto_tyre_list.setVisibility(View.GONE);
                }
            }
        });

    }

    public static class GetTyreList extends AsyncTask<String, Void, String> {
        private ArrayList<TyreDetailsPojo> tyreDetailsPojos;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rto_tyre_list.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "gettyre"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.TYREAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
                rto_tyre_list.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    tyreDetailsPojos = new ArrayList<TyreDetailsPojo>();
                    rto_tyre_list.setAdapter(new TyreListAdapter(context, tyreDetailsPojos));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                TyreDetailsPojo tyreDetailslist = new TyreDetailsPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                tyreDetailslist.setId(jsonObj.getString("id"));
                                tyreDetailslist.setRemark(jsonObj.getString("Remark"));
                                tyreDetailslist.setClient_id(jsonObj.getString("Name_of_the_Client"));
                                tyreDetailslist.setDescription(jsonObj.getString("Description"));
                                tyreDetailslist.setClient_name(jsonObj.getString("clientName"));
                                tyreDetailslist.setType_name(jsonObj.getString("TypeName"));
                                tyreDetailslist.setType_id(jsonObj.getString("Type"));
                                tyreDetailslist.setState_id(jsonObj.getString("State"));
                                tyreDetailslist.setStateName(jsonObj.getString("StateName"));
                                tyreDetailslist.setVehicle_no(jsonObj.getString("Vehicle_No"));
                                tyreDetailslist.setTyre_no(jsonObj.getString("Tyre_No"));
                                tyreDetailslist.setTyre_remounding_date(jsonObj.getString("Tyre_remounding_Date"));
                                tyreDetailslist.setTyre_replacement_date(jsonObj.getString("Tyre_Replacement_Date"));
                                tyreDetailslist.setPurchase_date(jsonObj.getString("Date_of_Purchase"));

                                ArrayList<TyreDetailsPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("tyre_dates").length(); j++) {
                                    TyreDetailsPojo.OtherDatesListPojo otherdateobj = new TyreDetailsPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("tyre_dates").getJSONObject(j).getString("tyre_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("tyre_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("tyre_dates").getJSONObject(j).getString("tyre_id"));
                                    otherDatesListPojos.add(otherdateobj);
                                }
                                tyreDetailslist.setOther_date(otherDatesListPojos);



                                ArrayList<TyreDetailsPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    TyreDetailsPojo.DocumentListPojo documentObj = new TyreDetailsPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDocument_id(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_id"));
                                    documentsList.add(documentObj);
                                }
                                tyreDetailslist.setDocument(documentsList);
                                tyreDetailsPojos.add(tyreDetailslist);
                            }
                            if (tyreDetailsPojos.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rto_tyre_list.setVisibility(View.GONE);
                            } else {
                                rto_tyre_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rto_tyre_list.setAdapter(new TyreListAdapter(context, tyreDetailsPojos));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rto_tyre_list.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rto_tyre_list.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}
