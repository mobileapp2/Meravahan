package in.rto.collections.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.EventListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class PremiumDue_Fragment extends Fragment {
    public LinearLayout ll_parent;
    private Context context;
    private FloatingActionButton fab_wish_whatsapp, fab_wish_sms,fab_wish_notification;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_nothingtoshow;
    private LinearLayoutManager layoutManager,layoutManager1;
    private UserSessionManager session;
    private int mYear, mMonth, mDay;
    private String user_id, date,role,test_id,name;
    private ArrayList<EventListPojo> premiumDueList,premiumDueListRTO;
    private String id = "", message = "", whatsappPicUrl = "", whatsappPic = "",clientMobile="";
    private EditText edt_date, dialog_edt_whatsappmessage;
    private CheckBox cb_checkall;
    private ImageView dialog_imv_whatsapppic;
    ExpandableListView rv_premiumdue;
    ExpandableListViewRTOAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_premium_due, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    @Override
    public void onResume() {
        setDefault();
        super.onResume();
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        fab_wish_whatsapp = rootView.findViewById(R.id.fab_wish_whatsapp);
        fab_wish_sms = rootView.findViewById(R.id.fab_wish_sms);
        fab_wish_notification = rootView.findViewById(R.id.fab_wish_notification);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
       rv_premiumdue = rootView.findViewById(R.id.rv_premiumdue);
       // rv_premiumdue1 = rootView.findViewById(R.id.rv_premiumdue1);
       // rv_premiumdue2 = rootView.findViewById(R.id.rv_premiumdue2);
        ll_parent = getActivity().findViewById(R.id.ll_parent);
//      shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        cb_checkall = rootView.findViewById(R.id.cb_checkall);
        edt_date = rootView.findViewById(R.id.edt_date);
      //  layoutManager = new LinearLayoutManager(context);
       // layoutManager1 = new LinearLayoutManager(context);
       // rv_premiumdue.setLayoutManager(layoutManager);
       // rv_premiumdue1.setLayoutManager(layoutManager1);
       // rv_premiumdue2.setLayoutManager(layoutManager);

        premiumDueList = new ArrayList<>();
        premiumDueListRTO = new ArrayList<>();
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
            test_id = json.getString("id");
            role = json.getString("role_id");
            name = json.getString("name");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        cb_checkall.setChecked(false);
        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        date = Utilities.ConvertDateFormat(Utilities.dfDate, mDay, mMonth + 1, mYear);

        edt_date.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", Utilities.ConvertDateFormat(Utilities.dfDate, mDay, mMonth + 1, mYear)));

        if (Utilities.isNetworkAvailable(context)) {
            if(role.equals("4")){
                new GetBankerEventList().execute(user_id, date);
            } if(role.equals("6")){
                new GetOtherVehicleEventList().execute(user_id, date);
            }else{
            new GetEventList().execute(user_id, date);}
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_premiumdue.setVisibility(View.GONE);
        }
        fab_wish_whatsapp.setVisibility(View.GONE);
        fab_wish_notification.setVisibility(View.GONE);
        fab_wish_sms.setVisibility(View.GONE);
        cb_checkall.setVisibility(View.GONE);
    }

    private void setEventHandlers() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    cb_checkall.setChecked(false);
                    if(role.equals("4")){
                        new GetBankerEventList().execute(user_id, date);
                    } if(role.equals("6")){
                        new GetOtherVehicleEventList().execute(user_id, date);
                    }else{
                        new GetEventList().execute(user_id, date);
                    }

                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_premiumdue.setVisibility(View.GONE);
                }
            }
        });

        fab_wish_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray user_info = null;
                int count = 0;
                if (premiumDueList.size() > 0)
                {
                    for (int i = 0;i<premiumDueList.size();i++)
                    {
                        if (premiumDueList.get(i).isChecked)
                            count = count + 1;
                    }
                }
                if (premiumDueListRTO.size() > 0)
                {
                    for (int i = 0;i<premiumDueListRTO.size();i++)
                    {
                        if (premiumDueListRTO.get(i).isChecked)
                            count = count + 1;
                    }
                }
                try {
                    user_info = new JSONArray(session.getUserDetails().get(
                            ApplicationConstants.KEY_LOGIN_INFO));
                    JSONObject json = user_info.getJSONObject(0);
                    if (Integer.parseInt(json.getString("whatsappCount"))+ count <= Integer.parseInt(json.getString("maxWhatsAppLimit")))
                    {
                        if (Utilities.isInternetAvailable(context)) {
                            sendWhatsapp("message", "","","","","whatsapp");
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    }
                    else
                    {
                        Utilities.buildDialogForSmsValidation(context,count);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        fab_wish_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isInternetAvailable(context)) {
                    sendWhatsapp("message", "","","","","notification");
                    // new GetPremiumMessage().execute(user_id, "", "WHATSAPP");
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        });

        fab_wish_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray user_info = null;
                int count = 0;
                if (premiumDueList.size() > 0)
                {
                    for (int i = 0;i<premiumDueList.size();i++)
                    {
                        if (premiumDueList.get(i).isChecked)
                            count = count + 1;
                    }
                }
                if (premiumDueListRTO.size() > 0)
                {
                    for (int i = 0;i<premiumDueListRTO.size();i++)
                    {
                        if (premiumDueListRTO.get(i).isChecked)
                            count = count + 1;
                    }
                }
                try {
                    user_info = new JSONArray(session.getUserDetails().get(
                            ApplicationConstants.KEY_LOGIN_INFO));
                    JSONObject json = user_info.getJSONObject(0);
                    if (Integer.parseInt(json.getString("smsCount"))+ count <= Integer.parseInt(json.getString("maxSMSLimit")))
                    {
                        if (Utilities.isInternetAvailable(context)) {
                            sendSMS("", "","","");
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    }
                    else
                    {
                        Utilities.buildDialogForSmsValidation(context,count);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date = Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year);

                        edt_date.setText(changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy", Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year)));

                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;

                        if (Utilities.isNetworkAvailable(context)) {
                            if(role.equals("4")){
                                new GetBankerEventList().execute(user_id, date);
                            }else{
                                new GetEventList().execute(user_id, date);}
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            swipeRefreshLayout.setRefreshing(false);
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            rv_premiumdue.setVisibility(View.GONE);
                        }

                    }
                }, mYear, mMonth, mDay);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }
        });
    }
    public class GetBankerEventList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_premiumdue.setVisibility(View.GONE);
            //rv_premiumdue1.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getBankerEvent");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("date", params[1]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.EVENTSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
                rv_premiumdue.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    premiumDueList = new ArrayList<>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                EventListPojo eventMainObj = new EventListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                eventMainObj.setId(String.valueOf( i));
                                eventMainObj.setDescription(jsonObj.getString("description"));
                                eventMainObj.setDate(jsonObj.getString("date"));
                                eventMainObj.setClient_id(jsonObj.getString("id"));
                                eventMainObj.setClient_name(jsonObj.getString("ClientName"));
                                eventMainObj.setClient_mobile(jsonObj.getString("ClientMobile"));
                                eventMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                eventMainObj.setDuedate(jsonObj.getString("date"));
                                premiumDueList.add(eventMainObj);
                            }
                            if (premiumDueList.size() == 0) {
                                 ll_nothingtoshow.setVisibility(View.VISIBLE);
                                 rv_premiumdue.setVisibility(View.GONE);
                                 cb_checkall.setVisibility(View.GONE);
                            } else {
                                prepareListData();
                                //new PremiumDue_Fragment.GetEventListRTO().execute(user_id, date);
                                // rv_premiumdue.setVisibility(View.VISIBLE);
                                // ll_nothingtoshow.setVisibility(View.GONE);
                                //cb_checkall.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                         ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_premiumdue.setVisibility(View.GONE);
                         cb_checkall.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_premiumdue.setVisibility(View.GONE);
                cb_checkall.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }
    public class GetEventList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_premiumdue.setVisibility(View.GONE);
            //rv_premiumdue1.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getEventStatus");
            obj.addProperty("subtype", "premium");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("date", params[1]);
            obj.addProperty("role", role);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.EVENTSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
                rv_premiumdue.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    premiumDueList = new ArrayList<>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                EventListPojo eventMainObj = new EventListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                eventMainObj.setId(String.valueOf( i));
                                eventMainObj.setDescription(jsonObj.getString("description"));
                                eventMainObj.setDate(jsonObj.getString("date"));
                                eventMainObj.setClient_id(jsonObj.getString("id"));
                                eventMainObj.setClient_name(jsonObj.getString("ClientName"));
                                eventMainObj.setClient_mobile(jsonObj.getString("ClientMobile"));
                                eventMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                eventMainObj.setDuedate(jsonObj.getString("date"));
                                premiumDueList.add(eventMainObj);
                            }
                            if (premiumDueList.size() == 0) {
                                new PremiumDue_Fragment.GetEventListRTO().execute(user_id, date);
                               // ll_nothingtoshow.setVisibility(View.VISIBLE);
                               // rv_premiumdue.setVisibility(View.GONE);
                               // cb_checkall.setVisibility(View.GONE);
                            } else {
                                new PremiumDue_Fragment.GetEventListRTO().execute(user_id, date);
                               // rv_premiumdue.setVisibility(View.VISIBLE);
                               // ll_nothingtoshow.setVisibility(View.GONE);
                                //cb_checkall.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                        new PremiumDue_Fragment.GetEventListRTO().execute(user_id, date);
                       // ll_nothingtoshow.setVisibility(View.VISIBLE);
                        //rv_premiumdue.setVisibility(View.GONE);
                       // cb_checkall.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_premiumdue.setVisibility(View.GONE);
                cb_checkall.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }
    public class GetOtherVehicleEventList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_premiumdue.setVisibility(View.GONE);
            //rv_premiumdue1.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getEventStatus");
            obj.addProperty("subtype", "premium");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("date", params[1]);
            obj.addProperty("role", role);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.EVENTSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
                rv_premiumdue.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    premiumDueList = new ArrayList<>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                EventListPojo eventMainObj = new EventListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                eventMainObj.setId(String.valueOf( i));
                                eventMainObj.setDescription(jsonObj.getString("description"));
                                eventMainObj.setDate(jsonObj.getString("date"));
                                eventMainObj.setClient_id(jsonObj.getString("id"));
                                eventMainObj.setClient_name(jsonObj.getString("ClientName"));
                                eventMainObj.setClient_mobile(jsonObj.getString("ClientMobile"));
                                eventMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                eventMainObj.setDuedate(jsonObj.getString("date"));
                                premiumDueList.add(eventMainObj);
                            }
                            if (premiumDueList.size() == 0) {
                                 ll_nothingtoshow.setVisibility(View.VISIBLE);
                                 rv_premiumdue.setVisibility(View.GONE);
                                 cb_checkall.setVisibility(View.GONE);
                            } else {
                                prepareListData();
                                 rv_premiumdue.setVisibility(View.VISIBLE);
                                 ll_nothingtoshow.setVisibility(View.GONE);
                                cb_checkall.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                         ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_premiumdue.setVisibility(View.GONE);
                         cb_checkall.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_premiumdue.setVisibility(View.GONE);
                cb_checkall.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }
    public class GetEventListRTO extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getEventStatus");
            obj.addProperty("subtype", "dealer");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("date", params[1]);
            obj.addProperty("role", role);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.EVENTSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                swipeRefreshLayout.setRefreshing(false);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    premiumDueListRTO = new ArrayList<>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                EventListPojo eventMainObj = new EventListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                eventMainObj.setId(jsonObj.getString("id"));
                                eventMainObj.setDescription(jsonObj.getString("description"));
                                eventMainObj.setDate(jsonObj.getString("date"));
                                eventMainObj.setClient_id(jsonObj.getString("id"));
                                eventMainObj.setClient_name(jsonObj.getString("ClientName"));
                                eventMainObj.setClient_mobile(jsonObj.getString("ClientMobile"));
                                eventMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                eventMainObj.setDuedate(jsonObj.getString("date"));
                                premiumDueListRTO.add(eventMainObj);
                            }
                            if (premiumDueListRTO.size() == 0 && premiumDueList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_premiumdue.setVisibility(View.GONE);
                            } else {
                                prepareListData();
                                rv_premiumdue.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                                cb_checkall.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                        if (premiumDueListRTO.size() == 0 && premiumDueList.size() == 0) {
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            rv_premiumdue.setVisibility(View.GONE);
                        }else {
                            prepareListData();
                            rv_premiumdue.setVisibility(View.VISIBLE);
                            ll_nothingtoshow.setVisibility(View.GONE);
                            cb_checkall.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_premiumdue.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        if(role.equals("1")) {
            // Adding child data
            listDataHeader.add("Own");
            //listDataHeader.add("Vehicle Dealer");
        }else if(role.equals("2")){
            listDataHeader.add("Own");
           //listDataHeader.add("RTO Agent");
        }else if(role.equals("4")){
            listDataHeader.add("Own");
            //listDataHeader.add("RTO Agent");
        }else if(role.equals("6")){
        listDataHeader.add("Own");
        //listDataHeader.add("RTO Agent");
        }


        if(role.equals("4")){
            List list1 = new ArrayList();
            list1.addAll(premiumDueList);
            listDataChild.put(listDataHeader.get(0), list1); // Header, Child data
            // listDataChild.put(listDataHeader.get(1), list2); // Header, Child data
            // listDataChild.put(listDataHeader.get(2), list3);
            listAdapter = new ExpandableListViewRTOAdapter(context,listDataHeader,listDataChild);
            rv_premiumdue.setAdapter(listAdapter);
            rv_premiumdue.expandGroup(0);
        }else {
            //  listDataHeader.add("RTO Agent");
            List list1 = new ArrayList();
            list1.addAll(premiumDueList);
            List list2 = new ArrayList();
            list2.addAll(premiumDueListRTO);

            // List list3 = new ArrayList();
            // list3.addAll(premiumDueListRTO);


            listDataChild.put(listDataHeader.get(0), list1); // Header, Child data
            // listDataChild.put(listDataHeader.get(1), list2); // Header, Child data
            // listDataChild.put(listDataHeader.get(2), list3);
            listAdapter = new ExpandableListViewRTOAdapter(context, listDataHeader, listDataChild);
            rv_premiumdue.setAdapter(listAdapter);
            rv_premiumdue.expandGroup(0);
        }
        //listDataChild.put(listDataHeader.get(1), nowShowing);
        //listDataChild.put(listDataHeader.get(2), comingSoon);
    }

    public class ExpandableListViewRTOAdapter extends BaseExpandableListAdapter {
        private Context context;

        // group titles
        private List<String> listDataGroup;

        // child data in format of header title, child title
        private HashMap<String, List<String>> listDataChild;

        public ExpandableListViewRTOAdapter(Context context, List<String> listDataGroup,
                                            HashMap<String, List<String>> listChildData)  {
            this.context = context;
            this.listDataGroup = listDataGroup;
            this.listDataChild = listChildData;
        }
        @Override
        public int getGroupCount() {
            return this.listDataGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.listDataChild.get(this.listDataGroup.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listDataGroup.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                    .get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }

            TextView textViewGroup = convertView
                    .findViewById(R.id.lblListHeader);
            textViewGroup.setTypeface(null, Typeface.BOLD);
            textViewGroup.setText(headerTitle);

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
         //   final String childText = (String) getChild(groupPosition, childPosition);

            EventListPojo ch = (EventListPojo) getChild(groupPosition, childPosition);
           // EventListPojo annivarsaryDetails = new EventListPojo();
           // annivarsaryDetails = premiumDueList.get(childPosition);
            //ch = premiumDueList.get(childPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_row_premium_due, null);
            }
                TextView textViewChild = convertView
                        .findViewById(R.id.tv_clientname);
            TextView textViewChild1 = convertView
                    .findViewById(R.id.tv_reminder);

            TextView textViewChild2 = convertView
                    .findViewById(R.id.tv_vehicle_no);

                final ImageView imv_sms = convertView.findViewById(R.id.imv_sms);
                final ImageView imv_whatsapp = convertView.findViewById(R.id.imv_whatsapp);
                final ImageView img_call = convertView.findViewById(R.id.imv_call);
                final ImageView img_share = convertView.findViewById(R.id.imv_share);
                final ImageView img_notification = convertView.findViewById(R.id.imv_notification);
                final CheckBox cb_wish = convertView.findViewById(R.id.cb_wish);

                textViewChild.setText(ch.getClient_name());
                textViewChild2.setText(ch.getVehicle_no());
                textViewChild1.setText("Reminder For - "+ch.getDescription());

            if(groupPosition == 1)
            {
                imv_sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONArray user_info = null;
                        try {
                            user_info = new JSONArray(session.getUserDetails().get(
                                    ApplicationConstants.KEY_LOGIN_INFO));
                            JSONObject json = user_info.getJSONObject(0);
                            if (Integer.parseInt(json.getString("smsCount"))+ 1 <= Integer.parseInt(json.getString("maxSMSLimit")))
                            {
                                if (Utilities.isInternetAvailable(context)) {
                                    sendSMS(premiumDueListRTO.get(childPosition).getClient_id(), premiumDueListRTO.get(childPosition).getDescription(),premiumDueList.get(childPosition).getVehicle_no(),premiumDueList.get(childPosition).getDuedate());
                                } else {
                                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                                }
                            }
                            else
                            {
                                Utilities.buildDialogForSmsValidation(context,1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                imv_whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONArray user_info = null;
                        try {
                            user_info = new JSONArray(session.getUserDetails().get(
                                    ApplicationConstants.KEY_LOGIN_INFO));
                            JSONObject json = user_info.getJSONObject(0);
                            if (Integer.parseInt(json.getString("whatsappCount"))+ 1 <= Integer.parseInt(json.getString("maxWhatsAppLimit")))
                            {
                                if (Utilities.isInternetAvailable(context))
                                {
                                    sendWhatsapp("message", premiumDueListRTO.get(childPosition).getClient_id(), premiumDueListRTO.get(childPosition).getDescription(), premiumDueList.get(childPosition).getDescription(),premiumDueList.get(childPosition).getDuedate(),"whatsapp");
                                }
                                else {
                                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                                }
                            }
                            else
                            {
                                Utilities.buildDialogForSmsValidation(context,1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                img_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isInternetAvailable(context)) {
                            sendWhatsapp("message", premiumDueListRTO.get(childPosition).getClient_id(), premiumDueListRTO.get(childPosition).getDescription(), premiumDueList.get(childPosition).getDescription(),premiumDueList.get(childPosition).getDuedate(),"notification");
                      } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    }
                });

                img_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {
                            context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null)));
                            Utilities.showMessageString(context, "Please provide permission for making call");
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            alertDialogBuilder.setTitle("Make a Call");
                            alertDialogBuilder.setIcon(R.drawable.icon_call_24dp);
                            alertDialogBuilder.setMessage("Are you sure you want to call " + premiumDueListRTO.get(childPosition).getClient_name() + " ?");
                            alertDialogBuilder.setCancelable(true);
                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @SuppressLint("MissingPermission")
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    context.startActivity(new Intent(Intent.ACTION_CALL,
                                            Uri.parse("tel:" + premiumDueListRTO.get(childPosition).getClient_mobile())));
                                }
                            });
                            alertDialogBuilder.setNegativeButton(
                                    "No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = alertDialogBuilder.create();
                            alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                            alert11.show();
                        }
                    }
                });



            }
            if(groupPosition == 0){
                imv_sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONArray user_info = null;
                        try {
                            user_info = new JSONArray(session.getUserDetails().get(
                                    ApplicationConstants.KEY_LOGIN_INFO));
                            JSONObject json = user_info.getJSONObject(0);
                            if (Integer.parseInt(json.getString("smsCount"))+ 1 <= Integer.parseInt(json.getString("maxSMSLimit")))
                            {
                                if (Utilities.isInternetAvailable(context)) {
                                    sendSMS(premiumDueList.get(childPosition).getClient_id(), premiumDueList.get(childPosition).getDescription(),premiumDueList.get(childPosition).getVehicle_no(),premiumDueList.get(childPosition).getDuedate());
                                    // new GetPremiumMessage().execute(user_id, premiumDueList.get(childPosition).getClient_id(), "SMS");
                                } else {
                                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                                }
                            }
                            else
                            {
                                Utilities.buildDialogForSmsValidation(context,1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

                imv_whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONArray user_info = null;
                        try {
                            user_info = new JSONArray(session.getUserDetails().get(
                                    ApplicationConstants.KEY_LOGIN_INFO));
                            JSONObject json = user_info.getJSONObject(0);
                            if (Integer.parseInt(json.getString("whatsappCount"))+ 1 <= Integer.parseInt(json.getString("maxWhatsAppLimit")))
                            {
                                if (Utilities.isInternetAvailable(context)) {
                                    sendWhatsapp("message", premiumDueList.get(childPosition).getClient_id(), premiumDueList.get(childPosition).getDescription(), premiumDueList.get(childPosition).getVehicle_no(), premiumDueList.get(childPosition).getDuedate(),"whatsapp");
                                 } else {
                                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                                }
                            }
                            else
                            {
                                Utilities.buildDialogForSmsValidation(context,1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                img_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Utilities.isInternetAvailable(context)) {
                            sendWhatsapp("message", premiumDueList.get(childPosition).getClient_id(), premiumDueList.get(childPosition).getDescription(), premiumDueList.get(childPosition).getVehicle_no(), premiumDueList.get(childPosition).getDuedate(),"notification");

                            // sendSMS( premiumDueList.get(childPosition).getClient_id(),premiumDueList.get(childPosition).getDescription());
                            // new GetPremiumMessage().execute(user_id, premiumDueList.get(childPosition).getClient_id(), "WHATSAPP");
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    }
                });

                img_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {
                            context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null)));
                            Utilities.showMessageString(context, "Please provide permission for making call");
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            alertDialogBuilder.setTitle("Make a Call");
                            alertDialogBuilder.setIcon(R.drawable.icon_call_24dp);
                            alertDialogBuilder.setMessage("Are you sure you want to call " + premiumDueList.get(childPosition).getClient_name() + " ?");
                            alertDialogBuilder.setCancelable(true);
                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @SuppressLint("MissingPermission")
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    context.startActivity(new Intent(Intent.ACTION_CALL,
                                            Uri.parse("tel:" + premiumDueList.get(childPosition).getClient_mobile())));
                                }
                            });
                            alertDialogBuilder.setNegativeButton(
                                    "No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = alertDialogBuilder.create();
                            alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                            alert11.show();
                        }
                    }
                });

                img_share.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        ShareMessage(premiumDueList.get(childPosition).getClient_id(),premiumDueList.get(childPosition).getClient_name(),premiumDueList.get(childPosition).getDescription(),premiumDueList.get(childPosition).getVehicle_no(),premiumDueList.get(childPosition).getDuedate(),premiumDueList.get(childPosition).getClient_mobile());
            }
                });

            }


                cb_wish.setChecked(ch.isChecked());

                cb_wish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(cb_wish.isChecked()){
//                            EventListPojo ch = (EventListPojo) getChild(groupPosition, childPosition);
//                            ch.setChecked(true);
//                        }else{
//                            EventListPojo ch = (EventListPojo) getChild(groupPosition, childPosition);
//                            ch.setChecked(false);
//                        }


                        if(groupPosition == 1)
                        {
                            if (cb_wish.isChecked()) {
                                premiumDueListRTO.get(childPosition).setChecked(true);

                            } else {
                                premiumDueListRTO.get(childPosition).setChecked(false);
                            }

                            if (isAllValuesCheckedRTO()) {
                                cb_checkall.setChecked(true);
                                fab_wish_whatsapp.setVisibility(View.VISIBLE);
                                fab_wish_notification.setVisibility(View.VISIBLE);
                                fab_wish_sms.setVisibility(View.VISIBLE);

                            } else {
                                cb_checkall.setChecked(false);
                                fab_wish_whatsapp.setVisibility(View.GONE);
                                fab_wish_notification.setVisibility(View.GONE);
                                fab_wish_sms.setVisibility(View.GONE);
                            }

                            if (isAtleastOneCheckedRTO(premiumDueListRTO)) {
                                fab_wish_whatsapp.setVisibility(View.VISIBLE);
                                fab_wish_notification.setVisibility(View.VISIBLE);
                                fab_wish_sms.setVisibility(View.VISIBLE);
                            } else {
                                fab_wish_whatsapp.setVisibility(View.GONE);
                                fab_wish_notification.setVisibility(View.GONE);
                                fab_wish_sms.setVisibility(View.GONE);
                            }


                        }else if(groupPosition == 0)
                        {
                            if (cb_wish.isChecked()) {
                                premiumDueList.get(childPosition).setChecked(true);
                            } else {
                                premiumDueList.get(childPosition).setChecked(false);
                            }

                            if (isAllValuesCheckedRTO()) {
                                cb_checkall.setChecked(true);
                                fab_wish_whatsapp.setVisibility(View.VISIBLE);
                                fab_wish_notification.setVisibility(View.VISIBLE);
                                fab_wish_sms.setVisibility(View.VISIBLE);

                            } else {
                                cb_checkall.setChecked(false);
                                fab_wish_whatsapp.setVisibility(View.GONE);
                                fab_wish_notification.setVisibility(View.GONE);
                                fab_wish_sms.setVisibility(View.GONE);
                            }

                            if (isAtleastOneChecked(premiumDueList)) {
                                fab_wish_whatsapp.setVisibility(View.VISIBLE);
                                fab_wish_notification.setVisibility(View.VISIBLE);
                                fab_wish_sms.setVisibility(View.VISIBLE);
                            } else {
                                fab_wish_whatsapp.setVisibility(View.GONE);
                                fab_wish_notification.setVisibility(View.GONE);
                                fab_wish_sms.setVisibility(View.GONE);
                            }
                        }
                    }
                });

            cb_checkall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < premiumDueList.size(); i++) {
                        if (((CheckBox) v).isChecked()) {
                            cb_wish.setChecked(true);
                            premiumDueList.get(i).setChecked(true);
                            fab_wish_whatsapp.setVisibility(View.VISIBLE);
                            fab_wish_notification.setVisibility(View.VISIBLE);
                            fab_wish_sms.setVisibility(View.VISIBLE);
                        } else {
                            cb_wish.setChecked(false);
                            premiumDueList.get(i).setChecked(false);
                            fab_wish_whatsapp.setVisibility(View.GONE);
                            fab_wish_notification.setVisibility(View.GONE);
                            fab_wish_sms.setVisibility(View.GONE);
                        }
                    }
                    for (int i = 0; i < premiumDueListRTO.size(); i++) {
                        if (((CheckBox) v).isChecked()) {
                            cb_wish.setChecked(true);
                            premiumDueListRTO.get(i).setChecked(true);
                            fab_wish_whatsapp.setVisibility(View.VISIBLE);
                            fab_wish_notification.setVisibility(View.VISIBLE);
                            fab_wish_sms.setVisibility(View.VISIBLE);
                        } else {
                            cb_wish.setChecked(false);
                            premiumDueListRTO.get(i).setChecked(false);
                            fab_wish_whatsapp.setVisibility(View.GONE);
                            fab_wish_notification.setVisibility(View.GONE);
                            fab_wish_sms.setVisibility(View.GONE);
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }
            });

                return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private boolean isAtleastOneChecked(ArrayList<EventListPojo> premiumDueList) {
            for (int i = 0; i < premiumDueList.size(); i++)
                if (premiumDueList.get(i).isChecked())
                    return true;
            return false;
        }

        private boolean isAllValuesChecked(ArrayList<EventListPojo> premiumDueList) {
            for (int i = 0; i < premiumDueList.size(); i++)
                if (!premiumDueList.get(i).isChecked())
                    return false;
            return true;
        }


        private boolean isAtleastOneCheckedRTO(ArrayList<EventListPojo> premiumDueListRTO) {
            for (int i = 0; i < premiumDueListRTO.size(); i++)
                if (premiumDueListRTO.get(i).isChecked())
                    return true;
            return false;
        }

        private boolean isAllValuesCheckedRTO() {
            boolean list1 = false,list2= false;
            for (int i = 0; i < premiumDueListRTO.size(); i++)
                if (!premiumDueListRTO.get(i).isChecked())
                    return false;
            list1= true;
            for (int i = 0; i < premiumDueList.size(); i++)
                if (!premiumDueList.get(i).isChecked())
                    return false;
            list2= true;
            if(list1 && list2)
                return true;
            else
                return false;
        }

    }



    public class GetPremiumMessage extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;
        private String singleReceiverID = "";
        private String messageType = "";

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
            singleReceiverID = params[1];
            messageType = params[2];

            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getPremiummessage"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.SETTINGSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        JSONArray jsonarr = jsonObject.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                id = jsonObj.getString("id");
                                message = jsonObj.getString("message");
                            }
                        }
                        whatsappPicUrl = jsonObject.getString("url");
                        Uri uri = Uri.parse(whatsappPicUrl);
                        whatsappPic = uri.getLastPathSegment();

                    }


                    if (messageType.equals("SMS")) {
                        final EditText edt_smsmessage = new EditText(context);
                        float dpi = context.getResources().getDisplayMetrics().density;
                        edt_smsmessage.setText(message);
                        edt_smsmessage.setSelection(message.length());
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setTitle("Send Message");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        final AlertDialog alertD = builder.create();
                        alertD.setView(edt_smsmessage, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();

                        edt_smsmessage.addTextChangedListener(new TextWatcher() {
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
                    } else if (messageType.equals("WHATSAPP")) {
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.prompt_send_whatsappmsg, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setTitle("Whatsapp Message");
                        alertDialogBuilder.setView(promptView);

                        dialog_edt_whatsappmessage = promptView.findViewById(R.id.dialog_edt_whatsappmessage);
                        dialog_imv_whatsapppic = promptView.findViewById(R.id.dialog_imv_whatsapppic);
                        final CheckBox cb_whatsappmsg = promptView.findViewById(R.id.cb_whatsappmsg);
                        final CheckBox cb_whatsappimg = promptView.findViewById(R.id.cb_whatsappimg);

                        dialog_edt_whatsappmessage.setText(message);

                        if (!whatsappPicUrl.equals("")) {
                            Picasso.with(context)
                                    .load(whatsappPicUrl)
                                    .placeholder(R.drawable.img_photo)
                                    .into(dialog_imv_whatsapppic);
                        }

                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!cb_whatsappmsg.isChecked() && !cb_whatsappimg.isChecked()) {
                                    Utilities.showMessageString(context, "Please Check Atleast One");
                                    return;
                                }

                                String whatsappPicSend = "", whatsappMsgSend = "";
                                if (cb_whatsappmsg.isChecked()) {
                                    whatsappMsgSend = dialog_edt_whatsappmessage.getText().toString().trim();
                                }
                                if (cb_whatsappimg.isChecked()) {
                                    whatsappPicSend = whatsappPic;
                                }

                            }
                        });

                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog alertD = alertDialogBuilder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();

                        dialog_edt_whatsappmessage.addTextChangedListener(new TextWatcher() {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

  private void ShareMessage(String clientId,String clientName,String description,String vehicleNo,String duedate,String mobile){
      clientMobile = mobile;
      JsonObject mainObj = new JsonObject();
      mainObj.addProperty("type", "sharePremiumMsg");
      mainObj.addProperty("client_name", clientName);
      mainObj.addProperty("description", description);
      mainObj.addProperty("vahicle_no", vehicleNo);
      mainObj.addProperty("due_date", duedate);
      mainObj.addProperty("client_id", clientId);
      mainObj.addProperty("user_name", name);

      if (Utilities.isInternetAvailable(context)) {
          new SharePremiumMsg().execute(mainObj.toString());
      } else {
          Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
      }
  }

    public class SharePremiumMsg extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.BIRTHDAYANNIVERSARYAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "",shareMsg = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                   // message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                       shareMsg = mainObj.getString("result");


                        try {
                            PackageManager pm = getActivity().getPackageManager();
                            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
                            sendIntent.setType("text/plain");
                            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
                            sendIntent.putExtra("jid", "91"+clientMobile + "@s.whatsapp.net"); //phone number without "+" prefix
                            sendIntent.setPackage("com.whatsapp");
                            startActivity(sendIntent);
                        } catch (PackageManager.NameNotFoundException e) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            alertDialogBuilder.setTitle("Whatsapp not Installed");
                            alertDialogBuilder.setIcon(R.drawable.icon_whatsapp);
                            alertDialogBuilder.setMessage("Please install Whatsapp and try again!!");
                            alertDialogBuilder.setCancelable(true);
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @SuppressLint("MissingPermission")
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                          /*  context.startActivity(new Intent(Intent.ACTION_CALL,
                                                    Uri.parse("tel:" + mob)));*/
                                }
                            });

                            AlertDialog alert11 = alertDialogBuilder.create();
                            alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                            alert11.show();
                        }



//                           Intent shareIntent = new Intent();
//                           shareIntent.setAction(Intent.ACTION_SENDTO);
//                           shareIntent.setType("text/plain");
//                           shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
//                           shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
//                           shareIntent.putExtra("jid", clientMobile + "@s.whatsapp.net"); //phone number without "+" prefix
//                           shareIntent.setPackage("com.whatsapp");
//                           startActivity(Intent.createChooser(shareIntent, "Share Message"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendSMS(String singleReceiverID,String description,String vehicleNo,String dueDate) {
        JsonArray clientIdJSONArray = new JsonArray();
        JsonArray clientIdJSONArray1 = new JsonArray();

        if (singleReceiverID.equals("")) {
            if(premiumDueList.size() > 0) {
                for (int i = 0; i < premiumDueList.size(); i++) {
                    if (premiumDueList.get(i).isChecked()) {
                        JsonObject childObj = new JsonObject();
                        childObj.addProperty("id", premiumDueList.get(i).getClient_id());
                        childObj.addProperty("description", premiumDueList.get(i).getDescription());
                        childObj.addProperty("vehicle_no", premiumDueList.get(i).getVehicle_no());
                        childObj.addProperty("date", premiumDueList.get(i).getDate());
                        clientIdJSONArray.add(childObj);

                    }
                }
            }
             if(premiumDueListRTO.size() > 0){
                for (int i = 0; i < premiumDueListRTO.size(); i++) {
                    if (premiumDueListRTO.get(i).isChecked()) {
                        JsonObject childObj = new JsonObject();
                        childObj.addProperty("id", premiumDueListRTO.get(i).getClient_id());
                        childObj.addProperty("description", premiumDueListRTO.get(i).getDescription());
                        childObj.addProperty("vehicle_no", premiumDueListRTO.get(i).getVehicle_no());
                        childObj.addProperty("date", premiumDueListRTO.get(i).getDate());
                        clientIdJSONArray.add(childObj);

                    }
                }
            }
        } else {
            JsonObject childObj = new JsonObject();
            childObj.addProperty("id", singleReceiverID);
            childObj.addProperty("description", description);
            childObj.addProperty("vehicle_no",vehicleNo);
            childObj.addProperty("date",dueDate);
            clientIdJSONArray.add(childObj);

        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "sendPremiumSMS");
        mainObj.add("id", clientIdJSONArray);
        mainObj.addProperty("user_id", test_id);

        if (Utilities.isInternetAvailable(context)) {
            new SendAnniversarySMS().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }

    }

    public class SendAnniversarySMS extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.BIRTHDAYANNIVERSARYAPI, params[0]);
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
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        JSONObject obj = jsonarr.getJSONObject(0);
                        changeSessionSMSCount(obj.getString("smsCount"),obj.getString("whatsappCount"),obj.getString("maxSMSLimit"),obj.getString("maxWhatsAppLimit"));

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("SMS Sent Successfully");
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
                    } else {

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendWhatsapp(String message, String singleReceiverID , String description,String vehicleNo,String dueDate,String subtype) {
        JsonArray clientIdJSONArray = new JsonArray();
        JsonArray clientIdJSONArray1 = new JsonArray();

        if (singleReceiverID.equals("")) {
            if(premiumDueList.size() > 0) {
                for (int i = 0; i < premiumDueList.size(); i++) {
                    if (premiumDueList.get(i).isChecked()) {
                        JsonObject childObj = new JsonObject();
                        childObj.addProperty("id", premiumDueList.get(i).getClient_id());
                        childObj.addProperty("description",premiumDueList.get(i).getDescription());
                        childObj.addProperty("vehicle_no",premiumDueList.get(i).getVehicle_no());
                        childObj.addProperty("date",premiumDueList.get(i).getDuedate());
                        clientIdJSONArray.add(childObj);


                    }
                }
            }
            if(premiumDueListRTO.size() > 0){
                for (int i = 0; i < premiumDueListRTO.size(); i++) {
                    if (premiumDueListRTO.get(i).isChecked()) {
                        JsonObject childObj = new JsonObject();
                        childObj.addProperty("id", premiumDueListRTO.get(i).getClient_id());
                        childObj.addProperty("description",premiumDueListRTO.get(i).getDescription());
                        childObj.addProperty("vehicle_no",premiumDueListRTO.get(i).getVehicle_no());
                        childObj.addProperty("date",premiumDueListRTO.get(i).getDuedate());
                        clientIdJSONArray.add(childObj);


                    }
                }
            }
        } else {
            JsonObject childObj = new JsonObject();
            childObj.addProperty("id", singleReceiverID);
            childObj.addProperty("description", description);
            childObj.addProperty("vehicle_no", vehicleNo);
            childObj.addProperty("date", dueDate);
            clientIdJSONArray.add(childObj);



        }

        JsonObject mainObj = new JsonObject();
        if(subtype.equals("notification")){
            mainObj.addProperty("type", "sendpremiumNotification");
        }else {
            mainObj.addProperty("type", "sendPremiumWhtasAppMsg");
        }
        mainObj.add("id", clientIdJSONArray);
        mainObj.addProperty("user_id", test_id);
        mainObj.addProperty("userName", name);

        if (Utilities.isInternetAvailable(context)) {
            new SendAnniversaryWhatsapp().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }

    }

    public class SendAnniversaryWhatsapp extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.BIRTHDAYANNIVERSARYAPI, params[0]);
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
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        JSONObject obj = jsonarr.getJSONObject(0);
                        changeSessionSMSCount(obj.getString("smsCount"),obj.getString("whatsappCount"),obj.getString("maxSMSLimit"),obj.getString("maxWhatsAppLimit"));

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage(message);
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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage(message);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void changeSessionSMSCount(String smsCount,String whatsappCount,String maxSMS,String maxWhatsapp)
    {
        JSONArray user_info = null;
        try {
            user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            json.put("smsCount",smsCount);
            json.put("whatsappCount",whatsappCount);
            json.put("maxSMSLimit",maxSMS);
            json.put("maxWhatsAppLimit",maxWhatsapp);
            session.updateSession(user_info.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
