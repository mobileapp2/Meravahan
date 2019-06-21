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
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.AddCustomer_Activity;
import in.rto.collections.adapters.CustomerAdapter;
import in.rto.collections.adapters.GetClientListAdapter;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class Self_Fragment extends Fragment {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView customer_list;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_customer;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static  String user_id;
    private SearchView searchView;
    private static  ArrayList<CustomerPojo> customerPojos;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_self, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        fab_add_customer = rootView.findViewById(R.id.fab_add_customer);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        customer_list = rootView.findViewById(R.id.customer);
        drawerlayout = getActivity().findViewById(R.id.drawerlayout);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        customer_list.setLayoutManager(layoutManager);
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            new Self_Fragment.GetCustomerList().execute(user_id);
        } else {
            Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            customer_list.setVisibility(View.GONE);
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
        fab_add_customer.setOnClickListener(new View.OnClickListener() {

          public void onClick(View view) {
               startActivity(new Intent(context, AddCustomer_Activity.class));
           }
      });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new Self_Fragment.GetCustomerList().execute(user_id);
                } else {
                    Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    customer_list.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ArrayList<CustomerPojo> customerSearchedList = new ArrayList<>();
                for (CustomerPojo customer :  customerPojos) {
                    String contactToBeSearched = customer.getVehicle_owner().toLowerCase()+ customer.getVehicle_no().toLowerCase();
                    if (contactToBeSearched.contains(query.toLowerCase())) {
                        customerSearchedList.add(customer);
                    }
                }

                if (customerSearchedList.size() == 0) {
                    customer_list.setAdapter(new CustomerAdapter(context, customerSearchedList,user_id));
                } else {
                    customer_list.setAdapter(new CustomerAdapter(context, customerSearchedList,user_id));
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<CustomerPojo> customerSearchedList = new ArrayList<>();
                    for (CustomerPojo customer : customerPojos) {
                        String contactToBeSearched = customer.getVehicle_owner().toLowerCase()+customer.getVehicle_no().toLowerCase();
                        if (contactToBeSearched.contains(newText.toLowerCase())) {
                            customerSearchedList.add(customer);
                        }
                    }
                    if (customerSearchedList.size() == 0) {
                        customer_list.setAdapter(new CustomerAdapter(context,customerSearchedList,user_id));
                        //   searchView.setQuery("", false);
                        //   bindRecyclerview(contactList);
                    } else {
                        //bindRecyclerview(contactsSearchedList);
                        customer_list.setAdapter(new CustomerAdapter(context,customerSearchedList,user_id));
                    }
                    return true;
                } else if (newText.equals("")) {
                    customer_list.setAdapter(new CustomerAdapter(context,customerPojos,user_id));
                    //bindRecyclerview(contactList);
                }
                return true;
            }
        });


    }

    public static class GetCustomerList extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            customer_list.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getcustomer"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.CUSTOMERAPI, param);
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
                customer_list.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    customerPojos = new ArrayList<CustomerPojo>();
                    customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                CustomerPojo customerMainObj = new CustomerPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                customerMainObj.setId(jsonObj.getString("id"));
                                customerMainObj.setChassis_no(jsonObj.getString("Chassis_No"));
                                customerMainObj.setDescription(jsonObj.getString("Description"));
                                customerMainObj.setEngine_no(jsonObj.getString("Engine_No"));
                                customerMainObj.setHypothecated_to(jsonObj.getString("Hypothecated_to"));
                                customerMainObj.setInsurance_policy_no(jsonObj.getString("Insurance_Policy_No"));
                                customerMainObj.setInsurance_renewal_date(jsonObj.getString("Insurenace_Renewal_Date"));
                                customerMainObj.setPurchase_date(jsonObj.getString("Date_of_Purchase"));
                                customerMainObj.setState_id(jsonObj.getString("State"));
                                customerMainObj.setStateName(jsonObj.getString("stateName"));
                                customerMainObj.setTem_reg_no(jsonObj.getString("Temporary_Registration_No"));
                                customerMainObj.setVehicle_owner(jsonObj.getString("Name_of_the_Vehicle_Owner"));
                                customerMainObj.setVehicle_no(jsonObj.getString("Vehicle_No"));
                                customerMainObj.setRto_agent_name(jsonObj.getString("RTO_Agent"));
                                customerMainObj.setVehicle_dealer_name(jsonObj.getString("Vehicle_Dealer"));
                                customerMainObj.setRemark(jsonObj.getString("Remark"));
                                //  customerMainObj.setClient_id(jsonObj.getString("client_name"));
                                // customerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                customerMainObj.setType_id(jsonObj.getString("Type"));
                                customerMainObj.setType_name(jsonObj.getString("typeName"));
                                customerMainObj.setFittness_valid_upto(jsonObj.getString("Fitness_Valid_Upto"));
                                customerMainObj.setPuc_renewal_date(jsonObj.getString("PUC_Renewal_Date"));
                                customerMainObj.setPermit_valid_upto(jsonObj.getString("Permit_Valid_Upto"));
                                customerMainObj.setNational_permit_valid_upto(jsonObj.getString("National_Permit_Valid_Upto"));
                                customerMainObj.setState_permit_valid_upto(jsonObj.getString("State_Permit_Valid_upto"));
                                customerMainObj.setTax_paid_up_to(jsonObj.getString("Tax_Paid_Upto"));
                                customerMainObj.setImportR(jsonObj.getString("import"));
                                customerMainObj.setBank_name(jsonObj.getString("bank_name"));
                                customerMainObj.setBranch_name(jsonObj.getString("branch_name"));
                                customerMainObj.setBorrower_name(jsonObj.getString("borrower_name"));
                                customerMainObj.setDate_to_section(jsonObj.getString("date_of_saction"));
                                customerMainObj.setLoan_amount(jsonObj.getString("loan_amount"));
                                customerMainObj.setLoan_account_number(jsonObj.getString("loan_account_number"));
                                customerMainObj.setInstallment_amount(jsonObj.getString("installment_amount"));
                                customerMainObj.setInstallment_start_date(jsonObj.getString("installment_start_date"));
                                customerMainObj.setInstallment_end_date(jsonObj.getString("installment_end_date"));
                                customerMainObj.setFrequency_id(jsonObj.getString("frqquency"));
                                customerMainObj.setFrequency(jsonObj.getString("frq"));
                                customerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                customerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));

                                ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("service_date").length(); j++) {
                                    CustomerPojo.ServiceDatesListPojo servicedateobj = new CustomerPojo.ServiceDatesListPojo();
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_date").getJSONObject(j).getString("service_date"));
                                    servicedateobj.setText(jsonObj.getJSONArray("service_date").getJSONObject(j).getString("text"));
                                    servicedateobj.setService_date_id(jsonObj.getJSONArray("service_date").getJSONObject(j).getString("service_id"));
                                    serviceDatesListPojos.add(servicedateobj);
                                }
                                customerMainObj.setService_date(serviceDatesListPojos);



                                ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("other_customer_dates").length(); j++) {
                                    CustomerPojo.OtherDatesListPojo otherdateobj = new CustomerPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("other_customer_dates").getJSONObject(j).getString("other_customer_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("other_customer_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("other_customer_dates").getJSONObject(j).getString("customer_id"));
                                    otherDatesListPojos.add(otherdateobj);
                                }
                                customerMainObj.setOther_date(otherDatesListPojos);



                                ArrayList<CustomerPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    CustomerPojo.DocumentListPojo documentObj = new CustomerPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDocument_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setOriginal_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("original_name"));
                                    documentsList.add(documentObj);
                                }
                                customerMainObj.setDocument(documentsList);
                                customerPojos.add(customerMainObj);
                            }
                            if (customerPojos.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                customer_list.setVisibility(View.GONE);
                            } else {
                                customer_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        customer_list.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                customer_list.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}
