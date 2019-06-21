package in.rto.collections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.LanguagePojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class LanguageSetting_Activity extends Activity {
    private Context context;
    private EditText edt_stateid;
    private FloatingActionButton fab_add_language;
    private LinearLayout ll_parent;
    private UserSessionManager session;
    private String user_id, id = "", languageid,language="";
    private ArrayList<LanguagePojo> languagelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }
    private void init() {
        context = LanguageSetting_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        edt_stateid = findViewById(R.id.edt_state);
        fab_add_language = findViewById(R.id.fab_add_language);
        new LanguageSetting_Activity.GetLanguageList1().execute(user_id, "2");
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
        languagelist = new ArrayList<>();
        if (Utilities.isInternetAvailable(context)) {
            new LanguageSetting_Activity.GetLanguages().execute(user_id);
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
    }
    private void setEventHandler() {
        fab_add_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText  edt_languageame = new EditText(context);
                edt_languageame.setClickable(false);
                edt_languageame.setFocusable(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_languageame.getWindowToken(), 0);
                float dpi = context.getResources().getDisplayMetrics().density;
                edt_languageame.setText(language);
                //edt_statename.setFocusable(false);
                // edt_statename.setClickable(true);
                //edt_smsmessage.setSelection(smsMessage.length());
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setTitle("Set Language");
                builder.setCancelable(false);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isInternetAvailable(context)) {
                            new LanguageSetting_Activity.AddLanguageSettings().execute(languageid, user_id);
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog alertD = builder.create();
                alertD.setView(edt_languageame, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
                edt_languageame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (languagelist.size() == 0) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new LanguageSetting_Activity.GetLanguageList().execute(user_id, "2");
                                edt_languageame.setText(language);
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }
                        } else {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                            builderSingle.setTitle("Select Language");
                            builderSingle.setCancelable(false);

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

                            for (int i = 0; i < languagelist.size(); i++) {

                                arrayAdapter.add(String.valueOf(languagelist.get(i).getLanguage()));
                            }
                            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    edt_languageame.setText(languagelist.get(which).getLanguage());
                                    languageid = languagelist.get(which).getId();

                                }
                            });
                            builderSingle.setNegativeButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });


                            AlertDialog alertD = builderSingle.create();
                            alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                            alertD.show();

                        }
                    }
                });
            }
        });
    }
    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Default Language Settings");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void languageListDialog(final ArrayList<LanguagePojo> languagelist) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Language");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < languagelist.size(); i++) {

            arrayAdapter.add(String.valueOf(languagelist.get(i).getLanguage()));
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
                languageid = languagelist.get(which).getId();
                language = languagelist.get(which).getLanguage();


            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
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
                        languagelist = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                LanguagePojo summary = new LanguagePojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("language").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setLanguage(jsonObj.getString("language"));
                                    languagelist.add(summary);
                                }
                            }
                            if (languagelist.size() != 0) {
                                languageListDialog(languagelist);
                            }
                        }
                    } else {
                        Utilities.showAlertDialog(context, "No Record Found", "Please enter family code manually", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetLanguageList1 extends AsyncTask<String, Void, String> {

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
                        languagelist = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                LanguagePojo summary = new LanguagePojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("language").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setLanguage(jsonObj.getString("language"));
                                    languagelist.add(summary);
                                }
                            }

                        }
                    } else {
                        Utilities.showAlertDialog(context, "No Record Found", "", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetLanguages extends AsyncTask<String, Void, String> {
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
            param.add(new ParamsPojo("type", "getDefaultLanguage"));
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
                                languageid = jsonObj.getString("language_id");
                                language = jsonObj.getString("language");
                                edt_stateid.setText(language);
                            }
                        }
                    } else {
                        edt_stateid.setText("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AddLanguageSettings extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "addLanguageSetting");
            obj.addProperty("language_id", languageid);
            obj.addProperty("user_id", params[1]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
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
                        if (Utilities.isInternetAvailable(context)) {
                            new LanguageSetting_Activity.GetLanguages().execute(user_id);
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
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


}
