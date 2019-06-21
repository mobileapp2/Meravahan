package in.rto.collections.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import in.rto.collections.adapters.GetRTOAgentDetailListAdapter;
import in.rto.collections.adapters.RTOLinkAdapter;
import in.rto.collections.fragments.Fragment_RTO_Agent;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.models.VehicleDealerListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class LinkToRTO_Activity extends Activity {
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
    private static ArrayList<LinkPojo> linkPojos;
    private static ArrayList<RTOAgentListPojo> rtoAgentListToLinkPojos;
    public static int selectedposition = 0;
    private static LinkPojo linkPojo;
    private ImageView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_to_rto);
        context = LinkToRTO_Activity.this;
        init();
        getSessionData();
        setDefault();
        setUpToolbar();
        setEventHandlers();
    }
    private void init() {
        session = new UserSessionManager(context);
        fab_add_agent = findViewById(R.id.fab_add_agent);
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
        rto_agent_list = findViewById(R.id.rto_agent_list);
        drawerlayout = findViewById(R.id.drawerlayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchView = findViewById(R.id.searchView);
        next = findViewById(R.id.img_save);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        rto_agent_list.setLayoutManager(layoutManager);
        linkPojo  = (LinkPojo) getIntent().getSerializableExtra("vehicleDetails");
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new LinkToRTO_Activity.GetRTOAgentList().execute(user_id);
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
    public  void  setUpToolbar() {
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
    private void setEventHandlers() {
        fab_add_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddRTOAgent_Activity.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new LinkToRTO_Activity.GetRTOAgentList().execute(user_id);
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
                    Utilities.showAlertDialog(context, "Fail", "No Such Vehicle Details Found", false);
                    //  searchView.setQuery("", false);
                    // bindRecyclerview(contactList);
                } else {
                    rtoAgentListToLinkPojos = rtoSearchedList;
                    rto_agent_list.setAdapter(new RTOLinkAdapter(context, rtoSearchedList,linkPojos));

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
                        Utilities.showMessageString(context, "No Such Vehicle Details Found");
                        //   searchView.setQuery("", false);
                        //   bindRecyclerview(contactList);
                    } else {
                        //bindRecyclerview(contactsSearchedList);
                        rtoAgentListToLinkPojos = rtoSearchedList;
                        rto_agent_list.setAdapter(new RTOLinkAdapter(context, rtoSearchedList,linkPojos));

                    }
                    return true;
                } else if (newText.equals("")) {
                    rto_agent_list.setAdapter(new RTOLinkAdapter(context, rtoAgentListPojos,linkPojos));
                    //customer_list.setAdapter(new CustomerAdapter(context,customerPojos,user_id));
                    //bindRecyclerview(contactList);
                }
                return true;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedposition != -1) {
                    RTOAgentListPojo rtoAgentListPojo = rtoAgentListToLinkPojos.get(selectedposition);
                    Intent intent = new Intent(context, Link_Edit_RTO.class);
                    intent.putExtra("rtoagentDetails", rtoAgentListPojo);
                    intent.putExtra("vehicleDetails", linkPojo);
                    context.startActivity(intent);

                }else{
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

    public  static class GetRTOAgentList extends AsyncTask<String, Void, String> {


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
            param.add(new ParamsPojo("type", "getRTO"));
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
                    linkPojos = new ArrayList<LinkPojo>();
                    rto_agent_list.setAdapter(new RTOLinkAdapter(context, rtoAgentListPojos,linkPojos));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                RTOAgentListPojo agentdealerMainObj = new RTOAgentListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                agentdealerMainObj.setId(jsonObj.getString("id"));
                                agentdealerMainObj.setRemark(jsonObj.getString("remark"));
                                agentdealerMainObj.setClient_id(jsonObj.getString("client_name"));
                                agentdealerMainObj.setDescription(jsonObj.getString("description"));
                                agentdealerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                agentdealerMainObj.setType_name(jsonObj.getString("vType"));
                                agentdealerMainObj.setType_id(jsonObj.getString("rtotype"));
                                agentdealerMainObj.setVehicle_dealer_id(jsonObj.getString("vehicle_dealer"));
                                agentdealerMainObj.setVehicle_dealer_name(jsonObj.getString("vehicleDealerName"));
                                agentdealerMainObj.setChassis_no(jsonObj.getString("chassis_no"));
                                agentdealerMainObj.setEngine_no(jsonObj.getString("engine_no"));
                                agentdealerMainObj.setInsurance_policy_no(jsonObj.getString("insurance_policy_no"));
                                agentdealerMainObj.setState_id(jsonObj.getString("state"));
                                agentdealerMainObj.setStateName(jsonObj.getString("sateName"));
                                agentdealerMainObj.setInsurance_renewal_date(jsonObj.getString("insurance_renewal_date"));
                                agentdealerMainObj.setPuc_renewal_date(jsonObj.getString("puc_renewal_date"));
                                agentdealerMainObj.setPermit_valid_upto(jsonObj.getString("permit_valid_upto"));
                                agentdealerMainObj.setTax_paid_up_to(jsonObj.getString("tax_paid_upto"));
                                agentdealerMainObj.setNational_permit_valid_upto(jsonObj.getString("national_permit_upto"));
                                agentdealerMainObj.setState_permit_valid_upto(jsonObj.getString("state_permit_valid_upto"));
                                agentdealerMainObj.setFittness_valid_upto(jsonObj.getString("fitness_valid_upto"));
                                agentdealerMainObj.setVehicle_owner(jsonObj.getString("vehicle_owner_name"));
                                agentdealerMainObj.setVehicle_no(jsonObj.getString("vechicle_no"));
                                agentdealerMainObj.setImportR(jsonObj.getString("import"));
                                agentdealerMainObj.setIsshowto_customer(jsonObj.getString("is_show_to_customer"));
                                agentdealerMainObj.setIsshowto_dealer(jsonObj.getString("is_show_to_dealer"));



                                ArrayList<RTOAgentListPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("rto_dates").length(); j++) {
                                    RTOAgentListPojo.OtherDatesListPojo otherdateobj = new RTOAgentListPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("rto_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("rto_id"));
                                    otherDatesListPojos.add(otherdateobj);
                                }
                                agentdealerMainObj.setOther_date(otherDatesListPojos);



                                ArrayList<RTOAgentListPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    RTOAgentListPojo.DocumentListPojo documentObj = new RTOAgentListPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setName(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setDocument_id(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_id"));
                                    documentObj.setDoc_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("doc_name"));
                                    documentsList.add(documentObj);
                                }
                                agentdealerMainObj.setDocument(documentsList);
                                rtoAgentListPojos.add(agentdealerMainObj);
                                linkPojos.add(linkPojo);
                            }
                            if (rtoAgentListPojos.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rto_agent_list.setVisibility(View.GONE);
                            } else {
                                rto_agent_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rtoAgentListToLinkPojos = rtoAgentListPojos;
                            rto_agent_list.setAdapter(new RTOLinkAdapter(context, rtoAgentListPojos,linkPojos));

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
