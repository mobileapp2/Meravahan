package in.rto.collections.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.fragments.Client_Fragment;
import in.rto.collections.models.ClientCodePojo;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.LanguagePojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class EditClientDetails_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private EditText edt_name, edt_alias, edt_mobile, edt_whatsapp, edt_email, edt_dob, edt_anniversary, edt_clientcode, edt_remark, edt_language;
    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private ClientMainListPojo clientDetails;
    private ArrayList<ClientCodePojo> clientCodeList;
    private ArrayList<LanguagePojo> languageList;
    private UserSessionManager session;
    private String user_id, clientCodeId = "0", clientCode = "", languageId = "0", language;
    private ImageView img_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client_details);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();


    }

    @Override
    protected void onPause() {
        super.onPause();
        // hideSoftKeyboard(EditClientDetails_Activity.this);
    }

    private void init() {
        context = EditClientDetails_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);

        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_whatsapp = findViewById(R.id.edt_whatsapp);
        edt_email = findViewById(R.id.edt_email);
        edt_dob = findViewById(R.id.edt_dob);
        edt_anniversary = findViewById(R.id.edt_anniversary);
        edt_clientcode = findViewById(R.id.edt_familycode);
        edt_remark = findViewById(R.id.edt_remark);
        edt_language = findViewById(R.id.edt_language);
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

    private void setDefaults() {
        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        mYear1 = cal.get(Calendar.YEAR);
        mMonth1 = cal.get(Calendar.MONTH);
        mDay1 = cal.get(Calendar.DAY_OF_MONTH);

        mYear2 = cal.get(Calendar.YEAR);
        mMonth2 = cal.get(Calendar.MONTH);
        mDay2 = cal.get(Calendar.DAY_OF_MONTH);

        clientDetails = (ClientMainListPojo) getIntent().getSerializableExtra("clientDetails");

        clientCodeList = new ArrayList<>();
        languageList = new ArrayList<>();

        edt_name.setText(clientDetails.getName());
        edt_alias.setText(clientDetails.getAlias());
        edt_mobile.setText(clientDetails.getMobile());
        edt_whatsapp.setText(clientDetails.getWhats_app_no());
        edt_email.setText(clientDetails.getEmail());
        edt_dob.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                clientDetails.getDob()));
        edt_anniversary.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                clientDetails.getAnniversary_date()));
        edt_clientcode.setText(clientDetails.getClient_code());
        clientCodeId = clientDetails.getClient_code_id();
        clientCode = clientDetails.getClient_code();
        languageId = clientDetails.getLanguage_id();
        edt_remark.setText(clientDetails.getRemark());
        edt_language.setText(clientDetails.getLanguage());
    }

    private void setEventHandler() {

        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_alias.setText(edt_name.getText().toString().trim());
            }
        });

        edt_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_whatsapp.setText(edt_mobile.getText().toString().trim());
            }
        });

        edt_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_dob.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))
                        );

                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                    dpd1.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }
        });

        edt_anniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_anniversary.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear1 = year;
                        mMonth1 = monthOfYear;
                        mDay1 = dayOfMonth;
                    }
                }, mYear1, mMonth1, mDay1);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                    dpd1.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }
        });

        edt_clientcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clientCodeList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetClientCodeList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    familyCodeDialog(clientCodeList);
                }
            }
        });

        edt_clientcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clientCodeId = "0";
            }

            @Override
            public void afterTextChanged(Editable s) {
                clientCodeId = "0";
            }
        });
        edt_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new EditClientDetails_Activity.GetLanguageList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    languageDialog(languageList);
                }
            }
        });

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });

    }


    public void selectDate(View view) {
        final TextView edt_familydob = (TextView) view;
        DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edt_familydob.setText(
                        changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy",
                                Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))
                );

            }
        }, mYear2, mMonth2, mDay2);
        try {
            dpd1.getDatePicker().setCalendarViewShown(false);
            dpd1.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        dpd1.show();
    }


    private void submitData() {
        if (edt_name.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Name");
            return;
        }

        if (edt_alias.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Display Name");
            return;
        }

        if (!Utilities.isMobileNo(edt_mobile)) {
            Utilities.showSnackBar(ll_parent, "Please Enter Valid Mobile Number");
            return;
        }

        if (!edt_whatsapp.getText().toString().equals("")) {
            if (!Utilities.isMobileNo(edt_whatsapp)) {
                Utilities.showSnackBar(ll_parent, "Please Enter Valid Mobile Number");
                return;
            }
        }

        if (!edt_email.getText().toString().equals("")) {
            if (!Utilities.isEmailValid(edt_email)) {
                Utilities.showSnackBar(ll_parent, "Please Enter Valid Email Address");
                return;
            }
        }
        JsonObject mainObj = new JsonObject();
        //  if (!clientCode.equals(edt_clientcode.getText().toString().trim())) {
        // clientCodeId = "0";
        // }

        mainObj.addProperty("type", "update");
        mainObj.addProperty("name", edt_name.getText().toString().trim());
        mainObj.addProperty("alias", edt_alias.getText().toString().trim());
        mainObj.addProperty("email", edt_email.getText().toString().trim());
        mainObj.addProperty("mobile", edt_mobile.getText().toString().trim());
        mainObj.addProperty("dob", changeDateFormat("dd/MM/yyyy",
                "yyyy-MM-dd",
                edt_dob.getText().toString().trim()));
        mainObj.addProperty("anniversary", changeDateFormat("dd/MM/yyyy",
                "yyyy-MM-dd",
                edt_anniversary.getText().toString().trim()));
        mainObj.addProperty("client_code_id", clientCodeId);
        // mainObj.addProperty("family_code", edt_clientcode.getText().toString().trim());
        mainObj.addProperty("id", clientDetails.getId());
        mainObj.addProperty("user_id", user_id);
        mainObj.addProperty("whatsapp", edt_whatsapp.getText().toString().trim());
        mainObj.addProperty("remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("language_id", languageId);
        Log.i("ClientDetailsJSON", mainObj.toString());

        if (Utilities.isInternetAvailable(context)) {
            new UpdateClientDetails().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }

    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        img_save = findViewById(R.id.img_save);
        mToolbar.setTitle("Edit Client Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class GetClientCodeList extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAllClientCode"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
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
                        clientCodeList = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                ClientCodePojo summary = new ClientCodePojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("code").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setCode(jsonObj.getString("code"));
                                    clientCodeList.add(summary);
                                }
                            }
                            if (clientCodeList.size() != 0) {
                                familyCodeDialog(clientCodeList);
                            } else {
                                //Utilities.showAlertDialog(context, "No Record Found", "Please enter code manually", false);
                            }
                        }
                    } else {
                        // Utilities.showAlertDialog(context, "No Record Found", "Please enter code manually", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void familyCodeDialog(final ArrayList<ClientCodePojo> clientCodeList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Client Code");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < clientCodeList.size(); i++) {

            arrayAdapter.add(String.valueOf(clientCodeList.get(i).getCode()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_clientcode.setText(clientCodeList.get(which).getCode());
                clientCodeId = clientCodeList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class UpdateClientDetails extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CLIENTAPI, params[0]);
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

                        new Client_Fragment.GetClientList().execute(user_id);
                        //new LifeInsurance_Fragment.GetLifeInsurance().execute(user_id);
                        // new GeneralInsurance_Fragment.GetGeneralInsurance().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Client Details Updated Successfully.");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
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

    public class GetLanguageList extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAllLanguages"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
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
                        languageList = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                LanguagePojo summary = new LanguagePojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("language").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setLanguage(jsonObj.getString("language"));
                                    languageList.add(summary);
                                }
                            }
                            if (languageList.size() != 0) {
                                languageDialog(languageList);
                            } else {
                                // Utilities.showAlertDialog(context, "No Record Found", "Please enter code manually", false);
                            }
                        }
                    } else {
                        //Utilities.showAlertDialog(context, "No Record Found", "Please enter code manually", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void languageDialog(final ArrayList<LanguagePojo> languageList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Language");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < languageList.size(); i++) {

            arrayAdapter.add(String.valueOf(languageList.get(i).getLanguage()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_language.setText(languageList.get(which).getLanguage());
                languageId = languageList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

}


