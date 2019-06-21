package in.rto.collections.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import in.rto.collections.adapters.CustomerAdapter;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class Vehicle_Customer_Fragment extends Fragment {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView customer_list;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static  String user_id,role;
    private SearchView searchView;
    private static ArrayList<CustomerPojo> customerPojos2,customerPojos,customerPojos1,customerPojos3;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vehicle__customer, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
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
            new Vehicle_Customer_Fragment.GetCustomerList().execute(user_id);
            new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role);
            new Vehicle_Customer_Fragment.GetBankerList().execute(user_id,role);
            new Vehicle_Customer_Fragment.GetOtherVehicleList().execute(user_id);

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
                    new Vehicle_Customer_Fragment.GetCustomerList().execute(user_id);
                    new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role);
                    new Vehicle_Customer_Fragment.GetBankerList().execute(user_id,role);
                    new Vehicle_Customer_Fragment.GetOtherVehicleList().execute(user_id);
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
       // private ArrayList<CustomerPojo> customerPojos;

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
            param.add(new ParamsPojo("type", "getcustomervehicle"));
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
                                customerMainObj.setChassis_no(jsonObj.getString("chassis_no"));
                                customerMainObj.setDescription(jsonObj.getString("description"));
                                customerMainObj.setEngine_no(jsonObj.getString("engine_no"));
                                customerMainObj.setHypothecated_to(jsonObj.getString("hypothecated_to"));
                                customerMainObj.setInsurance_policy_no(jsonObj.getString("insurance_policy_no"));
                                customerMainObj.setInsurance_renewal_date(jsonObj.getString("insurance_renewal_date"));
                                customerMainObj.setPurchase_date(jsonObj.getString("purchase_date"));
                                customerMainObj.setState_id(jsonObj.getString("state"));
                                customerMainObj.setStateName(jsonObj.getString("StateName"));
                                customerMainObj.setTem_reg_no(jsonObj.getString("tem_reg_no"));
                                customerMainObj.setVehicle_owner(jsonObj.getString("vehicle_owner"));
                                customerMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                customerMainObj.setRto_agent_name(jsonObj.getString("rtoAgentName"));
                                customerMainObj.setRemark(jsonObj.getString("remark"));
                                customerMainObj.setClient_id(jsonObj.getString("client_name"));
                                customerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                customerMainObj.setType_id(jsonObj.getString("type"));
                                customerMainObj.setType_name(jsonObj.getString("vType"));
                                customerMainObj.setIsimport(jsonObj.getString("is_import_by_customer"));
                                customerMainObj.setImportR(jsonObj.getString("import"));
                                customerMainObj.setCreated_by(jsonObj.getString("created_by"));
                                customerMainObj.setCreaterName(jsonObj.getString("createrName"));
                                customerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                customerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));

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

                                ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("service_dates").length(); j++) {
                                    CustomerPojo.ServiceDatesListPojo servicedateobj = new CustomerPojo.ServiceDatesListPojo();
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_date"));
                                    servicedateobj.setService_date_id(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_id"));
                                    servicedateobj.setText(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("text"));
                                    serviceDatesListPojos.add(servicedateobj);
                                }
                                customerMainObj.setService_date(serviceDatesListPojos);



                                ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("other_dates").length(); j++) {
                                    CustomerPojo.OtherDatesListPojo otherdateobj = new CustomerPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_id"));
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
                          //  new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role);
                            customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));

                        // new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role);
                       // ll_nothingtoshow.setVisibility(View.VISIBLE);
                       // customer_list.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
//                ll_nothingtoshow.setVisibility(View.VISIBLE);
//                customer_list.setVisibility(View.GONE);
//                e.printStackTrace();
            }
        }
     }
    public static class GetCustomerList1 extends AsyncTask<String, Void, String> {
      //  private ArrayList<CustomerPojo> customerPojos1;

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
            param.add(new ParamsPojo("type", "getcustomerrto"));
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
                    customerPojos1 = new ArrayList<CustomerPojo>();
                    customer_list.setAdapter(new CustomerAdapter(context, customerPojos1,user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                CustomerPojo customerMainObj = new CustomerPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                customerMainObj.setId(jsonObj.getString("id"));
                                customerMainObj.setTax_paid_up_to(jsonObj.getString("tax_paid_upto"));
                                customerMainObj.setRemark(jsonObj.getString("remark"));
                                customerMainObj.setClient_id(jsonObj.getString("client_name"));
                                customerMainObj.setDescription(jsonObj.getString("description"));
                                customerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                customerMainObj.setType_name(jsonObj.getString("vType"));
                                customerMainObj.setType_id(jsonObj.getString("rtotype"));
                                customerMainObj.setVehicle_dealer_name(jsonObj.getString("vehicleDealerName"));
                                customerMainObj.setNational_permit_valid_upto(jsonObj.getString("national_permit_upto"));
                                customerMainObj.setState_permit_valid_upto(jsonObj.getString("state_permit_valid_upto"));
                                customerMainObj.setChassis_no(jsonObj.getString("chassis_no"));
                                customerMainObj.setEngine_no(jsonObj.getString("engine_no"));
                                customerMainObj.setPermit_valid_upto(jsonObj.getString("permit_valid_upto"));
                                customerMainObj.setInsurance_policy_no(jsonObj.getString("insurance_policy_no"));
                                customerMainObj.setInsurance_renewal_date(jsonObj.getString("insurance_renewal_date"));
                                customerMainObj.setPuc_renewal_date(jsonObj.getString("puc_renewal_date"));
                                customerMainObj.setState_id(jsonObj.getString("state"));
                                customerMainObj.setStateName(jsonObj.getString("sateName"));
                                customerMainObj.setFittness_valid_upto(jsonObj.getString("fitness_valid_upto"));
                                customerMainObj.setVehicle_owner(jsonObj.getString("vehicle_owner_name"));
                                customerMainObj.setVehicle_no(jsonObj.getString("vechicle_no"));
                                customerMainObj.setIsimport(jsonObj.getString("is_import_by_customer"));
                                customerMainObj.setImportR(jsonObj.getString("import"));
                                customerMainObj.setCreated_by(jsonObj.getString("created_by"));
                                customerMainObj.setCreaterName(jsonObj.getString("createrName"));
                                customerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                customerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));



                                ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("service_dates").length(); j++) {
                                    CustomerPojo.ServiceDatesListPojo servicedateobj = new CustomerPojo.ServiceDatesListPojo();
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_date").getJSONObject(j).getString("service_date"));
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_date").getJSONObject(j).getString("text"));
                                    servicedateobj.setService_date_id(jsonObj.getJSONArray("service_date").getJSONObject(j).getString("service_id"));
                                    serviceDatesListPojos.add(servicedateobj);
                                }
                                customerMainObj.setService_date(serviceDatesListPojos);



                                ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("rto_dates").length(); j++) {
                                    CustomerPojo.OtherDatesListPojo otherdateobj = new CustomerPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("rto_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("rto_dates").getJSONObject(j).getString("rto_id"));
                                    otherDatesListPojos.add(otherdateobj);
                                }
                                customerMainObj.setOther_date(otherDatesListPojos);



                                ArrayList<CustomerPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    CustomerPojo.DocumentListPojo documentObj = new CustomerPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDocument_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setOriginal_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("original_name"));          documentsList.add(documentObj);
                                }
                                customerMainObj.setDocument(documentsList);
                                customerPojos1.add(customerMainObj);
                            }
                            if (customerPojos1.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                customer_list.setVisibility(View.GONE);
                            } else {
                                customer_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            customerPojos.addAll(customerPojos1);
                            //customerPojos2.addAll(customerPojos1);
                           // customerPojos2.addAll(listTwo);

                            customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        if(customerPojos.size() == 0) {
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            customer_list.setVisibility(View.GONE);
                        }else{
                            customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
                        }
                    }
                }
            } catch (Exception e) {
                customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
//                ll_nothingtoshow.setVisibility(View.VISIBLE);
//                customer_list.setVisibility(View.GONE);
//                e.printStackTrace();
            }
        }
    }

    public static class GetBankerList extends AsyncTask<String, Void, String> {
        //  private ArrayList<CustomerPojo> customerPojos1;

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
            param.add(new ParamsPojo("type", "getcustomerBankerDetails"));
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
                    customerPojos2 = new ArrayList<CustomerPojo>();
                    customer_list.setAdapter(new CustomerAdapter(context, customerPojos2,user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                CustomerPojo customerMainObj = new CustomerPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                customerMainObj.setId(jsonObj.getString("id"));
                                customerMainObj.setBank_name(jsonObj.getString("bank_name"));
                                customerMainObj.setBranch_name(jsonObj.getString("branch_name"));
                                customerMainObj.setBorrower_name(jsonObj.getString("borrower"));
                                customerMainObj.setVehicle_owner(jsonObj.getString("borrower"));
                                customerMainObj.setDate_to_section(jsonObj.getString("date_to_section"));
                                customerMainObj.setLoan_amount(jsonObj.getString("loan_amount"));
                                customerMainObj.setLoan_account_number(jsonObj.getString("loan_account_number"));
                                customerMainObj.setInstallment_amount(jsonObj.getString("installment_amount"));
                                customerMainObj.setInstallment_start_date(jsonObj.getString("installment_start_date"));
                                customerMainObj.setInstallment_end_date(jsonObj.getString("installment_end_date"));
                                customerMainObj.setFrequency_id(jsonObj.getString("frequency"));
                                customerMainObj.setFrequency(jsonObj.getString("frq"));
                                customerMainObj.setRemark(jsonObj.getString("remark"));
                                customerMainObj.setVehicle_no(jsonObj.getString("vehicle_number"));
                                customerMainObj.setClient_id(jsonObj.getString("client_id"));
                                customerMainObj.setDescription(jsonObj.getString("description"));
                                customerMainObj.setClient_name(jsonObj.getString("clientName"));
                                customerMainObj.setVehicle_dealer_name(jsonObj.getString("vehicleDealerName"));
                                customerMainObj.setPurchase_date(jsonObj.getString("date_of_purchase"));
                                customerMainObj.setImportR(jsonObj.getString("import"));
                                customerMainObj.setCreated_by(jsonObj.getString("created_by"));
                                customerMainObj.setCreaterName(jsonObj.getString("createrName"));
                                customerMainObj.setIsimport(jsonObj.getString("is_import_by_customer"));
                                customerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                customerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));



                                ArrayList<CustomerPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    CustomerPojo.DocumentListPojo documentObj = new CustomerPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setDocument_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setOriginal_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("doc_name"));
                                    documentObj.setDocument_id(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_id"));
                                    documentsList.add(documentObj);
                                }
                                customerMainObj.setDocument(documentsList);
                                customerPojos2.add(customerMainObj);
                            }

                            if (customerPojos2.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                customer_list.setVisibility(View.GONE);
                            } else {
                                customer_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            customerPojos.addAll(customerPojos2);

                            customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        if(customerPojos.size() == 0) {
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            customer_list.setVisibility(View.GONE);
                        }else{
                            customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
                        }
                    }
                }
            } catch (Exception e) {
                customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
//                ll_nothingtoshow.setVisibility(View.VISIBLE);
//                customer_list.setVisibility(View.GONE);
//                e.printStackTrace();
            }
        }
    }
    public static class GetOtherVehicleList extends AsyncTask<String, Void, String> {
        // private ArrayList<CustomerPojo> customerPojos;

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
            param.add(new ParamsPojo("type", "getcustomerothervehicle"));
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
                    customerPojos3 = new ArrayList<CustomerPojo>();
                    customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                CustomerPojo customerMainObj = new CustomerPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                customerMainObj.setId(jsonObj.getString("id"));
                                customerMainObj.setDescription(jsonObj.getString("description"));
                                customerMainObj.setState_id(jsonObj.getString("state"));
                                customerMainObj.setStateName(jsonObj.getString("StateName"));
                                customerMainObj.setVehicle_owner(jsonObj.getString("vehicle_owner"));
                                customerMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                customerMainObj.setRemark(jsonObj.getString("remark"));
                                customerMainObj.setClient_id(jsonObj.getString("client_name"));
                                customerMainObj.setClient_name(jsonObj.getString("ClientName"));
                                customerMainObj.setType_id(jsonObj.getString("type"));
                                customerMainObj.setType_name(jsonObj.getString("vType"));
                                customerMainObj.setImportR(jsonObj.getString("import"));
                                customerMainObj.setCreated_by(jsonObj.getString("created_by"));
                                customerMainObj.setCreaterName(jsonObj.getString("createrName"));
                                customerMainObj.setIsimport(jsonObj.getString("is_import_by_customer"));


                                ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("service_dates").length(); j++) {
                                    CustomerPojo.ServiceDatesListPojo servicedateobj = new CustomerPojo.ServiceDatesListPojo();
                                    servicedateobj.setService_date(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_date"));
                                    servicedateobj.setService_date_id(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("service_id"));
                                    servicedateobj.setText(jsonObj.getJSONArray("service_dates").getJSONObject(j).getString("text"));
                                    serviceDatesListPojos.add(servicedateobj);
                                }
                                customerMainObj.setService_date(serviceDatesListPojos);

                                ArrayList<CustomerPojo.WheelDatesListPojo> wheelDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("wheel_dates").length(); j++) {
                                    CustomerPojo.WheelDatesListPojo wheeldateobj = new CustomerPojo.WheelDatesListPojo();
                                    wheeldateobj.setAlignment_date(jsonObj.getJSONArray("wheel_dates").getJSONObject(j).getString("alignment_date"));
                                    wheeldateobj.setAlignment_date_id(jsonObj.getJSONArray("wheel_dates").getJSONObject(j).getString("wheel_id"));
                                    wheeldateobj.setText(jsonObj.getJSONArray("wheel_dates").getJSONObject(j).getString("text"));
                                    wheelDatesListPojos.add(wheeldateobj);
                                }
                                customerMainObj.setWheel_date(wheelDatesListPojos);



                                ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("other_dates").length(); j++) {
                                    CustomerPojo.OtherDatesListPojo otherdateobj = new CustomerPojo.OtherDatesListPojo();
                                    otherdateobj.setOther_date(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_dates"));
                                    otherdateobj.setText(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("text"));
                                    otherdateobj.setOther_date_id(jsonObj.getJSONArray("other_dates").getJSONObject(j).getString("other_id"));
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
                                customerPojos3.add(customerMainObj);
                            }
                            if (customerPojos3.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                customer_list.setVisibility(View.GONE);
                            } else {
                                customer_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            customerPojos.addAll(customerPojos3);
                            //  new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role);
                            customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));

                        // new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role);
                        // ll_nothingtoshow.setVisibility(View.VISIBLE);
                        // customer_list.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                customer_list.setAdapter(new CustomerAdapter(context, customerPojos,user_id));
//                ll_nothingtoshow.setVisibility(View.VISIBLE);
//                customer_list.setVisibility(View.GONE);
//                e.printStackTrace();
            }
        }
    }

}