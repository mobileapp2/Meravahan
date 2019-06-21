package in.rto.collections.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.adapters.ExpandableListViewAdapter;
import in.rto.collections.models.EventListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class PremiumDueSecond_Fragment extends Fragment {
    public LinearLayout ll_parent;
    private Context context;
    private FloatingActionButton fab_wish_whatsapp, fab_wish_sms;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_nothingtoshow;
    private LinearLayoutManager layoutManager, layoutManager1;
    private UserSessionManager session;
    private int mYear, mMonth, mDay;
    private String user_id, date, role;
    private ArrayList<EventListPojo> premiumDueList, premiumDueListDealer, premiumDueListRTO;
    private String id = "", message = "", whatsappPicUrl = "", whatsappPic = "";
    private EditText edt_date, dialog_edt_whatsappmessage;
    private CheckBox cb_checkall;
    private ImageView dialog_imv_whatsapppic;
    ExpandableListView rv_premiumdue;
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_premium_due_second, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        fab_wish_whatsapp = rootView.findViewById(R.id.fab_wish_whatsapp);
        fab_wish_sms = rootView.findViewById(R.id.fab_wish_sms);
        ll_nothingtoshow = rootView.findViewById(R.id.ll_nothingtoshow);
        rv_premiumdue = rootView.findViewById(R.id.rv_premiumdue);
        // rv_premiumdue1 = rootView.findViewById(R.id.rv_premiumdue1);
        // rv_premiumdue2 = rootView.findViewById(R.id.rv_premiumdue2);
        ll_parent = getActivity().findViewById(R.id.ll_parent);
//      shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        cb_checkall = rootView.findViewById(R.id.cb_checkall);
        edt_date = rootView.findViewById(R.id.edt_date);
        // layoutManager = new LinearLayoutManager(context);
        // layoutManager1 = new LinearLayoutManager(context);
        // rv_premiumdue.setLayoutManager(layoutManager);
        // rv_premiumdue1.setLayoutManager(layoutManager1);
        // rv_premiumdue2.setLayoutManager(layoutManager);


        premiumDueList = new ArrayList<>();
    }

    public void onResume() {
        setDefault();
        super.onResume();
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

    private void setDefault() {

        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        date = Utilities.ConvertDateFormat(Utilities.dfDate, mDay, mMonth + 1, mYear);

        edt_date.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", Utilities.ConvertDateFormat(Utilities.dfDate, mDay, mMonth + 1, mYear)));

        if (Utilities.isNetworkAvailable(context)) {
            new PremiumDueSecond_Fragment.GetEventList().execute(user_id, date);
            // new GetEventListRTO().execute(user_id, date);
//            new GetEventList().execute("472", "2018-10-13");
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_premiumdue.setVisibility(View.GONE);
        }


        fab_wish_whatsapp.setVisibility(View.GONE);
        fab_wish_sms.setVisibility(View.GONE);
        cb_checkall.setVisibility(View.GONE);
    }

    private void setEventHandlers() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new PremiumDueSecond_Fragment.GetEventList().execute(user_id, date);
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
                for (int i = 0; i < premiumDueList.size(); i++) {
                    if (premiumDueList.get(i).isChecked)
                        count = count + 1;
                }
                try {
                    user_info = new JSONArray(session.getUserDetails().get(
                            ApplicationConstants.KEY_LOGIN_INFO));
                    JSONObject json = user_info.getJSONObject(0);
                    if (Integer.parseInt(json.getString("whatsappCount")) + count <= Integer.parseInt(json.getString("maxWhatsAppLimit"))) {
                        if (Utilities.isInternetAvailable(context)) {
                            new PremiumDueSecond_Fragment.GetPremiumMessage().execute(user_id, "", "WHATSAPP");
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    } else {
                        Utilities.buildDialogForSmsValidation(context, count);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        fab_wish_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray user_info = null;
                int count = 0;
                for (int i = 0; i < premiumDueList.size(); i++) {
                    if (premiumDueList.get(i).isChecked)
                        count = count + 1;
                }
                try {
                    user_info = new JSONArray(session.getUserDetails().get(
                            ApplicationConstants.KEY_LOGIN_INFO));
                    JSONObject json = user_info.getJSONObject(0);
                    if (Integer.parseInt(json.getString("smsCount")) + count <= Integer.parseInt(json.getString("maxSMSLimit"))) {
                        if (Utilities.isInternetAvailable(context)) {
                            new PremiumDueSecond_Fragment.GetPremiumMessage().execute(user_id, "", "SMS");
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    } else {
                        Utilities.buildDialogForSmsValidation(context, count);
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
                            new PremiumDueSecond_Fragment.GetEventList().execute(user_id, date);
                            // new GetEventListVehicle().execute(user_id, date);
                            // new GetEventListRTO().execute(user_id, date);
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

        cb_checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < premiumDueList.size(); i++) {
                    //GetEventListAdapter.MyViewHolder myViewHolder =
                    // (GetEventListAdapter.MyViewHolder) rv_premiumdue.findViewHolderForAdapterPosition(i);

                    if (((CheckBox) v).isChecked()) {
                        //myViewHolder.cb_wish.setChecked(true);
                        premiumDueList.get(i).setChecked(true);
                        fab_wish_whatsapp.setVisibility(View.VISIBLE);
                        fab_wish_sms.setVisibility(View.VISIBLE);
                    } else {
                        // myViewHolder.cb_wish.setChecked(false);
                        premiumDueList.get(i).setChecked(false);
                        fab_wish_whatsapp.setVisibility(View.GONE);
                        fab_wish_sms.setVisibility(View.GONE);
                    }
                }
            }
        });


    }

    public class GetEventList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
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
                                eventMainObj.setId(jsonObj.getString("id"));
                                eventMainObj.setDescription(jsonObj.getString("description"));
                                eventMainObj.setDate(jsonObj.getString("date"));
                                eventMainObj.setClient_id(jsonObj.getString("id"));
                                eventMainObj.setClient_name(jsonObj.getString("ClientName"));
                                eventMainObj.setClient_mobile(jsonObj.getString("ClientName"));
                                eventMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                premiumDueList.add(eventMainObj);
                            }
                            if (premiumDueList.size() == 0) {
                                new PremiumDueSecond_Fragment.GetEventListDealer().execute(user_id, date);
                                //ll_nothingtoshow.setVisibility(View.VISIBLE);
                                //rv_premiumdue.setVisibility(View.GONE);
                                // cb_checkall.setVisibility(View.GONE);
                            } else {
                                new PremiumDueSecond_Fragment.GetEventListDealer().execute(user_id, date);
                                //rv_premiumdue.setVisibility(View.VISIBLE);
                                //ll_nothingtoshow.setVisibility(View.GONE);
                                // cb_checkall.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        new PremiumDueSecond_Fragment.GetEventListDealer().execute(user_id, date);
                        // ll_nothingtoshow.setVisibility(View.VISIBLE);
                        // rv_premiumdue.setVisibility(View.GONE);
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

    public class GetEventListDealer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_premiumdue.setVisibility(View.GONE);
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
                rv_premiumdue.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    premiumDueListDealer = new ArrayList<>();
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
                                eventMainObj.setClient_mobile(jsonObj.getString("ClientName"));
                                eventMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                premiumDueListDealer.add(eventMainObj);
                            }
                            if (premiumDueListDealer.size() == 0) {
                                new PremiumDueSecond_Fragment.GetEventListRTO().execute(user_id, date);
                                // ll_nothingtoshow.setVisibility(View.VISIBLE);
                                // rv_premiumdue.setVisibility(View.GONE);
                                //cb_checkall.setVisibility(View.GONE);
                            } else {
                                new PremiumDueSecond_Fragment.GetEventListRTO().execute(user_id, date);
                                //rv_premiumdue.setVisibility(View.VISIBLE);
                                //ll_nothingtoshow.setVisibility(View.GONE);
                                //cb_checkall.setVisibility(View.VISIBLE);
                            }
                            // rv_premiumdue.setAdapter(new DealerPremiumDue_Fragment.GetEventListAdapter());
                        }
                    } else {
                        new PremiumDueSecond_Fragment.GetEventListRTO().execute(user_id, date);
                        // ll_nothingtoshow.setVisibility(View.VISIBLE);
                        // rv_premiumdue.setVisibility(View.GONE);
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

    public class GetEventListRTO extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_premiumdue.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getEventStatus");
            obj.addProperty("subtype", "agent");
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
                                eventMainObj.setClient_mobile(jsonObj.getString("ClientName"));
                                eventMainObj.setVehicle_no(jsonObj.getString("vehicle_no"));
                                premiumDueListRTO.add(eventMainObj);
                            }
                            if (premiumDueListRTO.size() == 0 && premiumDueListDealer.size() == 0 && premiumDueList.size() == 0) {
                                // prepareListData();
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_premiumdue.setVisibility(View.GONE);
                            } else {
                                prepareListData();
                                rv_premiumdue.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                                cb_checkall.setVisibility(View.GONE);

                            }
                            //rv_premiumdue.setAdapter(new AgentPremiumDue_Fragmen.GetEventListAdapter());
                        }
                    } else {
                        prepareListData();
                        rv_premiumdue.setVisibility(View.VISIBLE);
                        ll_nothingtoshow.setVisibility(View.GONE);
                        cb_checkall.setVisibility(View.GONE);
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

        // Adding child data
        listDataHeader.add("Own Reminders");
        listDataHeader.add("Dealer Reminders");
        listDataHeader.add("Agent Reminders");
        List list1 = new ArrayList();
        list1.addAll(premiumDueList);
        List list2 = new ArrayList();
        list2.addAll(premiumDueListDealer);
        List list3 = new ArrayList();
        list3.addAll(premiumDueListRTO);


        listDataChild.put(listDataHeader.get(0), list1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), list2); // Header, Child data
        listDataChild.put(listDataHeader.get(2), list3);
        rv_premiumdue.setAdapter(new ExpandableListViewAdapter(context, listDataHeader, listDataChild));
        rv_premiumdue.expandGroup(0);
        //listDataChild.put(listDataHeader.get(1), nowShowing);
        //listDataChild.put(listDataHeader.get(2), comingSoon);
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

                                sendSMS(edt_smsmessage.getText().toString().trim(), singleReceiverID);
//                            } else if (messageType.equals("WHATSAPP")) {
//                                sendWhatsapp(edt_smsmessage.getText().toString().trim(), whatsappPic, singleReceiverID);
//                            }
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
                                sendWhatsapp(whatsappMsgSend, whatsappPicSend, singleReceiverID);
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


    private void sendSMS(String message, String singleReceiverID) {
        JsonArray clientIdJSONArray = new JsonArray();

        if (singleReceiverID.equals("")) {
            for (int i = 0; i < premiumDueList.size(); i++) {
                if (premiumDueList.get(i).isChecked()) {
                    JsonObject childObj = new JsonObject();
                    childObj.addProperty("id", premiumDueList.get(i).getClient_id());
                    clientIdJSONArray.add(childObj);
                }
            }
        } else {
            JsonObject childObj = new JsonObject();
            childObj.addProperty("id", singleReceiverID);
            clientIdJSONArray.add(childObj);
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "sendPremiumSMS");
        mainObj.add("id", clientIdJSONArray);
        mainObj.addProperty("message", message);
        mainObj.addProperty("user_id", user_id);

        if (Utilities.isInternetAvailable(context)) {
            new PremiumDueSecond_Fragment.SendAnniversarySMS().execute(mainObj.toString());
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
                        changeSessionSMSCount(obj.getString("smsCount"), obj.getString("whatsappCount"), obj.getString("maxSMSLimit"), obj.getString("maxWhatsAppLimit"));

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


    private void sendWhatsapp(String message, String whatsappPic, String singleReceiverID) {
        JsonArray clientIdJSONArray = new JsonArray();

        if (singleReceiverID.equals("")) {
            for (int i = 0; i < premiumDueList.size(); i++) {
                if (premiumDueList.get(i).isChecked()) {
                    JsonObject childObj = new JsonObject();
                    childObj.addProperty("id", premiumDueList.get(i).getId());
                    clientIdJSONArray.add(childObj);
                }
            }
        } else {
            JsonObject childObj = new JsonObject();
            childObj.addProperty("id", singleReceiverID);
            clientIdJSONArray.add(childObj);
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "sendPremiumWhtasAppMsg");
        mainObj.add("id", clientIdJSONArray);
        mainObj.addProperty("message", message);
        mainObj.addProperty("image", whatsappPic);
        mainObj.addProperty("user_id", user_id);

        if (Utilities.isInternetAvailable(context)) {
            new PremiumDueSecond_Fragment.SendAnniversaryWhatsapp().execute(mainObj.toString());
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
                        changeSessionSMSCount(obj.getString("smsCount"), obj.getString("whatsappCount"), obj.getString("maxSMSLimit"), obj.getString("maxWhatsAppLimit"));

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Whatsapp Message Sent Successfully");
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

    public void changeSessionSMSCount(String smsCount, String whatsappCount, String maxSMS, String maxWhatsapp) {
        JSONArray user_info = null;
        try {
            user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            json.put("smsCount", smsCount);
            json.put("whatsappCount", whatsappCount);
            json.put("maxSMSLimit", maxSMS);
            json.put("maxWhatsAppLimit", maxWhatsapp);
            session.updateSession(user_info.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}

