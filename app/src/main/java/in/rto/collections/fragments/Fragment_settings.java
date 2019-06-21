package in.rto.collections.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import in.rto.collections.R;
import in.rto.collections.activities.LanguageSetting_Activity;
import in.rto.collections.activities.SMSAnniversarySettings_Activity;
import in.rto.collections.activities.SMSBirthdaySettings_Activity;
import in.rto.collections.activities.Signature_Settings;
import in.rto.collections.activities.StateSetting_Activity;
import in.rto.collections.activities.WhatsappAnniversarySettings_Activity;
import in.rto.collections.activities.WhatsappBirthdaySettings_Activity;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;


public class Fragment_settings extends Fragment {
    private Context context;
    private UserSessionManager session;
    private String user_id, role;
    private CardView birthtext, annitext, premiumdue, languageCard;
    LinearLayout openBirthdayWhatsappSettings, openBirthdaySMSSettings, openAnniversaryWhatsappSettings, openAnniversarySMSSettings,
            openPremiumDueMessageSettings, openSignatureSettings, openAnniversaryNotificationSettings, openBirthdayNotificationSettings,
            openLanguageSettings;
    LinearLayout ll_parent;
    private SwitchCompat switchBirthdayWhatsappButton, switchBirthdaySMSButton, switchAnnniWhatsappButton, switchAnnniSMSButton,
            switchPremiumDueButton, switchPremiumDueSMSButton, switchPremiumDueNotificationButton, switchBirthdayNotificationButton, switchAnnniNotificationButton;
    int birthSMS, birthWhatsapp, birthNotification, anniSMS, anniWhatsapp, anniNotification, due, duesms, duenotification;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getActivity();
        session = new UserSessionManager(context);

        openBirthdayWhatsappSettings = rootView.findViewById(R.id.openBirthdayWhatsappSettings);
        openBirthdayWhatsappSettings.setOnClickListener(mButtonClickListener);

        //openBirthdaySMSSettings  = rootView.findViewById(R.id.openBirthdaySMSSettings);
        // openBirthdaySMSSettings.setOnClickListener(mButtonClickListener1);

        // openBirthdayNotificationSettings = rootView.findViewById(R.id.openBirthdayNotificationSettings);
        // openBirthdayNotificationSettings.setOnClickListener(mButtonClickListener7);

        openAnniversaryWhatsappSettings = rootView.findViewById(R.id.openAnniversaryWhatsappSettings);
        openAnniversaryWhatsappSettings.setOnClickListener(mButtonClickListener2);

        //openAnniversarySMSSettings  = rootView.findViewById(R.id.openAnniversarySMSSettings);
        //openAnniversarySMSSettings.setOnClickListener(mButtonClickListener3);

        openPremiumDueMessageSettings = rootView.findViewById(R.id.openPremiumDueMessageSettings);
        openPremiumDueMessageSettings.setOnClickListener(mButtonClickListener4);

        openSignatureSettings = rootView.findViewById(R.id.openSignatureSettings);
        openSignatureSettings.setOnClickListener(mButtonClickListener5);

        openLanguageSettings = rootView.findViewById(R.id.openLanguageSettings);
        openLanguageSettings.setOnClickListener(getmButtonClickListenerLanguage);

        birthtext = rootView.findViewById(R.id.birthtext);
        annitext = rootView.findViewById(R.id.annitext);
        premiumdue = rootView.findViewById(R.id.premiumdue);
        languageCard = rootView.findViewById(R.id.languageCard);
        getSessionData();
        if (role.equals("3")) {
            birthtext.setVisibility(View.GONE);
            annitext.setVisibility(View.GONE);
            premiumdue.setVisibility(View.GONE);
            languageCard.setVisibility(View.GONE);

        }
        ll_parent = rootView.findViewById(R.id.ll_parent);
        switchBirthdayWhatsappButton = rootView.findViewById(R.id.switchBirthdayWhatsappButton);
        switchBirthdayNotificationButton = rootView.findViewById(R.id.switchBirthdayNotificationButton);
        switchBirthdaySMSButton = rootView.findViewById(R.id.switchBirthdaySMSButton);
        switchAnnniWhatsappButton = rootView.findViewById(R.id.switchAnnniWhatsappButton);
        switchAnnniNotificationButton = rootView.findViewById(R.id.switchAnnniNotificationButton);
        switchAnnniSMSButton = rootView.findViewById(R.id.switchAnnniSMSButton);
        switchPremiumDueButton = rootView.findViewById(R.id.switchPremiumDueButton);
        switchPremiumDueSMSButton = rootView.findViewById(R.id.switchPremiumDueSMSButton);
        switchPremiumDueNotificationButton = rootView.findViewById(R.id.switchPremiumDueNotificationButton);
        switchAnnniNotificationButton = rootView.findViewById(R.id.switchAnnniNotificationButton);
        switchBirthdayNotificationButton = rootView.findViewById(R.id.switchBirthdayNotificationButton);
        //setUpToolbar(rootView);

        new Fragment_settings.getSettings().execute();
        setEventHandler();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new getSettings().execute();

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


    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            openBirthdayWhatsappSettings(v);
        }
    };

    private View.OnClickListener mButtonClickListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            openBirthdaySMSSettings(v);
        }
    };

    private View.OnClickListener mButtonClickListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            openAnniversaryWhatsappSettings(v);
        }
    };

    private View.OnClickListener mButtonClickListener7 = new View.OnClickListener() {
        public void onClick(View v) {
            openBirthdayNotificationSettings(v);
        }
    };

    private View.OnClickListener mButtonClickListener3 = new View.OnClickListener() {
        public void onClick(View v) {
            openAnniversarySMSSettings(v);
        }
    };
    private View.OnClickListener mButtonClickListener6 = new View.OnClickListener() {
        public void onClick(View v) {
            openAnniversaryNotificationSettings(v);
        }
    };
    private View.OnClickListener mButtonClickListener4 = new View.OnClickListener() {
        public void onClick(View v) {
            openPremiumDueMessageSettings(v);
        }
    };

    private View.OnClickListener getmButtonClickListenerLanguage = new View.OnClickListener() {
        public void onClick(View v) {
            openLanguageSettings(v);
        }
    };

    private View.OnClickListener mButtonClickListener5 = new View.OnClickListener() {
        public void onClick(View v) {
            openSignatureSettings(v);
        }
    };

    public void openBirthdayWhatsappSettings(View view) {
        startActivity(new Intent(context, WhatsappBirthdaySettings_Activity.class));
    }

    public void openBirthdaySMSSettings(View view) {
        startActivity(new Intent(context, SMSBirthdaySettings_Activity.class));
    }

    public void openBirthdayNotificationSettings(View view) {
        startActivity(new Intent(context, SMSBirthdaySettings_Activity.class));
    }

    public void openAnniversaryWhatsappSettings(View view) {
        startActivity(new Intent(context, WhatsappAnniversarySettings_Activity.class));
    }

    public void openAnniversarySMSSettings(View view) {
        startActivity(new Intent(context, SMSAnniversarySettings_Activity.class));
    }

    public void openAnniversaryNotificationSettings(View view) {
        startActivity(new Intent(context, SMSAnniversarySettings_Activity.class));
    }

    public void openPremiumDueMessageSettings(View view) {
        startActivity(new Intent(context, StateSetting_Activity.class));
    }

    public void openLanguageSettings(View view) {
        startActivity(new Intent(context, LanguageSetting_Activity.class));
    }

    public void openSignatureSettings(View view) {
        startActivity(new Intent(context, Signature_Settings.class));
    }

    private void setUpToolbar(View rootView) {
        Toolbar mToolbar = rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("Settings");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish();
            }
        });
    }

    private void setEventHandler() {
        switchBirthdayWhatsappButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Utilities.showSnackBar(ll_parent, "Birthday WhatsApp clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                    if (birthWhatsapp == 2) {
                        checked = "0";
                        switchBirthdayWhatsappButton.setChecked(false);
                        showAlertDialog("birthWhats");

                    }
                } else {
                    checked = "0";
                }
                new Fragment_settings.updateBirthdayWhatsApp().execute(checked);
            }
        });
        switchBirthdayNotificationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Utilities.showSnackBar(ll_parent, "Birthday WhatsApp clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                    if (birthNotification == 2) {
                        checked = "0";
                        switchBirthdayNotificationButton.setChecked(false);
                        showAlertDialog("birthNotification");

                    }
                } else {
                    checked = "0";
                }
                new Fragment_settings.updateBirthdayNotification().execute(checked);
            }
        });
        switchBirthdaySMSButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Utilities.showSnackBar(ll_parent, "Birthday SMS clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                    if (birthSMS == 2) {
                        checked = "0";
                        switchBirthdaySMSButton.setChecked(false);
                        showAlertDialog("birthSMS");
                    }
                } else {
                    checked = "0";
                }

                new Fragment_settings.updateBirthdaySMS().execute(checked);
            }
        });


        switchAnnniWhatsappButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Utilities.showSnackBar(ll_parent, "Anniversary Whatsapp clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                    if (anniWhatsapp == 2) {
                        checked = "0";
                        switchAnnniWhatsappButton.setChecked(false);
                        showAlertDialog("anniWhats");
                    }
                } else {
                    checked = "0";
                }
                new Fragment_settings.updateAnniversaryWhatsApp().execute(checked);
            }
        });
        switchAnnniNotificationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Utilities.showSnackBar(ll_parent, "Anniversary Whatsapp clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                    if (anniNotification == 2) {
                        checked = "0";
                        switchAnnniNotificationButton.setChecked(false);
                        showAlertDialog("anniNotification");
                    }
                } else {
                    checked = "0";
                }
                new Fragment_settings.updateAnniversaryNotification().execute(checked);
            }
        });

        switchAnnniSMSButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Utilities.showSnackBar(ll_parent, "Anniversary SMS clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                    if (anniSMS == 2) {
                        checked = "0";
                        switchAnnniSMSButton.setChecked(false);
                        showAlertDialog("anniSMS");

                    }
                } else {
                    checked = "0";
                }
                new Fragment_settings.updateAnniversarySMS().execute(checked);
            }
        });

        switchPremiumDueButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Utilities.showSnackBar(ll_parent, "Anniversary SMS clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                } else {
                    checked = "0";
                }
                new Fragment_settings.updatePremiumDue().execute(checked);
            }
        });

        switchPremiumDueSMSButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Utilities.showSnackBar(ll_parent, "Anniversary SMS clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                } else {
                    checked = "0";
                }
                new Fragment_settings.updatePremiumDueSMS().execute(checked);
            }
        });
        switchPremiumDueNotificationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Utilities.showSnackBar(ll_parent, "Anniversary SMS clicked" + isChecked);
                String checked;
                if (isChecked) {
                    checked = "1";
                } else {
                    checked = "0";
                }
                new Fragment_settings.updatePremiumDueNotification().execute(checked);
            }
        });

    }

    public void showAlertDialog(final String type) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("There are no default settings to send automated messages. Please add settings !");
        alertDialog.setIcon(R.drawable.ic_alert_red_24dp);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                switch (type) {
                    case "birthWhats":
                        startActivity(new Intent(context, WhatsappBirthdaySettings_Activity.class));
                        break;
                    case "birthSMS":
                        startActivity(new Intent(context, SMSBirthdaySettings_Activity.class));
                        break;
                    case "anniWhats":
                        startActivity(new Intent(context, WhatsappAnniversarySettings_Activity.class));
                        break;
                    case "anniSMS":
                        startActivity(new Intent(context, SMSAnniversarySettings_Activity.class));
                        break;

                }
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertD = alertDialog.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class updateBirthdayWhatsApp extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedBirthWhats");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                // pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

                        } else {
                            // Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
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

    public class updateBirthdayNotification extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedBirthNotification");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                // pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

                        } else {
                            // Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
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

    public class updateBirthdaySMS extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedBirthSMS");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                //   pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

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

    public class updateAnniversaryWhatsApp extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedAnniWhats");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                //pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

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

    public class updateAnniversarySMS extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedAnniSMS");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                //  pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

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

    public class updateAnniversaryNotification extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedAnnihNotification");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                //  pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

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

    public class updatePremiumDue extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedDue");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                //  pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

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

    public class updatePremiumDueSMS extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedDueSMS");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                //  pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

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

    public class updatePremiumDueNotification extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addauomatedDueNotification");
            obj.addProperty("user_id", user_id);
            obj.addProperty("setting", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                //  pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        if (Utilities.isInternetAvailable(context)) {

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


    public class getSettings extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getAutomatedSettings");
            obj.addProperty("user_id", user_id);

            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SETTINGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                // pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject data = mainObj.getJSONObject("result");
                        if (data.getString("birthsms").equals("1")) {
                            birthSMS = 0;
                            switchBirthdaySMSButton.setChecked(true);
                        } else if (data.getString("birthsms").equals("2")) {
                            birthSMS = 2;
                        } else {
                            birthSMS = 0;
                            switchBirthdaySMSButton.setChecked(false);
                        }


                        if (data.getString("birthapp").equals("1")) {
                            birthWhatsapp = 0;
                            switchBirthdayWhatsappButton.setChecked(true);
                        } else if (data.getString("birthapp").equals("2")) {
                            birthWhatsapp = 2;
                        } else {
                            birthWhatsapp = 0;
                            switchBirthdayWhatsappButton.setChecked(false);
                        }

                        if (data.getString("birthnotification").equals("1")) {
                            birthNotification = 0;
                            switchBirthdayNotificationButton.setChecked(true);
                        } else if (data.getString("birthnotification").equals("2")) {
                            birthNotification = 2;
                        } else {
                            birthNotification = 0;
                            switchBirthdayNotificationButton.setChecked(false);
                        }


                        if (data.getString("annisms").equals("1")) {
                            anniSMS = 0;
                            switchAnnniSMSButton.setChecked(true);
                        } else if (data.getString("annisms").equals("2")) {
                            anniSMS = 2;
                        } else {
                            anniSMS = 0;
                            switchAnnniSMSButton.setChecked(false);
                        }

                        if (data.getString("anniapp").equals("1")) {
                            anniWhatsapp = 0;
                            switchAnnniWhatsappButton.setChecked(true);
                        } else if (data.getString("anniapp").equals("2")) {
                            anniWhatsapp = 2;
                        } else {
                            anniWhatsapp = 0;
                            switchAnnniWhatsappButton.setChecked(false);
                        }

                        if (data.getString("anninotification").equals("1")) {
                            anniNotification = 0;
                            switchAnnniNotificationButton.setChecked(true);
                        } else if (data.getString("anninotification").equals("2")) {
                            anniNotification = 2;
                        } else {
                            anniNotification = 0;
                            switchAnnniNotificationButton.setChecked(false);
                        }

                        if (data.getString("due").equals("1")) {
                            due = 0;
                            switchPremiumDueButton.setChecked(true);
                        } else if (data.getString("due").equals("2")) {
                            due = 2;
                        } else {
                            due = 0;
                            switchPremiumDueButton.setChecked(false);
                        }


                        if (data.getString("duesms").equals("1")) {
                            due = 0;
                            switchPremiumDueSMSButton.setChecked(true);
                        } else if (data.getString("duesms").equals("2")) {
                            due = 2;
                        } else {
                            due = 0;
                            switchPremiumDueSMSButton.setChecked(false);
                        }

                        if (data.getString("duenotification").equals("1")) {
                            due = 0;
                            switchPremiumDueNotificationButton.setChecked(true);
                        } else if (data.getString("duenotification").equals("2")) {
                            due = 2;
                        } else {
                            due = 0;
                            switchPremiumDueNotificationButton.setChecked(false);
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
