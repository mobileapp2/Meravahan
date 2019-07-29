package in.rto.collections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.fragments.Tracking_Fragment;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CariqLogin_Activity extends AppCompatActivity {

    private Context context;
    private ScrollView ll_parent;
    private EditText edt_username, edt_password;
    private Button btn_login;
    private ImageView imv_back;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cariq_login);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();

    }

    private void init() {
        context = CariqLogin_Activity.this;
        ll_parent = findViewById(R.id.ll_parent);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        imv_back = findViewById(R.id.imv_back);
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
    }

    private void setEventHandler() {
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_username.getText().toString().trim().isEmpty()) {
                    edt_username.setError("Please enter username");
                    edt_username.requestFocus();
                    return;
                }

                if (edt_password.getText().toString().trim().isEmpty()) {
                    edt_password.setError("Please enter password");
                    edt_password.requestFocus();
                    return;
                }


                JsonObject mainObj = new JsonObject();
                mainObj.addProperty("password", Utilities.getMd5(edt_password.getText().toString().trim()));
                mainObj.addProperty("type", "AndroidApp");
                mainObj.addProperty("version", "1.0");
                mainObj.addProperty("token", "");
                mainObj.addProperty("username", edt_username.getText().toString().trim());


                if (Utilities.isInternetAvailable(context)) {
                    new CarIqLogin().execute(mainObj.toString());
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }

            }
        });
    }

    private class CarIqLogin extends AsyncTask<String, Void, String> {

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
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, params[0]);
            Request request = new Request.Builder()
                    .url(ApplicationConstants.CARIQLOGINAPI)
                    .post(body)
                    .addHeader("content-type", "application/json")
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
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    JSONObject userDetailsObj = mainObj.getJSONObject("userDetails");

                    JsonObject regObj = new JsonObject();
                    regObj.addProperty("type", "add");
                    regObj.addProperty("firstName", userDetailsObj.getString("firstName"));
                    regObj.addProperty("lastName", userDetailsObj.getString("lastName"));
                    regObj.addProperty("password", edt_password.getText().toString().trim());
                    regObj.addProperty("countryCode", "91");
                    regObj.addProperty("timeZoneId", "1");
                    regObj.addProperty("email", userDetailsObj.getString("email"));
                    regObj.addProperty("cellNumber", userDetailsObj.getString("cellNumber"));
                    regObj.addProperty("user_name", edt_username.getText().toString().trim());
                    regObj.addProperty("user_id", user_id);
                    new RegisterUser().execute(regObj.toString());

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showSnackBar(ll_parent, "Invalid username or password");

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
}