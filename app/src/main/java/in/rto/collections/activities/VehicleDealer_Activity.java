package in.rto.collections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.adapters.getVehicleDealerListAdapter;
import in.rto.collections.models.VehicleDealerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class VehicleDealer_Activity extends Activity {
    private static Context context;
    private static RecyclerView vehicledealerdetails;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static LinearLayout ll_nothingtoshow;
    private LinearLayout ll_parent;
    private LinearLayoutManager layoutManager;
    //    private ShimmerFrameLayout shimmer_view_container;
    private FloatingActionButton fab_add_vehicledealer;
    private UserSessionManager session;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_dealer_);
        init();
        getSessionData();
        setDefault();
        setEventHandlers();
        setUpToolbar();
    }

    private void init() {
        context = VehicleDealer_Activity.this;
        vehicledealerdetails = findViewById(R.id.vehicledealerdetails);
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        ll_parent = findViewById(R.id.ll_parent);
        fab_add_vehicledealer = findViewById(R.id.fab_add_vehicledealer);
        session = new UserSessionManager(context);
        layoutManager = new LinearLayoutManager(context);
        vehicledealerdetails.setLayoutManager(layoutManager);
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

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new VehicleDealer_Activity.GetVehicleDealerList().execute(user_id);
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            // ll_nothingtoshow.setVisibility(View.VISIBLE);
            vehicledealerdetails.setVisibility(View.GONE);
        }
    }

    private void setEventHandlers() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetVehicleDealerList().execute(user_id);
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    vehicledealerdetails.setVisibility(View.GONE);
                }
            }
        });

        fab_add_vehicledealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edt_name = new EditText(context);
                edt_name.setHint("Name*");
                final EditText edt_alias = new EditText(context);
                edt_alias.setHint("Email");
                final EditText edt_mobile = new EditText(context);
                edt_mobile.setHint("Mobile*");
                edt_mobile.setInputType(InputType.TYPE_CLASS_NUMBER);
                edt_mobile.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(10);
                edt_mobile.setFilters(filterArray);
                final EditText edt_landlineno = new EditText(context);
                edt_landlineno.setHint("Landline No");
                edt_landlineno.setInputType(InputType.TYPE_CLASS_NUMBER);

                float dpi = context.getResources().getDisplayMetrics().density;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setTitle("Add Vehicle Dealer");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isInternetAvailable(context)) {
                            if (edt_name.getText().toString().trim().equals("")) {
                                Utilities.showSnackBar(ll_parent, "Please Enter Name");
                                return;
                            }

                            if (edt_mobile.getText().toString().trim().equals("")) {
                                Utilities.showSnackBar(ll_parent, "Please Enter Mobile No.");
                                return;
                            }
                            new VehicleDealer_Activity.AddVehicleDealer().execute(edt_name.getText().toString().trim(), edt_alias.getText().toString().trim(), user_id, edt_mobile.getText().toString().trim(), edt_landlineno.getText().toString().trim());
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                final AlertDialog alertD = builder.create();
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(edt_name);
                ll.addView(edt_alias);
                ll.addView(edt_mobile);
                ll.addView(edt_landlineno);
                //alertD.setView(ll);
                alertD.setView(ll, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
                alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                edt_name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                        }

                    }


                });

            }
        });


    }

    private class AddVehicleDealer extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addVehicleDealer");
            obj.addProperty("name", params[0]);
            obj.addProperty("alias", params[1]);
            obj.addProperty("user_id", params[2]);
            obj.addProperty("mobile", params[3]);
            obj.addProperty("landline_no", params[4]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.MASTERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {

                        if (Utilities.isNetworkAvailable(context)) {
                            new VehicleDealer_Activity.GetVehicleDealerList().execute(user_id);
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            swipeRefreshLayout.setRefreshing(false);
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            vehicledealerdetails.setVisibility(View.GONE);
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class GetVehicleDealerList extends AsyncTask<String, Void, String> {
        private ArrayList<VehicleDealerPojo> vehicledealerlist;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            vehicledealerdetails.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getVehicleDealer"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
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
                vehicledealerdetails.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    vehicledealerlist = new ArrayList<>();
                    vehicledealerdetails.setAdapter(new getVehicleDealerListAdapter(context, vehicledealerlist));
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                VehicleDealerPojo summary = new VehicleDealerPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("name").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setName(jsonObj.getString("name"));
                                    summary.setAlias(jsonObj.getString("alias"));
                                    summary.setMobile(jsonObj.getString("mobile"));
                                    summary.setLandlineno(jsonObj.getString("landline_no"));
                                    vehicledealerlist.add(summary);
                                }
                            }
                            if (vehicledealerlist.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                vehicledealerdetails.setVisibility(View.GONE);
                            } else {
                                vehicledealerdetails.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                            }
                            vehicledealerdetails.setAdapter(new getVehicleDealerListAdapter(context, vehicledealerlist));
                        }
                    } else {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        vehicledealerdetails.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Vehicle Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
