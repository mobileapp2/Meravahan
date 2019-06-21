package in.rto.collections.fragments;

import android.content.Context;
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
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.adapters.GetRTOAgentDetailListAdapter;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class Fragment_RTO_Dealer_Details extends Fragment {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView rto_agent_list;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_agent;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static String user_id;
    private SearchView searchView;
    private static String role;
    private static ArrayList<RTOAgentListPojo> rtoAgentListPojos;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__rto__dealer__details, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }


    private void init(View rootView) {
        session = new UserSessionManager(context);
       // fab_add_agent = rootView.findViewById(R.id.fab_add_agent);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rto_agent_list = rootView.findViewById(R.id.rto_agent_list);
        drawerlayout = getActivity().findViewById(R.id.drawerlayout);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        rto_agent_list.setLayoutManager(layoutManager);
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            new Fragment_RTO_Dealer_Details.GetRTOAgentList().execute(user_id);
        } else {
            Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rto_agent_list.setVisibility(View.GONE);
        }
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
            role = json.getString("role_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandlers() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new Fragment_RTO_Dealer_Details.GetRTOAgentList().execute(user_id);
                } else {
                    Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rto_agent_list.setVisibility(View.GONE);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ArrayList<RTOAgentListPojo> rtoSearchedList = new ArrayList<>();
                for (RTOAgentListPojo rto : rtoAgentListPojos ) {
                    String contactToBeSearched = rto.getVehicle_owner().toLowerCase()+ rto.getVehicle_no().toLowerCase();
                    if (contactToBeSearched.contains(query.toLowerCase())) {
                        rtoSearchedList.add(rto);
                    }
                }

                if (rtoSearchedList.size() == 0) {
                    rto_agent_list.setAdapter(new GetRTOAgentDetailListAdapter(context, rtoSearchedList,user_id));
                } else {
                    rto_agent_list.setAdapter(new GetRTOAgentDetailListAdapter(context, rtoSearchedList,user_id));

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<RTOAgentListPojo> rtoSearchedList = new ArrayList<>();
                    for (RTOAgentListPojo rto : rtoAgentListPojos) {
                        String contactToBeSearched = rto.getVehicle_owner().toLowerCase()+rto.getVehicle_no().toLowerCase();
                        if (contactToBeSearched.contains(newText.toLowerCase())) {
                            rtoSearchedList.add(rto);
                        }
                    }
                    if (rtoSearchedList.size() == 0) {
                        rto_agent_list.setAdapter(new GetRTOAgentDetailListAdapter(context, rtoSearchedList,user_id));
                    } else {
                        //bindRecyclerview(contactsSearchedList);
                        rto_agent_list.setAdapter(new GetRTOAgentDetailListAdapter(context, rtoSearchedList,user_id));

                    }
                    return true;
                } else if (newText.equals("")) {
                    rto_agent_list.setAdapter(new GetRTOAgentDetailListAdapter(context, rtoAgentListPojos,user_id));
                    //customer_list.setAdapter(new CustomerAdapter(context,customerPojos,user_id));
                    //bindRecyclerview(contactList);
                }
                return true;
            }
        });

    }

    public static class GetRTOAgentList extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rto_agent_list.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getRTODealerRecored"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.RTOAGENTAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
//                shimmer_view_container.stopShimmer();
//                shimmer_view_container.setVisibility(View.GONE);
                rto_agent_list.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    rtoAgentListPojos = new ArrayList<RTOAgentListPojo>();
                    rto_agent_list.setAdapter(new GetRTOAgentDetailListAdapter(context, rtoAgentListPojos,user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                RTOAgentListPojo agentdealerMainObj = new RTOAgentListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                agentdealerMainObj.setId(jsonObj.getString("id"));
                                agentdealerMainObj.setRemark(jsonObj.getString("remark"));
                                agentdealerMainObj.setDescription(jsonObj.getString("description"));
                               // agentdealerMainObj.setClient_id(jsonObj.getString("client_name"));
                               // agentdealerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                agentdealerMainObj.setType_name(jsonObj.getString("vType"));
                                agentdealerMainObj.setType_id(jsonObj.getString("type"));
                                agentdealerMainObj.setChassis_no(jsonObj.getString("chassis_no"));
                                agentdealerMainObj.setEngine_no(jsonObj.getString("engine_no"));
                                agentdealerMainObj.setInsurance_policy_no(jsonObj.getString("insurance_policy_no"));
                                agentdealerMainObj.setInsurance_renewal_date(jsonObj.getString("insurance_renewal_date"));
                                agentdealerMainObj.setState_id(jsonObj.getString("state"));
                                agentdealerMainObj.setStateName(jsonObj.getString("StateName"));
                                agentdealerMainObj.setVehicle_owner(jsonObj.getString("vehicle_owner"));
                                agentdealerMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                agentdealerMainObj.setPuc_renewal_date(jsonObj.getString("puc_renewal_date"));
                                agentdealerMainObj.setPermit_valid_upto(jsonObj.getString("permit_valid_upto"));
                                agentdealerMainObj.setTax_paid_up_to(jsonObj.getString("tax_paid_upto"));
                                agentdealerMainObj.setNational_permit_valid_upto(jsonObj.getString("national_permit_upto"));
                                agentdealerMainObj.setState_permit_valid_upto(jsonObj.getString("state_permit_valid_upto"));
                                agentdealerMainObj.setFittness_valid_upto(jsonObj.getString("fitness_valid_upto"));
                                agentdealerMainObj.setIsimport(jsonObj.getString("is_import_by_rto"));
                                agentdealerMainObj.setImportR(jsonObj.getString("import"));
                                agentdealerMainObj.setCreated_by(jsonObj.getString("created_by"));
                                agentdealerMainObj.setIsshowto_customer(jsonObj.getString("is_show_to_customer"));
                                agentdealerMainObj.setIsshowto_dealer(jsonObj.getString("is_show_to_rto"));
                                agentdealerMainObj.setCreaterName(jsonObj.getString("createrName"));
                                agentdealerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                agentdealerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));
                                ArrayList<RTOAgentListPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("rto_dates").length(); j++) {
                                    RTOAgentListPojo.OtherDatesListPojo otherdateobj = new RTOAgentListPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("rto_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("text"));
                                   // otherdateobj.setOther_date_id(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("rto_id"));
                                    otherDatesListPojos.add(otherdateobj);
                                }
                                agentdealerMainObj.setOther_date(otherDatesListPojos);



                                ArrayList<RTOAgentListPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    RTOAgentListPojo.DocumentListPojo documentObj = new RTOAgentListPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDoc_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("doc_name"));
                                    documentObj.setName(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setDocument_id(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_id"));
                                    documentsList.add(documentObj);
                                }
                                agentdealerMainObj.setDocument(documentsList);
                                rtoAgentListPojos.add(agentdealerMainObj);
                            }
                            if (rtoAgentListPojos.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rto_agent_list.setVisibility(View.GONE);
                            } else {
                                rto_agent_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rto_agent_list.setAdapter(new GetRTOAgentDetailListAdapter(context, rtoAgentListPojos,user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rto_agent_list.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rto_agent_list.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }



}