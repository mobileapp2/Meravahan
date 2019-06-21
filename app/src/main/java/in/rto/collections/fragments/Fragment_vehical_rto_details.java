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
import in.rto.collections.adapters.GetVehicleDealerDetailsListAdapter;
import in.rto.collections.models.VehicleDealerListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;


public class Fragment_vehical_rto_details extends Fragment {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView vehicle_dealer_list;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_dealer;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    static String user_id, role;
    private SearchView searchView;
    private static ArrayList<VehicleDealerListPojo> vehicleDealerListPojos, vehicleDealerListPojos1;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vehical_rto_details, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        fab_add_dealer = rootView.findViewById(R.id.fab_add_client);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        vehicle_dealer_list = rootView.findViewById(R.id.vehicle_dealer_list);
        drawerlayout = getActivity().findViewById(R.id.drawerlayout);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        vehicle_dealer_list.setLayoutManager(layoutManager);
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            new Fragment_vehical_rto_details.GetVehicleDealerList().execute(user_id);
            new Fragment_vehical_rto_details.GetBankerDealerList().execute(user_id);
        } else {
            Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            vehicle_dealer_list.setVisibility(View.GONE);
        }
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
            role = json.getString("role");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandlers() {


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new Fragment_vehical_rto_details.GetVehicleDealerList().execute(user_id);
                    new Fragment_vehical_rto_details.GetBankerDealerList().execute(user_id);
                } else {
                    Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    vehicle_dealer_list.setVisibility(View.GONE);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ArrayList<VehicleDealerListPojo> dealerSearchedList = new ArrayList<>();
                for (VehicleDealerListPojo dealer : vehicleDealerListPojos) {
                    String contactToBeSearched = dealer.getVehicle_owner().toLowerCase() + dealer.getVehicle_no().toLowerCase();
                    if (contactToBeSearched.contains(query.toLowerCase())) {
                        dealerSearchedList.add(dealer);
                    }
                }

                if (dealerSearchedList.size() == 0) {
                    vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, dealerSearchedList, user_id));
                } else {
                    vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, dealerSearchedList, user_id));
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<VehicleDealerListPojo> dealerSearchedList = new ArrayList<>();
                    for (VehicleDealerListPojo dealer : vehicleDealerListPojos) {
                        String contactToBeSearched = dealer.getVehicle_owner().toLowerCase() + dealer.getVehicle_no().toLowerCase();
                        if (contactToBeSearched.contains(newText.toLowerCase())) {
                            dealerSearchedList.add(dealer);
                        }
                    }
                    if (dealerSearchedList.size() == 0) {
                        vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, dealerSearchedList, user_id));
                    } else {
                        //bindRecyclerview(contactsSearchedList);
                        vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, dealerSearchedList, user_id));
                    }
                    return true;
                } else if (newText.equals("")) {
                    vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, vehicleDealerListPojos, user_id));
                    //bindRecyclerview(contactList);
                }
                return true;
            }
        });

    }

    public static class GetVehicleDealerList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            vehicle_dealer_list.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveRTODetails"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.VEHICLEDEALER, param);
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
                vehicle_dealer_list.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    vehicleDealerListPojos = new ArrayList<VehicleDealerListPojo>();
                    vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, vehicleDealerListPojos, user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                VehicleDealerListPojo dealerMainObj = new VehicleDealerListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                dealerMainObj.setId(jsonObj.getString("id"));
                                dealerMainObj.setChassis_no(jsonObj.getString("chassis_no"));
                                dealerMainObj.setDescription(jsonObj.getString("description"));
                                //dealerMainObj.setDescription(jsonObj.getString("alias"));
                                dealerMainObj.setEngine_no(jsonObj.getString("engine_no"));
                                dealerMainObj.setInsurance_policy_no(jsonObj.getString("insurance_policy_no"));
                                dealerMainObj.setInsurance_renewal_date(jsonObj.getString("insurance_renewal_date"));
                                dealerMainObj.setState_id(jsonObj.getString("state"));
                                dealerMainObj.setStateName(jsonObj.getString("sateName"));
                                dealerMainObj.setVehicle_owner(jsonObj.getString("vehicle_owner_name"));
                                dealerMainObj.setVehicle_no(jsonObj.getString("vechicle_no"));
                                ;
                                dealerMainObj.setRemark(jsonObj.getString("remark"));
                                // dealerMainObj.setClient_id(jsonObj.getString("client_name"));
                                // dealerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                dealerMainObj.setType_id(jsonObj.getString("rtotype"));
                                dealerMainObj.setType_name(jsonObj.getString("vType"));
                                dealerMainObj.setIsimport(jsonObj.getString("is_import_by_dealer"));
                                dealerMainObj.setImportR(jsonObj.getString("import"));
                                dealerMainObj.setCreated_by(jsonObj.getString("created_by"));
                                dealerMainObj.setIsshowto_customer(jsonObj.getString("is_show_to_customer"));
                                dealerMainObj.setIsshowto_rto(jsonObj.getString("is_show_to_dealer"));
                                dealerMainObj.setCreaterName(jsonObj.getString("createrName"));
                                dealerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                dealerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));
                                ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("other_dates").length(); j++) {
                                    VehicleDealerListPojo.OtherDatesListPojo otherdateobj = new VehicleDealerListPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_id"));
                                    otherDatesListPojos.add(otherdateobj);
                                }
                                dealerMainObj.setOther_date(otherDatesListPojos);


                                ArrayList<VehicleDealerListPojo.ServiceDatesListPojo> serviceDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("service_dates").length(); j++) {
                                    VehicleDealerListPojo.ServiceDatesListPojo servicedateobj = new VehicleDealerListPojo.ServiceDatesListPojo();
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_date"));
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("text"));
                                    servicedateobj.setService_date_id(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_id"));
                                    serviceDatesListPojos.add(servicedateobj);
                                }
                                dealerMainObj.setService_date(serviceDatesListPojos);

                                ArrayList<VehicleDealerListPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    VehicleDealerListPojo.DocumentListPojo documentObj = new VehicleDealerListPojo.DocumentListPojo();
                                    documentObj.setOriginal_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("original_name"));
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDocument_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentsList.add(documentObj);
                                }
                                dealerMainObj.setDocument(documentsList);
                                vehicleDealerListPojos.add(dealerMainObj);
                            }
                            if (vehicleDealerListPojos.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                vehicle_dealer_list.setVisibility(View.GONE);
                            } else {
                                vehicle_dealer_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, vehicleDealerListPojos, user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        vehicle_dealer_list.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                vehicle_dealer_list.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    public static class GetBankerDealerList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            vehicle_dealer_list.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "giveBankerDetails"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.VEHICLEDEALER, param);
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
                vehicle_dealer_list.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    vehicleDealerListPojos1 = new ArrayList<VehicleDealerListPojo>();
                    vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, vehicleDealerListPojos, user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                VehicleDealerListPojo dealerMainObj = new VehicleDealerListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                dealerMainObj.setId(jsonObj.getString("id"));
                                dealerMainObj.setBank_name(jsonObj.getString("bank_name"));
                                dealerMainObj.setBranch_name(jsonObj.getString("branch_name"));
                                dealerMainObj.setBorrower_name(jsonObj.getString("borrower"));
                                dealerMainObj.setVehicle_owner(jsonObj.getString("borrower"));
                                dealerMainObj.setDate_to_section(jsonObj.getString("date_to_section"));
                                dealerMainObj.setLoan_amount(jsonObj.getString("loan_amount"));
                                dealerMainObj.setLoan_account_number(jsonObj.getString("loan_account_number"));
                                dealerMainObj.setInstallment_amount(jsonObj.getString("installment_amount"));
                                dealerMainObj.setInstallment_start_date(jsonObj.getString("installment_start_date"));
                                dealerMainObj.setInstallment_end_date(jsonObj.getString("installment_end_date"));
                                dealerMainObj.setFrequency_id(jsonObj.getString("frequency"));
                                dealerMainObj.setFrequency(jsonObj.getString("frq"));
                                dealerMainObj.setRemark(jsonObj.getString("remark"));
                                dealerMainObj.setVehicle_no(jsonObj.getString("vehicle_number"));
                                dealerMainObj.setClient_id(jsonObj.getString("client_id"));
                                dealerMainObj.setDescription(jsonObj.getString("description"));
                                dealerMainObj.setClient_name(jsonObj.getString("clientName"));
                                dealerMainObj.setPurchase_date(jsonObj.getString("date_of_purchase"));
                                dealerMainObj.setImportR(jsonObj.getString("import"));
                                dealerMainObj.setCreated_by(jsonObj.getString("created_by"));
                                dealerMainObj.setCreaterName(jsonObj.getString("createrName"));
                                dealerMainObj.setIsimport(jsonObj.getString("is_import_by_dealer"));
                                dealerMainObj.setIsshowto_customer(jsonObj.getString("is_show_to_customer"));
                                dealerMainObj.setIsshowto_rto(jsonObj.getString("is_show_to_dealer"));
                                dealerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                dealerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));

                                ArrayList<VehicleDealerListPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    VehicleDealerListPojo.DocumentListPojo documentObj = new VehicleDealerListPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDocument_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setOriginal_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("doc_name"));
                                    documentObj.setDocument_id(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_id"));
                                    documentsList.add(documentObj);
                                }
                                dealerMainObj.setDocument(documentsList);
                                vehicleDealerListPojos1.add(dealerMainObj);
                            }
                            if (vehicleDealerListPojos1.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                vehicle_dealer_list.setVisibility(View.GONE);
                            } else {
                                vehicle_dealer_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            vehicleDealerListPojos.addAll(vehicleDealerListPojos1);
                            vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, vehicleDealerListPojos, user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        if (vehicleDealerListPojos.size() > 0) {
                            vehicle_dealer_list.setAdapter(new GetVehicleDealerDetailsListAdapter(context, vehicleDealerListPojos, user_id));
                        } else {
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            vehicle_dealer_list.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                vehicle_dealer_list.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}