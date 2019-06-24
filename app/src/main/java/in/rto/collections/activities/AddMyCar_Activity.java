package in.rto.collections.activities;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import in.rto.collections.R;
import in.rto.collections.models.CarIqUserDetailsModel;
import in.rto.collections.models.CarMakerListModel;
import in.rto.collections.models.CarModelAndVariantListModel;
import in.rto.collections.models.CustomerPojo;
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

import static in.rto.collections.utilities.Utilities.changeDateFormat;
import static in.rto.collections.utilities.Utilities.getMd5;

public class AddMyCar_Activity extends AppCompatActivity {

    private Context context;
    private ProgressDialog pd;
    private LinearLayout ll_parent;
    private EditText edt_name, edt_carplate, edt_maker, edt_model, edt_variant, edt_date, edt_kmcovered, edt_deviceid;
    private Button btn_register;

    private CarIqUserDetailsModel.ResultBean cariqdetails;
    private String user_id, makerId, purchaseDate, variant, fuelType;
    private ArrayList<CustomerPojo> regNumList;
    private ArrayList<CarMakerListModel> makerList;
    private ArrayList<CarModelAndVariantListModel> modelAndVariantList;

    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mycar);


        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddMyCar_Activity.this;
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        ll_parent = findViewById(R.id.ll_parent);
        edt_name = findViewById(R.id.edt_name);
        edt_carplate = findViewById(R.id.edt_carplate);
        edt_maker = findViewById(R.id.edt_maker);
        edt_model = findViewById(R.id.edt_model);
        edt_variant = findViewById(R.id.edt_variant);
        edt_date = findViewById(R.id.edt_date);
        edt_kmcovered = findViewById(R.id.edt_kmcovered);
        edt_deviceid = findViewById(R.id.edt_deviceid);
        btn_register = findViewById(R.id.btn_register);

        regNumList = new ArrayList<>();
        makerList = new ArrayList<>();
        modelAndVariantList = new ArrayList<>();
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
        cariqdetails = (CarIqUserDetailsModel.ResultBean) getIntent().getSerializableExtra("cariqdetails");


        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
    }

    private void setEventHandler() {
        edt_carplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regNumList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetRegNumList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    regNumListDialog(regNumList);
                }
            }
        });

        edt_maker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_carplate.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select registration number");
                    return;
                }

                if (makerList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetMakerList().execute();
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    makerListDialog(makerList);
                }
            }
        });

        edt_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_carplate.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select registration number");
                    return;
                }

                if (edt_maker.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select maker");
                    return;
                }


                if (Utilities.isNetworkAvailable(context)) {
                    new GetModelAndVariantList().execute();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        });

        edt_variant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_carplate.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select registration number");
                    return;
                }

                if (edt_maker.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select maker");
                    return;
                }

                if (edt_model.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select model");
                    return;
                }


                ArrayList<CarModelAndVariantListModel> variantList = new ArrayList<>();

                for (int i = 0; i < modelAndVariantList.size(); i++) {
                    if (modelAndVariantList.get(i).getCarModel().equals(edt_model.getText().toString().trim())) {
                        variantList.add(modelAndVariantList.get(i));
                    }
                }

                variantListDialog(variantList);
            }
        });

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        purchaseDate = Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year);
                        edt_date.setText(
                                changeDateFormat("yyyy-MM-dd", "dd/MM/yyyy", Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))
                        );

                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
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

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_name.getText().toString().trim().isEmpty()) {
                    edt_name.setError("Please enter nick name");
                    edt_name.requestFocus();
                    return;
                }

                if (edt_carplate.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select registration number");
                    return;
                }

                if (edt_maker.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select maker");
                    return;
                }

                if (edt_model.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select model");
                    return;
                }

                if (edt_variant.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select variant");
                    return;
                }

                if (edt_date.getText().toString().trim().isEmpty()) {
                    Utilities.showSnackBar(ll_parent, "Please select purchase date");
                    return;
                }

                if (edt_kmcovered.getText().toString().trim().isEmpty()) {
                    edt_kmcovered.setError("Please enter covered distance");
                    edt_kmcovered.requestFocus();
                    return;
                }

                if (edt_deviceid.getText().toString().trim().isEmpty()) {
                    edt_deviceid.setError("Please enter device id");
                    edt_deviceid.requestFocus();
                    return;
                }


                JsonObject mainObj = new JsonObject();

                mainObj.addProperty("mfgYear", String.valueOf(mYear));
                mainObj.addProperty("purchaseDate", purchaseDate);
                mainObj.addProperty("kmsCovered", edt_kmcovered.getText().toString().trim());
                mainObj.addProperty("fuelType", fuelType);
                mainObj.addProperty("registrationNumber", edt_carplate.getText().toString().trim());
                mainObj.addProperty("variant", variant);
                mainObj.addProperty("model", edt_model.getText().toString().trim());
                mainObj.addProperty("vin", "dummyvin");
                mainObj.addProperty("deviceId", edt_deviceid.getText().toString().trim());
                mainObj.addProperty("make", edt_maker.getText().toString().trim());

                if (Utilities.isInternetAvailable(context)) {
                    new CarRegistration().execute(mainObj.toString());
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }


            }
        });
    }

    private class GetRegNumList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getcustomer"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.CUSTOMERAPI, param);
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
                    regNumList = new ArrayList<CustomerPojo>();
                    if (type.equalsIgnoreCase("success")) {
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                CustomerPojo customerMainObj = new CustomerPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                customerMainObj.setVehicle_no(jsonObj.getString("Vehicle_No"));
                                regNumList.add(customerMainObj);
                            }

                            if (regNumList.size() != 0) {
                                regNumListDialog(regNumList);
                            } else {
                                Utilities.showAlertDialog(context, "Alert", "Please add vehicle", false);
                            }

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, "Alert", "Please add vehicle", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void regNumListDialog(final ArrayList<CustomerPojo> typelist) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Registration Number");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < typelist.size(); i++) {

            arrayAdapter.add(String.valueOf(typelist.get(i).getVehicle_no()));
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
                edt_carplate.setText(typelist.get(which).getVehicle_no());
                edt_maker.setText("");
                edt_model.setText("");
                edt_variant.setText("");

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    private class GetMakerList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            Request request = new Request.Builder()
                    .url(ApplicationConstants.MAKERLISTAPI)
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
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        CarMakerListModel carMakerListModel = new CarMakerListModel();
                        carMakerListModel.setCarMake(object.getString("carMake"));
                        carMakerListModel.setMakeId(object.getString("makeId"));
                        makerList.add(carMakerListModel);
                    }


                    if (makerList.size() != 0) {
                        makerListDialog(makerList);
                    } else {
                        Utilities.showAlertDialog(context, "Alert", "Maker list not available", false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Alert", "Maker list not available", false);

            }
        }
    }

    private void makerListDialog(final ArrayList<CarMakerListModel> makerList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Maker");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < makerList.size(); i++) {

            arrayAdapter.add(String.valueOf(makerList.get(i).getCarMake()));
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
                edt_maker.setText(makerList.get(which).getCarMake());
                makerId = makerList.get(which).getMakeId();
                edt_model.setText("");
                edt_variant.setText("");

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    private class GetModelAndVariantList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            Request request = new Request.Builder()
                    .url(ApplicationConstants.MODELANDVARIANTLISTAPI + makerId)
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
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONArray jsonArray = new JSONArray(result);
                    modelAndVariantList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        CarModelAndVariantListModel carMakerListModel = new CarModelAndVariantListModel();
                        carMakerListModel.setCarModel(object.getString("carModel"));
                        carMakerListModel.setFuelType(object.getString("fuelType"));
                        carMakerListModel.setVariant(object.getString("variant"));
                        modelAndVariantList.add(carMakerListModel);
                    }


                    if (modelAndVariantList.size() != 0) {

                        ArrayList<CarModelAndVariantListModel> modelList = new ArrayList<>();
                        Set<CarModelAndVariantListModel> primesWithoutDuplicates = new LinkedHashSet<CarModelAndVariantListModel>(modelAndVariantList);
                        modelList.addAll(primesWithoutDuplicates);
                        modelListDialog(modelList);

                    } else {
                        Utilities.showAlertDialog(context, "Alert", "Maker list not available", false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Alert", "Maker list not available", false);

            }
        }
    }

    private void modelListDialog(final ArrayList<CarModelAndVariantListModel> makerList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Maker");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < makerList.size(); i++) {

            arrayAdapter.add(String.valueOf(makerList.get(i).getCarModel()));
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
                edt_model.setText(makerList.get(which).getCarModel());
                edt_variant.setText("");

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    private void variantListDialog(final ArrayList<CarModelAndVariantListModel> makerList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Variant");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < makerList.size(); i++) {

            arrayAdapter.add(makerList.get(i).getVariant() + " (" + makerList.get(i).getFuelType() + ")");
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
                edt_variant.setText(makerList.get(which).getVariant() + " (" + makerList.get(which).getFuelType() + ")");
                variant = makerList.get(which).getVariant();
                fuelType = makerList.get(which).getFuelType();
            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }


    private class CarRegistration extends AsyncTask<String, Void, String> {

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
                    .url(ApplicationConstants.CARREGAPI)
                    .post(body)
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
                    JSONObject mainObj = new JSONObject(result);
                    JSONArray jsonArray = mainObj.getJSONArray("messages");
                    messages = jsonArray.getString(0);
                    String id = mainObj.getString("id");

                    if (id == null) {
                        Utilities.showSnackBar(ll_parent, messages);
                    } else {

                        JsonObject regObj = new JsonObject();

                        regObj.addProperty("type", "type");
                        regObj.addProperty("mfgYear", String.valueOf(mYear));
                        regObj.addProperty("purchaseDate", purchaseDate);
                        regObj.addProperty("kmsCovered", edt_kmcovered.getText().toString().trim());
                        regObj.addProperty("fuelType", fuelType);
                        regObj.addProperty("registrationNumber", edt_carplate.getText().toString().trim());
                        regObj.addProperty("variant", variant);
                        regObj.addProperty("model", edt_model.getText().toString().trim());
                        regObj.addProperty("vin", "dummyvin");
                        regObj.addProperty("deviceId", edt_deviceid.getText().toString().trim());
                        regObj.addProperty("make", edt_maker.getText().toString().trim());
                        regObj.addProperty("user_id", user_id);
                        regObj.addProperty("vehicleDetailsId", id);

                        new RegisterCar().execute(regObj.toString());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showSnackBar(ll_parent, messages);

            }
        }
    }


    public class RegisterCar extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.VEHICLETRACKINGAPI, params[0]);
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
                        builder.setMessage("Car registered Successfully.");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(context, MyCarsList_Activity.class));
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
        toolbar.setTitle("Car Registration");
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
