package in.rto.collections.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.adapters.DealerLinkAdapter;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.VehicleDealerListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class LinkToDealer_Activity extends Activity {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView vehicle_dealer_list;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_dealer;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static String user_id;
    private SearchView searchView;
    private static ArrayList<LinkPojo> linkPojos;
    private static LinkPojo linkPojo;
    private static ArrayList<VehicleDealerListPojo> vehicleDealerListPojos;
    private static ArrayList<VehicleDealerListPojo> vehicleDealerListToLinkPojos;
    public static int selectedposition = 0;
    private ImageView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_to_dealer);
        context = LinkToDealer_Activity.this;
        init();
        getSessionData();
        setUpToolbar();
        setDefault();
        setEventHandlers();
    }

    private void init() {
        session = new UserSessionManager(context);
        fab_add_dealer = findViewById(R.id.fab_add_client);
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
        vehicle_dealer_list = findViewById(R.id.vehicle_dealer_list);
        drawerlayout = findViewById(R.id.drawerlayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchView = findViewById(R.id.searchView);
        next = findViewById(R.id.img_save);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        vehicle_dealer_list.setLayoutManager(layoutManager);
        linkPojo = (LinkPojo) getIntent().getSerializableExtra("rtoDetails");
        vehicleDealerListToLinkPojos = new ArrayList<VehicleDealerListPojo>();
    }

    public void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Link To");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new LinkToDealer_Activity.GetVehicleDealerList().execute(user_id);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandlers() {
        fab_add_dealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddvehicleDealer_Activity.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new LinkToDealer_Activity.GetVehicleDealerList().execute(user_id);
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
                    Utilities.showAlertDialog(context, "Fail", "No Such Vehicle Details Found", false);
                    //  searchView.setQuery("", false);
                    // bindRecyclerview(contactList);
                } else {
                    vehicleDealerListToLinkPojos = dealerSearchedList;
                    vehicle_dealer_list.setAdapter(new DealerLinkAdapter(context, dealerSearchedList, linkPojos));
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
                        Utilities.showMessageString(context, "No Such Vehicle Details Found");
                        //   searchView.setQuery("", false);
                        //   bindRecyclerview(contactList);
                    } else {
                        //bindRecyclerview(contactsSearchedList);
                        vehicleDealerListToLinkPojos = dealerSearchedList;
                        vehicle_dealer_list.setAdapter(new DealerLinkAdapter(context, dealerSearchedList, linkPojos));
                    }
                    return true;
                } else if (newText.equals("")) {
                    vehicleDealerListToLinkPojos = vehicleDealerListPojos;
                    vehicle_dealer_list.setAdapter(new DealerLinkAdapter(context, vehicleDealerListPojos, linkPojos));
                    //bindRecyclerview(contactList);
                }
                return true;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedposition != -1) {
                    VehicleDealerListPojo vehicleDealerListPojo = vehicleDealerListToLinkPojos.get(selectedposition);
                    Intent intent = new Intent(context, Link_Edit_Dealer.class);
                    intent.putExtra("vehicleDetails", vehicleDealerListPojo);
                    intent.putExtra("rtoDetails", linkPojo);
                    context.startActivity(intent);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Please Select Record To Link.");
                    builder.setIcon(R.drawable.ic_success_24dp);
                    builder.setTitle("Success");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();
                }

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
            param.add(new ParamsPojo("type", "getAllVehicleDetails"));
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
                    linkPojos = new ArrayList<LinkPojo>();
                    vehicle_dealer_list.setAdapter(new DealerLinkAdapter(context, vehicleDealerListPojos, linkPojos));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                VehicleDealerListPojo dealerMainObj = new VehicleDealerListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                dealerMainObj.setId(jsonObj.getString("id"));
                                dealerMainObj.setChassis_no(jsonObj.getString("chassis_no"));
                                dealerMainObj.setDescription(jsonObj.getString("description"));
                                dealerMainObj.setEngine_no(jsonObj.getString("engine_no"));
                                dealerMainObj.setHypothecated_to(jsonObj.getString("hypothecated_to"));
                                dealerMainObj.setInsurance_policy_no(jsonObj.getString("insurance_policy_no"));
                                dealerMainObj.setInsurance_renewal_date(jsonObj.getString("insurance_renewal_date"));
                                dealerMainObj.setPurchase_date(jsonObj.getString("purchase_date"));
                                dealerMainObj.setState_id(jsonObj.getString("state"));
                                dealerMainObj.setStateName(jsonObj.getString("StateName"));
                                dealerMainObj.setTem_reg_no(jsonObj.getString("tem_reg_no"));
                                dealerMainObj.setVehicle_owner(jsonObj.getString("vehicle_owner"));
                                dealerMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                dealerMainObj.setRto_agent_id(jsonObj.getString("rto_agent"));
                                dealerMainObj.setRto_agent_name(jsonObj.getString("rtoAgentName"));
                                dealerMainObj.setRemark(jsonObj.getString("remark"));
                                dealerMainObj.setClient_id(jsonObj.getString("client_name"));
                                dealerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                dealerMainObj.setType_id(jsonObj.getString("type"));
                                dealerMainObj.setType_name(jsonObj.getString("vType"));
                                dealerMainObj.setImportR(jsonObj.getString("import"));
                                dealerMainObj.setIsshowto_customer(jsonObj.getString("is_show_to_customer"));
                                dealerMainObj.setIsshowto_rto(jsonObj.getString("is_show_to_rto"));

                                dealerMainObj.setBank_name(jsonObj.getString("bank_name"));
                                dealerMainObj.setBranch_name(jsonObj.getString("branch_name"));
                                dealerMainObj.setBorrower_name(jsonObj.getString("borrower_name"));
                                dealerMainObj.setDate_to_section(jsonObj.getString("date_of_saction"));
                                dealerMainObj.setLoan_amount(jsonObj.getString("loan_amount"));
                                dealerMainObj.setLoan_account_number(jsonObj.getString("loan_account_number"));
                                dealerMainObj.setInstallment_amount(jsonObj.getString("installment_amount"));
                                dealerMainObj.setInstallment_start_date(jsonObj.getString("installment_start_date"));
                                dealerMainObj.setInstallment_end_date(jsonObj.getString("installment_end_date"));
                                dealerMainObj.setFrequency_id(jsonObj.getString("frqquency"));
                                dealerMainObj.setFrequency(jsonObj.getString("frq"));
                                ArrayList<VehicleDealerListPojo.ServiceDatesListPojo> serviceDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("service_dates").length(); j++) {
                                    VehicleDealerListPojo.ServiceDatesListPojo servicedateobj = new VehicleDealerListPojo.ServiceDatesListPojo();
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_date"));
                                    servicedateobj.setService_date_id(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_id"));
                                    servicedateobj.setText(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("text"));
                                    serviceDatesListPojos.add(servicedateobj);
                                }
                                dealerMainObj.setService_date(serviceDatesListPojos);


                                ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("other_dates").length(); j++) {
                                    VehicleDealerListPojo.OtherDatesListPojo otherdateobj = new VehicleDealerListPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_id"));
                                    otherDatesListPojos.add(otherdateobj);
                                }
                                dealerMainObj.setOther_date(otherDatesListPojos);


                                ArrayList<VehicleDealerListPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    VehicleDealerListPojo.DocumentListPojo documentObj = new VehicleDealerListPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDocument_id(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_id"));
                                    documentObj.setDocument_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setOriginal_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("original_name"));
                                    documentsList.add(documentObj);
                                }
                                dealerMainObj.setDocument(documentsList);
                                vehicleDealerListPojos.add(dealerMainObj);
                                linkPojos.add(linkPojo);
                            }
                            if (vehicleDealerListPojos.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                vehicle_dealer_list.setVisibility(View.GONE);
                            } else {
                                vehicle_dealer_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            vehicleDealerListToLinkPojos = vehicleDealerListPojos;
                            vehicle_dealer_list.setAdapter(new DealerLinkAdapter(context, vehicleDealerListPojos, linkPojos));

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

}
