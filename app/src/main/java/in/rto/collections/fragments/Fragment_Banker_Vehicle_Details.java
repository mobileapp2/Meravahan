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
import in.rto.collections.activities.Add_BankerDetails_Activity;
import in.rto.collections.adapters.GetBankerDetailsListAdapter;
import in.rto.collections.models.BankerDetailsPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class Fragment_Banker_Vehicle_Details extends Fragment {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView banker_list;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_banker;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private static String user_id;
    private SearchView searchView;
    private static String role;
    private static ArrayList<BankerDetailsPojo> bankerDetailsPojos;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__banker__vehicle__details, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        fab_add_banker = rootView.findViewById(R.id.fab_add_banker);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        banker_list = rootView.findViewById(R.id.banker_list);
        drawerlayout = getActivity().findViewById(R.id.drawerlayout);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        banker_list.setLayoutManager(layoutManager);
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            new Fragment_Banker_Vehicle_Details.GetBankerList().execute(user_id);
        } else {
            Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            banker_list.setVisibility(View.GONE);
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
        fab_add_banker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Add_BankerDetails_Activity.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new Fragment_Banker_Vehicle_Details.GetBankerList().execute(user_id);
                } else {
                    Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    banker_list.setVisibility(View.GONE);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ArrayList<BankerDetailsPojo> bankerSearchedList = new ArrayList<>();
                for (BankerDetailsPojo banker : bankerDetailsPojos) {
                    String contactToBeSearched = banker.getBorrower_name().toLowerCase() + banker.getVehicle_number().toLowerCase();
                    if (contactToBeSearched.contains(query.toLowerCase())) {
                        bankerSearchedList.add(banker);
                    }
                }

                if (bankerSearchedList.size() == 0) {
                    banker_list.setAdapter(new GetBankerDetailsListAdapter(context, bankerSearchedList, user_id));
                } else {
                    banker_list.setAdapter(new GetBankerDetailsListAdapter(context, bankerSearchedList, user_id));

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<BankerDetailsPojo> bankerearchedList = new ArrayList<>();
                    for (BankerDetailsPojo banker : bankerDetailsPojos) {
                        String contactToBeSearched = banker.getBorrower_name().toLowerCase() + banker.getVehicle_number().toLowerCase();
                        if (contactToBeSearched.contains(newText.toLowerCase())) {
                            bankerearchedList.add(banker);
                        }
                    }
                    if (bankerearchedList.size() == 0) {
                        banker_list.setAdapter(new GetBankerDetailsListAdapter(context, bankerearchedList, user_id));
                    } else {
                        //bindRecyclerview(contactsSearchedList);
                        banker_list.setAdapter(new GetBankerDetailsListAdapter(context, bankerearchedList, user_id));

                    }
                    return true;
                } else if (newText.equals("")) {
                    banker_list.setAdapter(new GetBankerDetailsListAdapter(context, bankerDetailsPojos, user_id));
                    //customer_list.setAdapter(new CustomerAdapter(context,customerPojos,user_id));
                    //bindRecyclerview(contactList);
                }
                return true;
            }
        });

    }

    public static class GetBankerList extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            banker_list.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getBanker"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.BANKERAPI, param);
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
                banker_list.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    bankerDetailsPojos = new ArrayList<BankerDetailsPojo>();
                    banker_list.setAdapter(new GetBankerDetailsListAdapter(context, bankerDetailsPojos, user_id));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                BankerDetailsPojo bankerMainObj = new BankerDetailsPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                bankerMainObj.setId(jsonObj.getString("id"));
                                bankerMainObj.setRemark(jsonObj.getString("remark"));
                                bankerMainObj.setClient_id(jsonObj.getString("client_id"));
                                bankerMainObj.setDescription(jsonObj.getString("description"));
                                bankerMainObj.setClient_name(jsonObj.getString("clientName"));
                                bankerMainObj.setBank_name(jsonObj.getString("bank_name"));
                                bankerMainObj.setVehicle_dealer_id(jsonObj.getString("dealer_id"));
                                bankerMainObj.setVehicle_dealer_name(jsonObj.getString("vehicleDealerName"));
                                bankerMainObj.setBorrower_name(jsonObj.getString("borrower"));
                                bankerMainObj.setBranch_name(jsonObj.getString("branch_name"));
                                bankerMainObj.setDate_of_purchase(jsonObj.getString("date_of_purchase"));
                                bankerMainObj.setDate_to_section(jsonObj.getString("date_to_section"));
                                bankerMainObj.setFrequency(jsonObj.getString("frq"));
                                bankerMainObj.setFrequency_id(jsonObj.getString("frequency"));
                                bankerMainObj.setLoan_amount(jsonObj.getString("loan_amount"));
                                bankerMainObj.setLoan_account_number(jsonObj.getString("loan_account_number"));
                                bankerMainObj.setInstallment_amount(jsonObj.getString("installment_amount"));
                                bankerMainObj.setInstallment_start_date(jsonObj.getString("installment_start_date"));
                                bankerMainObj.setInstallment_end_date(jsonObj.getString("installment_end_date"));
                                bankerMainObj.setVehicle_number(jsonObj.getString("vehicle_number"));
                                bankerMainObj.setIsshowto_customer(jsonObj.getString("is_show_to_customer"));
                                bankerMainObj.setIsshowto_dealer(jsonObj.getString("is_show_to_dealer"));
                                bankerMainObj.setVehicle_image(jsonObj.getString("vehicle_image"));
                                bankerMainObj.setVehicle_image_url(jsonObj.getString("vehicleImageURL"));
                                ArrayList<BankerDetailsPojo.DocumentListPojo> documentsList = new ArrayList<>();

                                for (int j = 0; j < jsonObj.getJSONArray("document").length(); j++) {
                                    BankerDetailsPojo.DocumentListPojo documentObj = new BankerDetailsPojo.DocumentListPojo();
                                    documentObj.setDocument(jsonObj.getJSONArray("document").getJSONObject(j).getString("document"));
                                    documentObj.setName(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_name"));
                                    documentObj.setDocument_id(jsonObj.getJSONArray("document").getJSONObject(j).getString("document_id"));
                                    documentObj.setDoc_name(jsonObj.getJSONArray("document").getJSONObject(j).getString("doc_name"));
                                    documentsList.add(documentObj);
                                }
                                bankerMainObj.setDocument(documentsList);
                                bankerDetailsPojos.add(bankerMainObj);
                            }
                            if (bankerDetailsPojos.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                banker_list.setVisibility(View.GONE);
                            } else {
                                banker_list.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            banker_list.setAdapter(new GetBankerDetailsListAdapter(context, bankerDetailsPojos, user_id));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        banker_list.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                banker_list.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}
