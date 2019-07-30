package in.rto.collections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.rto.collections.R;
import in.rto.collections.models.VASListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class ValueAddedServices_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private LinearLayout ll_parent;
    private EditText edt_type, edt_mobile, edt_description;
    private Button btn_send;

    private String user_id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_added_services);

        init();
        getSessionData();
        setDefault();
        setEventHandlers();
        setUpToolbar();
    }

    private void init() {
        context = ValueAddedServices_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        edt_type = findViewById(R.id.edt_type);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_description = findViewById(R.id.edt_description);
        btn_send = findViewById(R.id.btn_send);
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            edt_mobile.setText(json.getString("mobile"));
            user_id = json.getString("id");
            name = json.getString("name");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<VASListModel> vasList = new ArrayList<>();
                vasList.add(new VASListModel("1", "Tracking Device"));
                vasList.add(new VASListModel("2", "Fuel Card"));
                vasList.add(new VASListModel("3", "Fuel Efficiency"));
                showRequestListDialog(vasList);
            }
        });
    }

    private void setEventHandlers() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_type.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select request type");
                    return;
                }

                if (!Utilities.isMobileNo(edt_mobile)) {
                    Utilities.showSnackBar(ll_parent, "Please enter valid mobile no.");
                    return;
                }

                if (edt_description.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please enter request message");
                    return;
                }

                String messageStr = "Dear Team," + System.getProperty("line.separator") +
                        "There is an enquiry for" + edt_type.getText().toString().trim() + System.getProperty("line.separator") +
                        "Customer Name - " + name + System.getProperty("line.separator") +
                        "Customer Mobile - " + edt_mobile.getText().toString().trim() + System.getProperty("line.separator") +
                        "Customer Message - " + edt_description.getText().toString().trim() + System.getProperty("line.separator") +
                        "Please contact customer for further details" + System.getProperty("line.separator") +
                        "Thanks - MERAVAHAN Team";


                if (Utilities.isInternetAvailable(context)) {
                    JsonObject mainObj = new JsonObject();
                    mainObj.addProperty("type", "sendSms");
                    mainObj.addProperty("mobile", edt_mobile.getText().toString().trim());
                    mainObj.addProperty("message", messageStr);
                    mainObj.addProperty("user_id", "0");
                    mainObj.addProperty("client_id", user_id);
                    new sendSms().execute(mainObj.toString());
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }


            }
        });
    }

    private void showRequestListDialog(final ArrayList<VASListModel> makerList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Request Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < makerList.size(); i++) {

            arrayAdapter.add(String.valueOf(makerList.get(i).getName()));
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
                edt_type.setText(makerList.get(which).getName());
            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class sendSms extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SENDVASMSGAPI, params[0]);
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
                        String messageStr = "Dear Team," + System.getProperty("line.separator") +
                                "There is an enquiry for" + edt_type.getText().toString().trim() + System.getProperty("line.separator") +
                                "Customer Name - " + name + System.getProperty("line.separator") +
                                "Customer Mobile - " + edt_mobile.getText().toString().trim() + System.getProperty("line.separator") +
                                "Customer Message - " + edt_description.getText().toString().trim() + System.getProperty("line.separator") +
                                "Please contact customer for further details" + System.getProperty("line.separator") +
                                "Thanks - MERAVAHAN Team";


                        JsonObject mainObj1 = new JsonObject();
                        mainObj1.addProperty("type", "sendWhatsappMessage");
                        mainObj1.addProperty("mobile", edt_mobile.getText().toString().trim());
                        mainObj1.addProperty("message", messageStr);
                        mainObj1.addProperty("user_id", "0");
                        mainObj1.addProperty("client_id", user_id);
                        new sendWhatsappMessage().execute(mainObj1.toString());
                    } else {
                        Utilities.showAlertDialog(context, "Fail", "Failed to send sms message", false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class sendWhatsappMessage extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.SENDVASMSGAPI, params[0]);
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
                        builder.setMessage("Your request is submitted successfully");
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

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Value Added Services");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
