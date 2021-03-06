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
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.EventListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class DealerPremiumDue_Fragment extends Fragment {
    public LinearLayout ll_parent;
    private Context context;
    private RecyclerView rv_premiumdue;
    private FloatingActionButton fab_wish_whatsapp, fab_wish_sms;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_nothingtoshow;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private int mYear, mMonth, mDay;
    private String user_id, date, role;
    private ArrayList<EventListPojo> premiumDueList;
    private String id = "", message = "", whatsappPicUrl = "", whatsappPic = "";
    private EditText edt_date, dialog_edt_whatsappmessage;
    private CheckBox cb_checkall;
    private ImageView dialog_imv_whatsapppic;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dealer_premium_due, container, false);
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
        ll_parent = getActivity().findViewById(R.id.ll_parent);
//        shimmer_view_container = rootView.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        cb_checkall = rootView.findViewById(R.id.cb_checkall);
        edt_date = rootView.findViewById(R.id.edt_date);
        layoutManager = new LinearLayoutManager(context);
        rv_premiumdue.setLayoutManager(layoutManager);

        premiumDueList = new ArrayList<>();
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
            new DealerPremiumDue_Fragment.GetEventList().execute(user_id, date);
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
                    new DealerPremiumDue_Fragment.GetEventList().execute(user_id, date);
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
                if (Utilities.isInternetAvailable(context)) {
                    new DealerPremiumDue_Fragment.GetPremiumMessage().execute(user_id, "", "WHATSAPP");
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        });

        fab_wish_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isInternetAvailable(context)) {
                    new DealerPremiumDue_Fragment.GetPremiumMessage().execute(user_id, "", "SMS");
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
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
                            new DealerPremiumDue_Fragment.GetEventList().execute(user_id, date);
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

                    DealerPremiumDue_Fragment.GetEventListAdapter.MyViewHolder myViewHolder =
                            (DealerPremiumDue_Fragment.GetEventListAdapter.MyViewHolder) rv_premiumdue.findViewHolderForAdapterPosition(i);

                    if (((CheckBox) v).isChecked()) {
                        // myViewHolder.cb_wish.setChecked(true);
                        premiumDueList.get(i).setChecked(true);
                        fab_wish_whatsapp.setVisibility(View.VISIBLE);
                        fab_wish_sms.setVisibility(View.VISIBLE);
                    } else {
                        //myViewHolder.cb_wish.setChecked(false);
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
                    premiumDueList = new ArrayList<>();
                    rv_premiumdue.setAdapter(new DealerPremiumDue_Fragment.GetEventListAdapter());
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
                                premiumDueList.add(eventMainObj);
                            }
                            if (premiumDueList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_premiumdue.setVisibility(View.GONE);
                                cb_checkall.setVisibility(View.GONE);
                            } else {
                                rv_premiumdue.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                                cb_checkall.setVisibility(View.VISIBLE);
                            }
                            rv_premiumdue.setAdapter(new DealerPremiumDue_Fragment.GetEventListAdapter());
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

    public class GetEventListAdapter extends RecyclerView.Adapter<DealerPremiumDue_Fragment.GetEventListAdapter.MyViewHolder> {

        @Override
        public DealerPremiumDue_Fragment.GetEventListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_dealer_agent, parent, false);
            DealerPremiumDue_Fragment.GetEventListAdapter.MyViewHolder myViewHolder = new DealerPremiumDue_Fragment.GetEventListAdapter.MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final DealerPremiumDue_Fragment.GetEventListAdapter.MyViewHolder holder, final int position) {
            EventListPojo annivarsaryDetails = new EventListPojo();
            annivarsaryDetails = premiumDueList.get(position);
            holder.tv_clientname.setText(annivarsaryDetails.getClient_name() + " | " + annivarsaryDetails.getDescription());

        }

        @Override
        public int getItemCount() {
            return premiumDueList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_clientname;

            public MyViewHolder(View view) {
                super(view);
                tv_clientname = view.findViewById(R.id.tv_clientname);

            }
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
            new DealerPremiumDue_Fragment.SendAnniversarySMS().execute(mainObj.toString());
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
            new DealerPremiumDue_Fragment.SendAnniversaryWhatsapp().execute(mainObj.toString());
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


}
