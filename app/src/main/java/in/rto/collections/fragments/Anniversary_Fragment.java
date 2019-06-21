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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.BirthdayAnnivarsaryListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;


public class Anniversary_Fragment extends Fragment {
    public LinearLayout ll_parent;
    private Context context;
    private RecyclerView rv_annivarsary;
    private FloatingActionButton fab_wish_whatsapp, fab_wish_sms, fab_wish_notification;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_nothingtoshow;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private int mYear, mMonth, mDay;
    private String user_id, date, name;
    private ArrayList<BirthdayAnnivarsaryListPojo> annivarsaryList;
    private String id = "", smsMessage = "", whatsappMessage = "", whatsappPicUrl = "", whatsappPic = "", subtype;
    private EditText dialog_edt_whatsappmessage, edt_date;
    private ImageView dialog_imv_whatsapppic;
    private CheckBox cb_checkall;
    private File rtoagentPicFolder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anniversary, container, false);
        context = getActivity();
        init(rootView);
        getSessionData();
        setDefault();
        setEventHandlers();
        return rootView;
    }

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
        rv_annivarsary = rootView.findViewById(R.id.rv_annivarsary);
        ll_parent = getActivity().findViewById(R.id.ll_parent);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        cb_checkall = rootView.findViewById(R.id.cb_checkall);
        edt_date = rootView.findViewById(R.id.edt_date);
        layoutManager = new LinearLayoutManager(context);
        rv_annivarsary.setLayoutManager(layoutManager);
        annivarsaryList = new ArrayList<>();
        rtoagentPicFolder = new File(Environment.getExternalStorageDirectory() + "/Banker/" + "Banker");
        if (!rtoagentPicFolder.exists())
            rtoagentPicFolder.mkdirs();
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
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
            new GetAnniverasryList().execute(user_id, date);
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            swipeRefreshLayout.setRefreshing(false);
            ll_nothingtoshow.setVisibility(View.VISIBLE);
            rv_annivarsary.setVisibility(View.GONE);
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
                cb_checkall.setChecked(false);
                if (Utilities.isNetworkAvailable(context)) {
                    new GetAnniverasryList().execute(user_id, date);
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    swipeRefreshLayout.setRefreshing(false);
                    ll_nothingtoshow.setVisibility(View.VISIBLE);
                    rv_annivarsary.setVisibility(View.GONE);
                }
            }
        });

        fab_wish_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray user_info = null;
                int count = 0;
                for (int i = 0; i < annivarsaryList.size(); i++) {
                    if (annivarsaryList.get(i).isChecked)
                        count = count + 1;
                }
                try {
                    user_info = new JSONArray(session.getUserDetails().get(
                            ApplicationConstants.KEY_LOGIN_INFO));
                    JSONObject json = user_info.getJSONObject(0);
                    if (Integer.parseInt(json.getString("whatsappCount")) + count <= Integer.parseInt(json.getString("maxWhatsAppLimit"))) {
                        if (Utilities.isInternetAvailable(context)) {
                            new GetAnniWhatsappSettings().execute(user_id, "", "whatsapp");
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
        fab_wish_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isInternetAvailable(context)) {
                    new GetAnniWhatsappSettings().execute(user_id, "", "notification");
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
                for (int i = 0; i < annivarsaryList.size(); i++) {
                    if (annivarsaryList.get(i).isChecked)
                        count = count + 1;
                }
                try {
                    user_info = new JSONArray(session.getUserDetails().get(
                            ApplicationConstants.KEY_LOGIN_INFO));
                    JSONObject json = user_info.getJSONObject(0);
                    if (Integer.parseInt(json.getString("smsCount")) + count <= Integer.parseInt(json.getString("maxSMSLimit"))) {
                        if (Utilities.isInternetAvailable(context)) {
                            new GetAnniSMSSettings().execute(user_id, "");
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
                            new GetAnniverasryList().execute(user_id, date);
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            swipeRefreshLayout.setRefreshing(false);
                            ll_nothingtoshow.setVisibility(View.VISIBLE);
                            rv_annivarsary.setVisibility(View.GONE);
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
                for (int i = 0; i < annivarsaryList.size(); i++) {

                    GetAnnivarsaryListAdapter.MyViewHolder myViewHolder =
                            (GetAnnivarsaryListAdapter.MyViewHolder) rv_annivarsary.findViewHolderForAdapterPosition(i);

                    if (((CheckBox) v).isChecked()) {
                        myViewHolder.cb_wish.setChecked(true);
                        annivarsaryList.get(i).setChecked(true);
                        fab_wish_whatsapp.setVisibility(View.VISIBLE);
                        fab_wish_notification.setVisibility(View.VISIBLE);
                        fab_wish_sms.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.cb_wish.setChecked(false);
                        annivarsaryList.get(i).setChecked(false);
                        fab_wish_whatsapp.setVisibility(View.GONE);
                        fab_wish_notification.setVisibility(View.GONE);
                        fab_wish_sms.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public class GetAnniverasryList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_nothingtoshow.setVisibility(View.GONE);
            rv_annivarsary.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
//            shimmer_view_container.setVisibility(View.VISIBLE);
//            shimmer_view_container.startShimmer();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAllAnniverary"));
            param.add(new ParamsPojo("user_id", params[0]));
            param.add(new ParamsPojo("date", params[1]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.BIRTHDAYANNIVERSARYAPI, param);
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
                rv_annivarsary.setVisibility(View.VISIBLE);
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    annivarsaryList = new ArrayList<>();
                    rv_annivarsary.setAdapter(new GetAnnivarsaryListAdapter());
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {

                                BirthdayAnnivarsaryListPojo anniversaryMainObj = new BirthdayAnnivarsaryListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                anniversaryMainObj.setId(jsonObj.getString("id"));
                                anniversaryMainObj.setName(jsonObj.getString("client_name"));
                                anniversaryMainObj.setMobile(jsonObj.getString("mobile"));
                                annivarsaryList.add(anniversaryMainObj);
                            }
                            if (annivarsaryList.size() == 0) {
                                ll_nothingtoshow.setVisibility(View.VISIBLE);
                                rv_annivarsary.setVisibility(View.GONE);
                                cb_checkall.setVisibility(View.GONE);
                            } else {
                                rv_annivarsary.setVisibility(View.VISIBLE);
                                ll_nothingtoshow.setVisibility(View.GONE);
                                cb_checkall.setVisibility(View.VISIBLE);
                            }
                            rv_annivarsary.setAdapter(new GetAnnivarsaryListAdapter());
                        }
                    } else {
                        ll_nothingtoshow.setVisibility(View.VISIBLE);
                        rv_annivarsary.setVisibility(View.GONE);
                        cb_checkall.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                ll_nothingtoshow.setVisibility(View.VISIBLE);
                rv_annivarsary.setVisibility(View.GONE);
                cb_checkall.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    public class GetAnnivarsaryListAdapter extends RecyclerView.Adapter<GetAnnivarsaryListAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_birthannidays, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            BirthdayAnnivarsaryListPojo annivarsaryDetails = new BirthdayAnnivarsaryListPojo();
            annivarsaryDetails = annivarsaryList.get(position);

            holder.tv_clientname.setText(annivarsaryDetails.getName());

            holder.cb_wish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.cb_wish.isChecked())
                        annivarsaryList.get(position).setChecked(true);
                    else
                        annivarsaryList.get(position).setChecked(false);

                    if (isAllValuesChecked(annivarsaryList)) {
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

                    if (isAtleastOneChecked(annivarsaryList)) {
                        fab_wish_whatsapp.setVisibility(View.VISIBLE);
                        fab_wish_notification.setVisibility(View.VISIBLE);
                        fab_wish_sms.setVisibility(View.VISIBLE);
                    } else {
                        fab_wish_whatsapp.setVisibility(View.GONE);
                        fab_wish_notification.setVisibility(View.GONE);
                        fab_wish_sms.setVisibility(View.GONE);
                    }
                }
            });

            holder.imv_sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONArray user_info = null;
                    try {
                        user_info = new JSONArray(session.getUserDetails().get(
                                ApplicationConstants.KEY_LOGIN_INFO));
                        JSONObject json = user_info.getJSONObject(0);
                        if (Integer.parseInt(json.getString("smsCount")) + 1 <= Integer.parseInt(json.getString("maxSMSLimit"))) {
                            if (Utilities.isInternetAvailable(context)) {
                                new GetAnniSMSSettings().execute(user_id, annivarsaryList.get(position).getId(), "whatsapp");
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }
                        } else {
                            Utilities.buildDialogForSmsValidation(context, 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.imv_whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONArray user_info = null;
                    try {
                        user_info = new JSONArray(session.getUserDetails().get(
                                ApplicationConstants.KEY_LOGIN_INFO));
                        JSONObject json = user_info.getJSONObject(0);
                        if (Integer.parseInt(json.getString("whatsappCount")) + 1 <= Integer.parseInt(json.getString("maxWhatsAppLimit"))) {
                            if (Utilities.isInternetAvailable(context)) {
                                new GetAnniWhatsappSettings().execute(user_id, annivarsaryList.get(position).getId(), "whatsapp");
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }
                        } else {
                            Utilities.buildDialogForSmsValidation(context, 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            holder.imv_call.setOnClickListener(new View.OnClickListener() {
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
                        alertDialogBuilder.setMessage("Are you sure you want to call " + annivarsaryList.get(position).getName() + " ?");
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                context.startActivity(new Intent(Intent.ACTION_CALL,
                                        Uri.parse("tel:" + annivarsaryList.get(position).getMobile())));
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

            holder.imv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Anniversary_Fragment.GetAnniWhatsappShare().execute(user_id, annivarsaryList.get(position).getId(), annivarsaryList.get(position).getMobile());
                }
            });

            holder.imv_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.isInternetAvailable(context)) {
                        new GetAnniWhatsappSettings().execute(user_id, annivarsaryList.get(position).getId(), "notification");
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return annivarsaryList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_clientname;
            private ImageView imv_sms, imv_whatsapp, imv_call, imv_share, imv_notification;
            private CheckBox cb_wish;

            public MyViewHolder(View view) {
                super(view);
                tv_clientname = view.findViewById(R.id.tv_clientname);
                imv_sms = view.findViewById(R.id.imv_sms);
                imv_whatsapp = view.findViewById(R.id.imv_whatsapp);
                cb_wish = view.findViewById(R.id.cb_wish);
                imv_call = view.findViewById(R.id.imv_call);
                imv_share = view.findViewById(R.id.imv_share);
                imv_notification = view.findViewById(R.id.imv_notification);
            }
        }

        private boolean isAtleastOneChecked(ArrayList<BirthdayAnnivarsaryListPojo> annivarsaryList) {
            for (int i = 0; i < annivarsaryList.size(); i++)
                if (annivarsaryList.get(i).isChecked())
                    return true;
            return false;
        }

        private boolean isAllValuesChecked(ArrayList<BirthdayAnnivarsaryListPojo> annivarsaryList) {
            for (int i = 0; i < annivarsaryList.size(); i++)
                if (!annivarsaryList.get(i).isChecked())
                    return false;
            return true;
        }
    }

    public class GetAnniSMSSettings extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;
        private String singleReceiverID = "";

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

            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAnniSMSSettings"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.SETTINGSAPI, param);
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
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                id = jsonObj.getString("id");
                                smsMessage = jsonObj.getString("message");
                            }
                        }
                    }

                    final EditText edt_smsmessage = new EditText(context);
                    float dpi = context.getResources().getDisplayMetrics().density;
                    edt_smsmessage.setText(smsMessage);
                    edt_smsmessage.setSelection(smsMessage.length());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setTitle("SMS Message");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendSMS(edt_smsmessage.getText().toString().trim(), singleReceiverID);
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSMS(String message, String singleReceiverID) {
        JsonArray clientIdJSONArray = new JsonArray();

        if (singleReceiverID.equals("")) {
            for (int i = 0; i < annivarsaryList.size(); i++) {
                if (annivarsaryList.get(i).isChecked()) {
                    JsonObject childObj = new JsonObject();
                    childObj.addProperty("id", annivarsaryList.get(i).getId());
                    clientIdJSONArray.add(childObj);
                }
            }
        } else {
            JsonObject childObj = new JsonObject();
            childObj.addProperty("id", singleReceiverID);
            clientIdJSONArray.add(childObj);
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "sendAnniversarySMS");
        mainObj.add("client_id", clientIdJSONArray);
        mainObj.addProperty("message", message);
        mainObj.addProperty("user_id", user_id);

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

    public class GetAnniWhatsappShare extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;
        private String singleReceiverID = "", clientMobile = "";

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
            clientMobile = params[2];

            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAnniWhatsAPPSettingsShare"));
            param.add(new ParamsPojo("user_id", params[0]));
            param.add(new ParamsPojo("client_id", params[1]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.SETTINGSAPI, param);
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
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                id = jsonObj.getString("id");
                                whatsappMessage = jsonObj.getString("share");
                                whatsappPicUrl = jsonObj.getString("images");
                                whatsappPic = jsonObj.getString("image");
                            }
                        }
                        if (whatsappMessage.equals("")) {
                            whatsappMessage = "Happy Birthday";
                        }


                        try {
                            PackageManager pm = getActivity().getPackageManager();
                            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                            if (!whatsappPic.equals("")) {
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());
                                File dfile = new Anniversary_Fragment.DownloadDocument().execute(whatsappPicUrl).get();
                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                sendIntent.setType("*/*");
                                sendIntent.putExtra("jid", "91" + clientMobile + "@s.whatsapp.net"); //phone number without "+" prefix
                                sendIntent.setPackage("com.whatsapp");
                                sendIntent.putExtra(Intent.EXTRA_TEXT, whatsappMessage);
                                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(dfile));
                                startActivity(sendIntent);
                            } else {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
                                shareIntent.putExtra("jid", "91" + clientMobile + "@s.whatsapp.net"); //phone number without "+" prefix
                                shareIntent.setPackage("com.whatsapp");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, whatsappMessage);
                                shareIntent.setType("text/plain");
                                startActivity(Intent.createChooser(shareIntent, "Share Message"));
                            }
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

                    } else {


                        try {
                            PackageManager pm = getActivity().getPackageManager();
                            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
                            shareIntent.putExtra("jid", "91" + clientMobile + "@s.whatsapp.net"); //phone number without "+" prefix
                            shareIntent.setPackage("com.whatsapp");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Happy Anniversary");
                            shareIntent.setType("text/plain");
                            startActivity(Intent.createChooser(shareIntent, "Share Message"));
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

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Uri getLocalBitmapUri(ImageView dialog_imv_whatsapppic) {
        Drawable drawable = dialog_imv_whatsapppic.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) dialog_imv_whatsapppic.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    public class GetAnniWhatsappSettings extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;
        private String singleReceiverID = "";

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
            subtype = params[2];
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAnniWhatsAPPSettings"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.SETTINGSAPI, param);
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
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                id = jsonObj.getString("id");
                                whatsappMessage = jsonObj.getString("message");
                                whatsappPicUrl = jsonObj.getString("images");
                                whatsappPic = jsonObj.getString("image");
                            }
                        }
                    }

                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View promptView = layoutInflater.inflate(R.layout.prompt_send_whatsappmsg, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    if (subtype.equals("notification")) {
                        alertDialogBuilder.setTitle("Notification");
                    } else {
                        alertDialogBuilder.setTitle("Whatsapp Message");
                    }
                    alertDialogBuilder.setView(promptView);

                    dialog_edt_whatsappmessage = promptView.findViewById(R.id.dialog_edt_whatsappmessage);
                    dialog_imv_whatsapppic = promptView.findViewById(R.id.dialog_imv_whatsapppic);
                    final CheckBox cb_whatsappmsg = promptView.findViewById(R.id.cb_whatsappmsg);
                    final CheckBox cb_whatsappimg = promptView.findViewById(R.id.cb_whatsappimg);

                    dialog_edt_whatsappmessage.setText(whatsappMessage);

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendWhatsapp(String message, String whatsappPic, String singleReceiverID) {
        JsonArray clientIdJSONArray = new JsonArray();

        if (singleReceiverID.equals("")) {
            for (int i = 0; i < annivarsaryList.size(); i++) {
                if (annivarsaryList.get(i).isChecked()) {
                    JsonObject childObj = new JsonObject();
                    childObj.addProperty("id", annivarsaryList.get(i).getId());
                    clientIdJSONArray.add(childObj);
                }
            }
        } else {
            JsonObject childObj = new JsonObject();
            childObj.addProperty("id", singleReceiverID);
            clientIdJSONArray.add(childObj);
        }

        JsonObject mainObj = new JsonObject();
        if (subtype.equals("notification")) {
            mainObj.addProperty("type", "sendAnniNotification");
        } else {
            mainObj.addProperty("type", "sendAnniversaryWhtasAppMsg");
        }
        mainObj.add("client_id", clientIdJSONArray);
        mainObj.addProperty("message", message);
        mainObj.addProperty("image", whatsappPic);
        mainObj.addProperty("user_id", user_id);
        mainObj.addProperty("user_Name", name);

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
                        changeSessionSMSCount(obj.getString("smsCount"), obj.getString("whatsappCount"), obj.getString("maxSMSLimit"), obj.getString("maxWhatsAppLimit"));

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

    public class DownloadDocument extends AsyncTask<String, Integer, File> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog mProgressDialog;

        @Override
        protected File doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;


            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                File file = new File(rtoagentPicFolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                return file;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


    }

}
