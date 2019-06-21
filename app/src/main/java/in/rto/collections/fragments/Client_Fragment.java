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
import in.rto.collections.activities.Add_Client_Activity;
import in.rto.collections.adapters.GetClientListAdapter;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;


public class Client_Fragment extends Fragment {
    public static DrawerLayout drawerlayout;
    private static Context context;
    private static RecyclerView rv_clientlist;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private FloatingActionButton fab_add_client;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private String user_id;
    private SearchView searchView;
    private static ArrayList<ClientMainListPojo> clientList;
    private GetClientListAdapter clientListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_client, container, false);

        context = getActivity();

        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    public void onResume() {

        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setDefault();
        super.onResume();
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        fab_add_client = rootView.findViewById(R.id.fab_add_client);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_clientlist = rootView.findViewById(R.id.rv_clientlist);
        drawerlayout = getActivity().findViewById(R.id.drawerlayout);
        searchView = rootView.findViewById(R.id.searchView);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        layoutManager = new LinearLayoutManager(context);
        rv_clientlist.setLayoutManager(layoutManager);
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(getActivity())) {
            new GetClientList().execute(user_id);
        } else {
            Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_clientlist.setVisibility(View.GONE);
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
        fab_add_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Add_Client_Activity.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetClientList().execute(user_id);
                } else {
                    Utilities.showSnackBar(drawerlayout, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_clientlist.setVisibility(View.GONE);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                ArrayList<ClientMainListPojo> contactsSearchedList = new ArrayList<>();
                for (ClientMainListPojo client : clientList) {
                    String contactToBeSearched = client.getName().toLowerCase();
                    if (contactToBeSearched.contains(query.toLowerCase())) {
                        contactsSearchedList.add(client);

                    }
                }

                if (contactsSearchedList.size() == 0) {
                    //Utilities.showAlertDialog(context, "Fail", "No Such Client Found", false);
                    rv_clientlist.setAdapter(new GetClientListAdapter(context, contactsSearchedList));
                    //  searchView.setQuery("", false);
                    // bindRecyclerview(contactList);
                } else {
                    rv_clientlist.setAdapter(new GetClientListAdapter(context, contactsSearchedList));
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<ClientMainListPojo> contactsSearchedList = new ArrayList<>();
                    for (ClientMainListPojo contacts : clientList) {
                        String contactToBeSearched = contacts.getName().toLowerCase();
                        if (contactToBeSearched.contains(newText.toLowerCase())) {
                            contactsSearchedList.add(contacts);
                        }
                    }

                    if (contactsSearchedList.size() == 0) {
                        rv_clientlist.setAdapter(new GetClientListAdapter(context, contactsSearchedList));
                        // Utilities.showMessageString(context, "No Such Client Found");

                    } else {
                        //bindRecyclerview(contactsSearchedList);
                        rv_clientlist.setAdapter(new GetClientListAdapter(context, contactsSearchedList));
                    }
                    return true;
                } else if (newText.equals("")) {
                    rv_clientlist.setAdapter(new GetClientListAdapter(context, clientList));
                    //bindRecyclerview(contactList);
                }
                return true;
            }
        });

    }

    public static class GetClientList extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_clientlist.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAllClients"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.CLIENTAPI, param);
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
                rv_clientlist.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    clientList = new ArrayList<ClientMainListPojo>();
                    rv_clientlist.setAdapter(new GetClientListAdapter(context, clientList));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                ClientMainListPojo clientMainObj = new ClientMainListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                clientMainObj.setId(jsonObj.getString("id"));
                                clientMainObj.setName(jsonObj.getString("name"));
                                clientMainObj.setAlias(jsonObj.getString("alias"));
                                clientMainObj.setMobile(jsonObj.getString("mobile"));
                                clientMainObj.setWhats_app_no(jsonObj.getString("whats_app_no"));
                                clientMainObj.setEmail(jsonObj.getString("email"));
                                clientMainObj.setDob(jsonObj.getString("dob"));
                                clientMainObj.setAnniversary_date(jsonObj.getString("anniversary_date"));
                                clientMainObj.setClient_code(jsonObj.getString("client_code"));
                                clientMainObj.setClient_code_id(jsonObj.getString("client_code_id"));
                                clientMainObj.setRemark(jsonObj.getString("remark"));
                                clientMainObj.setLanguage_id(jsonObj.getString("language_id"));
                                clientMainObj.setLanguage(jsonObj.getString("language"));

                                clientList.add(clientMainObj);
                            }
                            if (clientList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_clientlist.setVisibility(View.GONE);
                            } else {
                                rv_clientlist.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            rv_clientlist.setAdapter(new GetClientListAdapter(context, clientList));

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_clientlist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_clientlist.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

}
