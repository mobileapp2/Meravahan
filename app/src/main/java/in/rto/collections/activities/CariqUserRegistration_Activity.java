package in.rto.collections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.fragments.Tracking_Fragment;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CariqUserRegistration_Activity extends AppCompatActivity {

    private Context context;
    private LinearLayout ll_parent;
    private EditText edt_username, edt_firstname, edt_lastname, edt_mobile, edt_email, edt_password;
    private Button btn_register;
    private String user_id, adminUsername, adminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cariquser_registration);


        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();

    }

    private void init() {
        context = CariqUserRegistration_Activity.this;
        ll_parent = findViewById(R.id.ll_parent);
        edt_username = findViewById(R.id.edt_username);
        edt_firstname = findViewById(R.id.edt_firstname);
        edt_lastname = findViewById(R.id.edt_lastname);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_register = findViewById(R.id.btn_register);
    }

    private void getSessionDetails() {
        try {
            UserSessionManager session = new UserSessionManager(context);
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
            new GetAdminDetails().execute();
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
    }

    private class GetAdminDetails extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getAdminDetails"));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.USECARIQRAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = mainObj.getJSONArray("result");
                        JSONObject object = jsonArray.getJSONObject(0);

                        adminUsername = object.getString("username");
                        adminPassword = object.getString("password");

                    } else {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setEventHandler() {
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_username.getText().toString().trim().isEmpty()) {
                    edt_username.setError("Please enter username");
                    edt_username.requestFocus();
                    return;
                }

                if (edt_firstname.getText().toString().trim().isEmpty()) {
                    edt_firstname.setError("Please enter first name");
                    edt_firstname.requestFocus();
                    return;
                }

                if (edt_lastname.getText().toString().trim().isEmpty()) {
                    edt_lastname.setError("Please enter last name");
                    edt_lastname.requestFocus();
                    return;
                }

                if (!Utilities.isMobileNo(edt_mobile)) {
                    edt_mobile.setError("Please enter valid mobile");
                    edt_mobile.requestFocus();
                    return;
                }

                if (!Utilities.isEmailValid(edt_email)) {
                    edt_email.setError("Please enter valid email");
                    edt_email.requestFocus();
                    return;
                }

                if (edt_password.getText().toString().trim().isEmpty()) {
                    edt_password.setError("Please enter password");
                    edt_password.requestFocus();
                    return;
                }


                JsonObject mainObj = new JsonObject();
//                mainObj.addProperty("firstName", "test");
//                mainObj.addProperty("lastName", "test");
//                mainObj.addProperty("password", "test123");
//                mainObj.addProperty("countryCode", "91");
//                mainObj.addProperty("timeZoneId", "1");
//                mainObj.addProperty("email", "test@email.com");
//                mainObj.addProperty("cellNumber", "9876543210");
//                mainObj.addProperty("username", "test");

                mainObj.addProperty("firstName", edt_firstname.getText().toString().trim());
                mainObj.addProperty("lastName", edt_lastname.getText().toString().trim());
                mainObj.addProperty("password", edt_password.getText().toString().trim());
                mainObj.addProperty("countryCode", "91");
                mainObj.addProperty("timeZoneId", "1");
                mainObj.addProperty("email", edt_email.getText().toString().trim());
                mainObj.addProperty("cellNumber", edt_mobile.getText().toString().trim());
                mainObj.addProperty("username", edt_username.getText().toString().trim());


                if (Utilities.isInternetAvailable(context)) {
                    new CarIqUserRegistration().execute(mainObj.toString());
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }

            }
        });
    }

    private class CarIqUserRegistration extends AsyncTask<String, Void, String> {

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

            OkHttpClient client = new OkHttpClient.Builder()
//                    .addInterceptor(new BasicAuthInterceptor(adminUsername, adminPassword))
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, params[0]);
            Request request = new Request.Builder()
                    .url(ApplicationConstants.CARIQUSERREGAPI)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String messages = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    JSONArray jsonArray = mainObj.getJSONArray("messages");
                    messages = jsonArray.getString(0);
                    String id = mainObj.getString("id");

                    if (id == null) {
                        Utilities.showSnackBar(ll_parent, messages);
                    } else {

                        JsonObject regObj = new JsonObject();
                        regObj.addProperty("type", "add");
                        regObj.addProperty("firstName", edt_firstname.getText().toString().trim());
                        regObj.addProperty("lastName", edt_lastname.getText().toString().trim());
                        regObj.addProperty("password", edt_password.getText().toString().trim());
                        regObj.addProperty("countryCode", "91");
                        regObj.addProperty("timeZoneId", "1");
                        regObj.addProperty("email", edt_email.getText().toString().trim());
                        regObj.addProperty("cellNumber", edt_mobile.getText().toString().trim());
                        regObj.addProperty("user_name", edt_username.getText().toString().trim());
                        regObj.addProperty("user_id", user_id);
                        new RegisterUser().execute(regObj.toString());

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showSnackBar(ll_parent, messages);

            }
        }
    }

    public class RegisterUser extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.USECARIQRAPI, params[0]);
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
                        builder.setMessage("User registered Successfully.");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(context, CarIqCarsList_Activity.class));
                                Tracking_Fragment.setDefault();
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
        Toolbar toolbar = findViewById(R.id.anim_toolbar);
        toolbar.setTitle("User Registration");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
