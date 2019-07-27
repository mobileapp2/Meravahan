package in.rto.collections.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static in.rto.collections.utilities.Utilities.getMd5;

public class CariqProfileEdit_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ScrollView ll_parent;
    private ImageView imv_back;
    private EditText edt_firstname, edt_lastname, edt_mobile, edt_email;
    private Button btn_register;

    private String user_id;
    private CarIqUserDetailsModel.ResultBean cariqdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cariq_profile_edit);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
    }

    private void init() {
        context = CariqProfileEdit_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        imv_back = findViewById(R.id.imv_back);
        edt_firstname = findViewById(R.id.edt_firstname);
        edt_lastname = findViewById(R.id.edt_lastname);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        btn_register = findViewById(R.id.btn_register);
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String user_info = session.getCarIqUserDetails().get(
                    ApplicationConstants.CARIQ_LOGIN);
            CarIqUserDetailsModel pojoDetails = new Gson().fromJson(user_info, CarIqUserDetailsModel.class);

            ArrayList<CarIqUserDetailsModel.ResultBean> myCarList = new ArrayList<>();
            myCarList = pojoDetails.getResult();
            cariqdetails = myCarList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        edt_firstname.setText(cariqdetails.getFirst_name());
        edt_lastname.setText(cariqdetails.getLast_name());
        edt_mobile.setText(cariqdetails.getCell_number());
        edt_email.setText(cariqdetails.getEmail());
    }

    private void setEventHandler() {
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


                JsonObject mainObj = new JsonObject();
//                mainObj.addProperty("firstName", "test");
//                mainObj.addProperty("lastName", "test");
//                mainObj.addProperty("password", "test123");
//                mainObj.addProperty("countryCode", "91");
//                mainObj.addProperty("timeZoneId", "1");
//                mainObj.addProperty("email", "test@email.com");
//                mainObj.addProperty("cellNumber", "9876543210");
//                mainObj.addProperty("username", "test");

                mainObj.addProperty("lastName", edt_lastname.getText().toString().trim());
                mainObj.addProperty("address2", "");
                mainObj.addProperty("address1", "");
                mainObj.addProperty("stateId", 0);
                mainObj.addProperty("cityId", 0);
                mainObj.addProperty("countryId", 0);
                mainObj.addProperty("sos2", "");
                mainObj.addProperty("cellNumber", edt_mobile.getText().toString().trim());
                mainObj.addProperty("sos1", "");
                mainObj.addProperty("firstName", edt_firstname.getText().toString().trim());
                mainObj.addProperty("countryCode", "91");
                mainObj.addProperty("timeZoneId", "1");
                mainObj.addProperty("email", edt_email.getText().toString().trim());

                if (Utilities.isInternetAvailable(context)) {
                    new CarIqProfileUpdate().execute(mainObj.toString());
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        });
    }

    private class CarIqProfileUpdate extends AsyncTask<String, Void, String> {

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
                    .url(ApplicationConstants.CARIQPOFILEUPDATEAPI)
                    .put(body)
                    .addHeader("content-type", "application/json")
                    .header("Authorization", Credentials.basic(cariqdetails.getUser_name(), getMd5(cariqdetails.getPassword())))
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
//                    JSONObject mainObj = new JSONObject(result);
//                    JSONArray jsonArray = mainObj.getJSONArray("messages");
//                    messages = jsonArray.getString(0);
//                    String id = mainObj.getString("id");
//
//                    if (id == null) {
//                        Utilities.showSnackBar(ll_parent, messages);
//                    } else {

//                        JsonObject regObj = new JsonObject();
//                        regObj.addProperty("type", "add");
//                        regObj.addProperty("firstName", edt_firstname.getText().toString().trim());
//                        regObj.addProperty("lastName", edt_lastname.getText().toString().trim());
//                        regObj.addProperty("password", edt_password.getText().toString().trim());
//                        regObj.addProperty("countryCode", "91");
//                        regObj.addProperty("timeZoneId", "1");
//                        regObj.addProperty("email", edt_email.getText().toString().trim());
//                        regObj.addProperty("cellNumber", edt_mobile.getText().toString().trim());
//                        regObj.addProperty("user_name", edt_username.getText().toString().trim());
//                        regObj.addProperty("user_id", user_id);
//                        new RegisterUser().execute(regObj.toString());

//                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showSnackBar(ll_parent, messages);

            }
        }
    }

}
