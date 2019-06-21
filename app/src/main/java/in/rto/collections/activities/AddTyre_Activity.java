package in.rto.collections.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import in.rto.collections.R;
import in.rto.collections.fragments.Fragment_tyre;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.models.TyreDetailsPojo;
import in.rto.collections.models.VehicleDealerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.MultipartUtility;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class AddTyre_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_state, edt_vehicleno, edt_clientname,
            edt_description, edt_type, edt_replacementdate,
            edt_remouning, edt_purchasedate, edt_remark, edt_tyreno;
    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private int mYear3, mMonth3, mDay3;
    private EditText edt_selectdocuments = null;
    private ImageView img_save;
    private ArrayList<ClientMainListPojo> clientList;
    private List<LinearLayout> documentsLayoutsList;
    private List<LinearLayout> otherLayoutsList;
    private ArrayList<VehicleDealerPojo> vehicleDealerPojos;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String companyAliasName = "";
    private String user_id, stateId, clientId, typeId, dealerId, statename;
    private String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File photoFile, tyrePicFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tyre);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();

    }

    private void init() {
        context = AddTyre_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);

        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_description = findViewById(R.id.edt_description);
        edt_type = findViewById(R.id.edt_type);
        edt_remark = findViewById(R.id.edt_remark);
        edt_tyreno = findViewById(R.id.edt_tyreno);

        edt_replacementdate = findViewById(R.id.edt_tyrereplacementdate);
        edt_remouning = findViewById(R.id.edt_tyreremoundingdate);
        edt_purchasedate = findViewById(R.id.edt_purchasedate);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);

        tyrePicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Tyre");
        if (!tyrePicFolder.exists())
            tyrePicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
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
        if (Utilities.isInternetAvailable(context)) {
            new AddTyre_Activity.GetstateName().execute(user_id);
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }
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

        mYear3 = cal.get(Calendar.YEAR);
        mMonth3 = cal.get(Calendar.MONTH);
        mDay3 = cal.get(Calendar.DAY_OF_MONTH);

        typelist = new ArrayList<>();
        clientList = new ArrayList<>();
        statelist = new ArrayList<>();
        vehicleDealerPojos = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventHandler() {

        edt_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statelist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new AddTyre_Activity.GetStateList().execute(user_id, "2");
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    stateListDialog(statelist);
                }
            }
        });

        edt_clientname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clientList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new AddTyre_Activity.GetClientList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    clientListDialog(clientList);
                }
            }
        });


        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typelist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new AddTyre_Activity.GetTypeList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    typeListDialog(typelist);
                }
            }
        });


        edt_purchasedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_purchasedate.setText("");

                        edt_purchasedate.setText(
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }
        });


        btn_addotherdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_otherdates, null);
                LinearLayout ll = (LinearLayout) rowView;
                otherLayoutsList.add(ll);
                ll_Otherdates.addView(rowView, ll_Otherdates.getChildCount() - 1);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        edt_remouning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_remouning.setText(
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
                    //  dpd1.getDatePicker().setMinDate(c.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });


        edt_replacementdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_replacementdate.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear2 = year;
                        mMonth2 = monthOfYear;
                        mDay2 = dayOfMonth;
                    }
                }, mYear2, mMonth2, mDay2);
                Calendar c = Calendar.getInstance();
                c.set(mYear, mMonth, mDay);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                    dpd1.getDatePicker().setMinDate(c.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });


        btn_addotherdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_otherdates, null);
                LinearLayout ll = (LinearLayout) rowView;
                otherLayoutsList.add(ll);
                ll_Otherdates.addView(rowView, ll_Otherdates.getChildCount() - 1);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });


        btn_adddocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_document, null);
                LinearLayout ll = (LinearLayout) rowView;
                documentsLayoutsList.add(ll);
                ll_documents.addView(rowView, ll_documents.getChildCount() - 1);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitData();
                    }
                });
            }
        });

    }

    private void stateListDialog(final ArrayList<StatePojo> statelist) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select State");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < statelist.size(); i++) {

            arrayAdapter.add(String.valueOf(statelist.get(i).getstate()));
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
                edt_state.setText(statelist.get(which).getstate());
                stateId = statelist.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetStateList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getAllStates"));
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
                        statelist = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                StatePojo summary = new StatePojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("states").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setstate(jsonObj.getString("states"));
                                    statelist.add(summary);
                                }
                            }
                            if (statelist.size() != 0) {
                                stateListDialog(statelist);
                            } else {
                                Utilities.showAlertDialog(context, "No Record Found", "Please enter family code manually", false);
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


    private void typeListDialog(final ArrayList<TypePojo> typelist) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < typelist.size(); i++) {

            arrayAdapter.add(String.valueOf(typelist.get(i).gettype()));
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
                edt_type.setText(typelist.get(which).gettype());
                typeId = typelist.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetTypeList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getAllType"));
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
                        statelist = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                TypePojo summary = new TypePojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("type").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setType(jsonObj.getString("type"));
                                    typelist.add(summary);
                                }
                            }
                            if (typelist.size() != 0) {
                                typeListDialog(typelist);
                            } else {
                                Utilities.showAlertDialog(context, "No Record Found", "Please enter  rto agent manually", false);
                            }
                        }
                    } else {
                        Utilities.showAlertDialog(context, "No Record Found", "Please enter rto agent manually", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void clientListDialog(final ArrayList<ClientMainListPojo> clientList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Client");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < clientList.size(); i++) {

            arrayAdapter.add(String.valueOf(clientList.get(i).getName()));
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
                edt_clientname.setText(clientList.get(which).getName());
                clientId = clientList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetClientList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getAllClients"));
            param.add(new ParamsPojo("user_id", params[0]));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.CLIENTAPI, param);
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
                        clientList = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                ClientMainListPojo summary = new ClientMainListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("name").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setName(jsonObj.getString("name"));
                                    clientList.add(summary);
                                }
                            }
                            if (clientList.size() != 0) {
                                clientListDialog(clientList);
                            } else {
                                Utilities.showAlertDialog(context, "No Record Found", "Please enter client manually", false);
                            }
                        }
                    } else {
                        Utilities.showAlertDialog(context, "No Record Found", "Please enter client manually", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        img_save = findViewById(R.id.img_save);
        mToolbar.setTitle("Add Tyre Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void submitData() {

        if (edt_state.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select State");
            return;
        }

        if (edt_vehicleno.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Vehicle No.");
            return;
        }


        ArrayList<TyreDetailsPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        for (int i = 0; i < otherLayoutsList.size(); i++) {

            if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {
                if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {

                    TyreDetailsPojo.OtherDatesListPojo otherDateObj = new TyreDetailsPojo.OtherDatesListPojo();

                    otherDateObj.setOther_date(changeDateFormat("dd/MM/yyyy", "yyyy-MM-dd",
                            ((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim()));
                    otherDateObj.setText(((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_text)).getText().toString().trim());

                    otherDatesList.add(otherDateObj);
                }
            } else {
                ((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_text)).setError("Enter Text");
                return;
            }
        }

        JsonArray otherDatesJSONArray = new JsonArray();

        for (int i = 0; i < otherDatesList.size(); i++) {
            JsonObject otherDatesJSONObj = new JsonObject();
            otherDatesJSONObj.addProperty("date", otherDatesList.get(i).getOther_date());
            otherDatesJSONObj.addProperty("des", otherDatesList.get(i).getText());
            otherDatesJSONArray.add(otherDatesJSONObj);
        }

        ArrayList<TyreDetailsPojo.DocumentListPojo> documentsList = new ArrayList<>();
        for (int i = 0; i < documentsLayoutsList.size(); i++) {

            if (!((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim().equals("")) {

                TyreDetailsPojo.DocumentListPojo documentObj = new TyreDetailsPojo.DocumentListPojo();

                documentObj.setDocument(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim());

                documentsList.add(documentObj);
            }
        }

        JsonArray documentJSONArray = new JsonArray();

        for (int i = 0; i < documentsList.size(); i++) {
            JsonObject documentJSONObj = new JsonObject();
            documentJSONObj.addProperty("photo", documentsList.get(i).getDocument());
            documentJSONArray.add(documentJSONObj);
        }
        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "add");
        mainObj.addProperty("State", stateId);
        mainObj.addProperty("Name_of_the_Client", clientId);
        mainObj.addProperty("vehicle_dealer", dealerId);
        mainObj.addProperty("t_Type", typeId);
        mainObj.addProperty("Description", edt_description.getText().toString().trim());
        mainObj.addProperty("Remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("Tyre_No", edt_tyreno.getText().toString().trim());
        mainObj.addProperty("Tyre_Replacement_Date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_replacementdate.getText().toString().trim()));
        mainObj.addProperty("Date_of_Purchase", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_purchasedate.getText().toString().trim()));

        mainObj.addProperty("Tyre_remounding_Date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_remouning.getText().toString().trim()));

        mainObj.add("documents", documentJSONArray);
        mainObj.addProperty("Vehicle_No", edt_vehicleno.getText().toString().trim());
        mainObj.add("other_dates", otherDatesJSONArray);
        mainObj.addProperty("created_by", user_id);
        mainObj.addProperty("updated_by", user_id);

        // Log.i("LifeInsuranceJson", mainObj.toString());

        if (Utilities.isInternetAvailable(context)) {
            new AddTyre_Activity.AddTyreDetails().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }


    }

    public class AddTyreDetails extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.TYREAPI, params[0]);
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

                        new Fragment_tyre.GetTyreList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Tyre Details Saved Successfully");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.ic_alert_red_24dp);
                    builder.setMessage("Please provide permission for Camera and Gallery");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null)));
                        }
                    });
                    builder.create();
                    AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();
                }
            }

        }
    }

    public void selectDate(View view) {
        final EditText edt_otheredate = (EditText) view;

        DatePickerDialog dpd2 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edt_otheredate.setError(null);
                edt_otheredate.setText(
                        changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy",
                                Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))
                );

            }
        }, mYear3, mMonth3, mDay3);
        try {
            dpd2.getDatePicker().setCalendarViewShown(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dpd2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        dpd2.show();
    }


    public void selectDocuments(View view) {
        if (doesAppNeedPermissions()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, 1);
                return;
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    edt_selectdocuments = (EditText) view;
                    final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Choose a Document"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setCancelable(false);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take a Photo")) {
                                photoFile = new File(tyrePicFolder, "doc_image.png");
                                photoURI = Uri.fromFile(photoFile);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(intent, CAMERA_REQUEST);
                            } else if (options[item].equals("Choose from Gallery")) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_REQUEST);
                            } else if (options[item].equals("Choose a Document")) {
                                FilePickerBuilder
                                        .getInstance()
                                        .setMaxCount(1)
                                        .setActivityTheme(R.style.LibAppTheme)
                                        .pickFile(AddTyre_Activity.this);
                            }
                        }


                    });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                }
            }
        } else {
            if (Utilities.isNetworkAvailable(context)) {
                edt_selectdocuments = (EditText) view;
                final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Choose a Document"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setCancelable(false);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take a Photo")) {
                            photoFile = new File(tyrePicFolder, "doc_image.png");
                            photoURI = Uri.fromFile(photoFile);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(intent, CAMERA_REQUEST);
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, GALLERY_REQUEST);
                        } else if (options[item].equals("Choose a Document")) {
                            FilePickerBuilder
                                    .getInstance()
                                    .setMaxCount(1)
                                    .setActivityTheme(R.style.LibAppTheme)
                                    .pickFile(AddTyre_Activity.this);
                        }
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
            } else {
                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
            }
        }


    }

    public void removeDocument(View view) {
        ll_documents.removeView((View) view.getParent());
        documentsLayoutsList.remove(view.getParent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(AddTyre_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(AddTyre_Activity.this);
            }

            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                Uri fileUri = data.getData();
                ArrayList<String> filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                File fileToBeUploaded = new File(filePath.get(0));
                new AddTyre_Activity.UploadProductPhoto().execute(fileToBeUploaded);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                savefile(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private class UploadProductPhoto extends AsyncTask<File, Integer, String> {
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
        protected String doInBackground(File... params) {
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.UPLOADFILEAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadFile");
                multipart.addFormField("user_id", user_id);
                multipart.addFilePart("document", params[0]);

                List<String> response = multipart.finish();
                for (String line : response) {
                    res = res + line;
                }
                return res;
            } catch (IOException ex) {
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                pd.dismiss();

                if (result != null && result.length() > 0 && !result.equalsIgnoreCase("[]")) {
                    JSONObject mainObj = new JSONObject(result);
                    String type = mainObj.getString("type");
                    String message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("Success")) {
                        JSONObject Obj1 = mainObj.getJSONObject("result");
                        String document_name = Obj1.getString("name");
                        edt_selectdocuments.setText(document_name);

                    } else {
                        Utilities.showSnackBar(ll_parent, message);
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void savefile(Uri sourceuri) {
        Log.i("sourceuri1", "" + sourceuri);
        String sourceFilename = sourceuri.getPath();
        String destinationFilename = Environment.getExternalStorageDirectory() + "/RTO/"
                + "/Tyre/" + File.separatorChar + "img.png";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File photoFileToUpload = new File(destinationFilename);
        new AddTyre_Activity.UploadProductPhoto().execute(photoFileToUpload);
//        doc_image_uri = Uri.fromFile(imageFile);
    }

    public class GetstateName extends AsyncTask<String, Void, String> {
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
            param.add(new ParamsPojo("type", "getDefaultState"));
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
                                //id = jsonObj.getString("id");
                                stateId = jsonObj.getString("state_id");
                                statename = jsonObj.getString("StateName");
                                edt_state.setText(statename);
                            }
                        }
                    } else {
                        edt_state.setText("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}



