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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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
import in.rto.collections.adapters.listClientsAdapter;
import in.rto.collections.fragments.Fragment_Other_Vehicle_Details;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.models.VehicleDealerListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.MultipartUtility;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class EditOtherVehicle_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates, ll_wheeldates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates, btn_addwheeldates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername,
            edt_description, edt_type,
            edt_remark;

    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private int mYear3, mMonth3, mDay3;
    private EditText edt_selectdocuments = null;
    private ImageView img_save;
    private List<LinearLayout> serviceDatesLayoutsList;
    private ArrayList<ClientMainListPojo> clientList;
    private List<LinearLayout> documentsLayoutsList;
    private List<LinearLayout> otherLayoutsList;
    private List<LinearLayout> wheelLayoutsList;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String companyAliasName = "", documentType;
    private String user_id, stateId, clientId = "0", typeId, rtoId, id, serviceDateId = "0", otherDateId = "0", wheeldateId = "0", documentId;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private VehicleDealerListPojo vehicaldealerdetails;
    private File photoFile, vehicledealerPicFolder;
    private String isshowtocustomer;
    private CheckBox isshow_to_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_other_vehicle);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = EditOtherVehicle_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);

        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_description = findViewById(R.id.edt_description);
        edt_type = findViewById(R.id.edt_type);
        edt_remark = findViewById(R.id.edt_remark);
        ll_servicedates = findViewById(R.id.ll_servicedates);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        ll_wheeldates = findViewById(R.id.ll_wheeldates);
        isshow_to_customer = findViewById(R.id.is_show_customer);


        btn_addservicedates = findViewById(R.id.btn_addservicedates);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);
        btn_addwheeldates = findViewById(R.id.btn_addwheeldates);

        vehicledealerPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Vehicle_dealer");
        if (!vehicledealerPicFolder.exists())
            vehicledealerPicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

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

        mYear3 = cal.get(Calendar.YEAR);
        mMonth3 = cal.get(Calendar.MONTH);
        mDay3 = cal.get(Calendar.DAY_OF_MONTH);


        typelist = new ArrayList<>();
        clientList = new ArrayList<>();
        statelist = new ArrayList<>();
        serviceDatesLayoutsList = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        wheelLayoutsList = new ArrayList<>();
        vehicaldealerdetails = (VehicleDealerListPojo) getIntent().getSerializableExtra("vehicleDetails");

        id = vehicaldealerdetails.getId();
        stateId = vehicaldealerdetails.getStateId();
        clientId = vehicaldealerdetails.getClient_id();
        typeId = vehicaldealerdetails.getType_id();
        rtoId = vehicaldealerdetails.getRto_agent_id();


        edt_clientname.setText(vehicaldealerdetails.getClient_name());
        edt_state.setText(vehicaldealerdetails.getStateName());
        edt_description.setText(vehicaldealerdetails.getDescription());
        edt_remark.setText(vehicaldealerdetails.getRemark());
        edt_type.setText(vehicaldealerdetails.getType_name());
        edt_vehicleownername.setText(vehicaldealerdetails.getVehicle_owner());
        edt_description.setText(vehicaldealerdetails.getDescription());
        edt_vehicleno.setText(vehicaldealerdetails.getVehicle_no());

        if (vehicaldealerdetails.getIsshowto_customer().equals("1")) {
            isshow_to_customer.setChecked(true);
        } else {
            isshow_to_customer.setChecked(false);
        }

        //edt_insurancepolicyno.setText(vehicaldealerdetails.getVehicle_no());
        ArrayList<VehicleDealerListPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        serviceDatesList = vehicaldealerdetails.getService_date();

        if (serviceDatesList.size() != 0) {
            for (int i = 0; i < serviceDatesList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_servicedates, null);
                serviceDatesLayoutsList.add((LinearLayout) rowView);
                ll_servicedates.addView(rowView, ll_servicedates.getChildCount());

                ((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).setText(changeDateFormat("yyyy-MM-dd",
                        "dd/MM/yyyy",
                        serviceDatesList.get(i).getService_date()));
                ((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_km)).setText(serviceDatesList.get(i).getText());
                serviceDateId = serviceDatesList.get(i).getService_date_id();

            }
        } else {
            //tv_ser.setText("No Maturity Dates Added");
        }
        ArrayList<VehicleDealerListPojo.WheelDatesListPojo> wheelDatesList = new ArrayList<>();
        wheelDatesList = vehicaldealerdetails.getWheel_date();

        if (wheelDatesList.size() != 0) {
            for (int i = 0; i < wheelDatesList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_wheeldates, null);
                wheelLayoutsList.add((LinearLayout) rowView);
                ll_wheeldates.addView(rowView, ll_wheeldates.getChildCount());

                ((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_wheeldate)).setText(changeDateFormat("yyyy-MM-dd",
                        "dd/MM/yyyy",
                        wheelDatesList.get(i).getWheel_alignment_date()));
                ((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_km)).setText(wheelDatesList.get(i).getText());
                wheeldateId = wheelDatesList.get(i).getWheel_date_id();

            }
        } else {
            //tv_ser.setText("No Maturity Dates Added");
        }


        ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        otherDatesList = vehicaldealerdetails.getOther_date();

        if (otherDatesList.size() != 0) {
            for (int i = 0; i < otherDatesList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_otherdates, null);
                otherLayoutsList.add((LinearLayout) rowView);
                ll_Otherdates.addView(rowView, ll_Otherdates.getChildCount());

                ((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).setText(changeDateFormat("yyyy-MM-dd",
                        "dd/MM/yyyy",
                        otherDatesList.get(i).getOther_date()));
                ((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_text)).setText(otherDatesList.get(i).getText());
                otherDateId = otherDatesList.get(i).getOther_date_id();
            }
        } else {
            //tv_ser.setText("No Maturity Dates Added");
        }


        ArrayList<VehicleDealerListPojo.DocumentListPojo> documentsList = new ArrayList<>();
        documentsList = vehicaldealerdetails.getDocument();

        if (documentsList.size() != 0) {
            for (int i = 0; i < documentsList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_document, null);
                documentsLayoutsList.add((LinearLayout) rowView);
                ll_documents.addView(rowView, ll_documents.getChildCount());
//                Uri uri = Uri.parse(documentsList.get(i).getDocument());
//                String document_name = uri.getLastPathSegment();
//                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_documentname)).setText(document_name);
                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).setText(documentsList.get(i).getOriginal_name());
                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).setText(documentsList.get(i).getDocument_name());
            }
        } else {
            // tv_documents.setText("No Documents Added");
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventHandler() {

        edt_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statelist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetStateList().execute(user_id, "2");
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
                        new GetClientList().execute(user_id);
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
                        new GetTypeList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    typeListDialog(typelist);
                }
            }
        });

        btn_addservicedates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_servicedates, null);
                LinearLayout ll = (LinearLayout) rowView;
                serviceDatesLayoutsList.add(ll);
                ll_servicedates.addView(rowView, ll_servicedates.getChildCount() - 1);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        btn_addwheeldates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_wheeldates, null);
                LinearLayout ll = (LinearLayout) rowView;
                wheelLayoutsList.add(ll);
                ll_wheeldates.addView(rowView, ll_wheeldates.getChildCount() - 1);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
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

    private void submitData() {

        if (edt_state.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select State");
            return;
        }

        if (edt_vehicleno.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Enter Vehicle No.");
            return;
        }

        if (!Utilities.isVehicleNo(edt_vehicleno)) {
            Utilities.showSnackBar(ll_parent, "Please Enter Valid Vehicle Number");
            return;
        }

        ArrayList<VehicleDealerListPojo.WheelDatesListPojo> wheelDatesList = new ArrayList<>();
        for (int i = 0; i < wheelLayoutsList.size(); i++) {

            if (!((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_wheeldate)).getText().toString().trim().equals("")) {
                if (!((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_wheeldate)).getText().toString().trim().equals("")) {

                    VehicleDealerListPojo.WheelDatesListPojo wheelDateObj = new VehicleDealerListPojo.WheelDatesListPojo();

                    if (i < wheelDatesList.size()) {
                        wheelDateObj.setWheel_date_id(wheeldateId);
                    } else {
                        wheelDateObj.setWheel_date_id("0");
                    }
                    wheelDateObj.setWheel_alignment_date(changeDateFormat("dd/MM/yyyy", "yyyy-MM-dd",
                            ((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_wheeldate)).getText().toString().trim()));
                    wheelDateObj.setText(((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_km)).getText().toString().trim());

                    wheelDatesList.add(wheelDateObj);
                }
            } else {
                ((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_wheeldate)).setError("Enter Date");
                return;
            }
        }

        JsonArray wheelDatesJSONArray = new JsonArray();

        for (int i = 0; i < wheelDatesList.size(); i++) {
            JsonObject wheelDatesJSONObj = new JsonObject();
            wheelDatesJSONObj.addProperty("date", wheelDatesList.get(i).getWheel_alignment_date());
            wheelDatesJSONObj.addProperty("text", wheelDatesList.get(i).getText());
            wheelDatesJSONObj.addProperty("id", wheelDatesList.get(i).getWheel_date_id());
            wheelDatesJSONArray.add(wheelDatesJSONObj);
        }

        ArrayList<VehicleDealerListPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        for (int i = 0; i < serviceDatesLayoutsList.size(); i++) {

            if (!((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).getText().toString().trim().equals("")) {
                if (!((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).getText().toString().trim().equals("")) {

                    VehicleDealerListPojo.ServiceDatesListPojo serviceDateObj = new VehicleDealerListPojo.ServiceDatesListPojo();

                    if (i < serviceDatesList.size()) {
                        serviceDateObj.setService_date_id(serviceDateId);
                    } else {
                        serviceDateObj.setService_date_id("0");
                    }
                    serviceDateObj.setService_date(changeDateFormat("dd/MM/yyyy", "yyyy-MM-dd",
                            ((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).getText().toString().trim()));
                    serviceDateObj.setText(((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_km)).getText().toString().trim());

                    serviceDatesList.add(serviceDateObj);
                }
            } else {
                ((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).setError("Enter Date");
                return;
            }
        }

        if (isshow_to_customer.isChecked()) {
            isshowtocustomer = "1";
        } else {
            isshowtocustomer = "0";
        }
        JsonArray serviceDatesJSONArray = new JsonArray();

        for (int i = 0; i < serviceDatesList.size(); i++) {
            JsonObject serviceDatesJSONObj = new JsonObject();
            serviceDatesJSONObj.addProperty("date", serviceDatesList.get(i).getService_date());
            serviceDatesJSONObj.addProperty("text", serviceDatesList.get(i).getText());
            serviceDatesJSONObj.addProperty("id", serviceDatesList.get(i).getService_date_id());
            serviceDatesJSONArray.add(serviceDatesJSONObj);
        }

        ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        for (int i = 0; i < otherLayoutsList.size(); i++) {

            if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {
                if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {

                    VehicleDealerListPojo.OtherDatesListPojo otherDateObj = new VehicleDealerListPojo.OtherDatesListPojo();
                    if (i < otherDatesList.size()) {
                        otherDateObj.setOther_date_id(otherDateId);
                        //serviceDatesList.setFamily_details_id(clientDetails.getRelation_details().get(i).getFamily_details_id());
                    } else {
                        // clientFamilyObj.setFamily_details_id("0");
                        otherDateObj.setOther_date_id("0");
                    }


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
            otherDatesJSONObj.addProperty("id", otherDatesList.get(i).getOther_date_id());
            otherDatesJSONArray.add(otherDatesJSONObj);
        }

        ArrayList<VehicleDealerListPojo.DocumentListPojo> documentsList = new ArrayList<>();
        for (int i = 0; i < documentsLayoutsList.size(); i++) {

            if (!((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim().equals("")) {

                VehicleDealerListPojo.DocumentListPojo documentObj = new VehicleDealerListPojo.DocumentListPojo();
                if (i < documentsList.size()) {
                    documentObj.setDocument_id(documentId);
                    //serviceDatesList.setFamily_details_id(clientDetails.getRelation_details().get(i).getFamily_details_id());
                } else {
                    // clientFamilyObj.setFamily_details_id("0");
                    documentObj.setDocument_id("0");
                }
                documentObj.setDocument(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim());
                documentObj.setDocument_name(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).getText().toString().trim());

                documentsList.add(documentObj);
            }
        }

        JsonArray documentJSONArray = new JsonArray();
        for (int i = 0; i < documentsList.size(); i++) {
            JsonObject documentJSONObj = new JsonObject();
            documentJSONObj.addProperty("photo", documentsList.get(i).getDocument());
            documentJSONObj.addProperty("document_name", documentsList.get(i).getDocument_name());
            documentJSONObj.addProperty("id", documentsList.get(i).getDocument_id());
            documentJSONArray.add(documentJSONObj);
        }


        JsonObject mainObj = new JsonObject();


        mainObj.addProperty("type", "update");
        mainObj.addProperty("state", stateId);
        mainObj.addProperty("client_name", clientId);
        mainObj.addProperty("vehicle_owner", edt_vehicleownername.getText().toString().trim());
        mainObj.addProperty("rto_agent", rtoId);
        mainObj.addProperty("v_type", typeId);
        mainObj.add("documents", documentJSONArray);
        mainObj.addProperty("remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("description", edt_description.getText().toString().trim());
        mainObj.addProperty("vehicle_no", edt_vehicleno.getText().toString().trim());
        mainObj.add("service_dates", serviceDatesJSONArray);
        mainObj.add("other_dates", otherDatesJSONArray);
        mainObj.add("wheel_dates", wheelDatesJSONArray);
        mainObj.addProperty("user_id", user_id);
        mainObj.addProperty("id", id);
        mainObj.addProperty("is_show_to_customer", isshowtocustomer);


        if (Utilities.isInternetAvailable(context)) {
            new UpdateOtherVehicleDetails().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }


    }

    public class UpdateOtherVehicleDetails extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.OTHERVEHICLEDEALER, params[0]);
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

                        new Fragment_Other_Vehicle_Details.GetOtherVehicleDealerList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Vehicle Details Updated Successfully.");
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

    private void clientListDialog(final ArrayList<ClientMainListPojo> clientList) {
        final listClientsAdapter[] adapter = new listClientsAdapter[1];

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Client");
        builderSingle.setCancelable(false);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.alert_dialog_listview_search, null);
        builderSingle.setView(view);
        final ListView listView = (ListView) view.findViewById(R.id.alertSearchListView);
        final android.widget.SearchView searchView = (android.widget.SearchView) view.findViewById(R.id.searchView);
        searchView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            searchView.setFocusedByDefault(false);
        }

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (!query.equals("")) {
                    ArrayList<ClientMainListPojo> SearchedClients = new ArrayList<>();
                    for (ClientMainListPojo list : clientList) {
                        String listToBeSearched = list.getName().toLowerCase();
                        if (listToBeSearched.contains(query.toLowerCase())) {
                            SearchedClients.add(list);
                        }
                    }
                    adapter[0] = new listClientsAdapter(context, SearchedClients);
                    listView.setAdapter(adapter[0]);
                } else {
                    adapter[0] = new listClientsAdapter(context, clientList);
                    listView.setAdapter(adapter[0]);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<ClientMainListPojo> SearchedList = new ArrayList<>();
                    for (ClientMainListPojo list : clientList) {
                        String listToBeSearched = list.getName().toLowerCase();
                        if (listToBeSearched.contains(newText.toLowerCase())) {
                            SearchedList.add(list);
                        }
                    }
                    adapter[0] = new listClientsAdapter(context, SearchedList);
                    listView.setAdapter(adapter[0]);
                } else if (newText.equals("")) {
                    adapter[0] = new listClientsAdapter(context, clientList);
                    listView.setAdapter(adapter[0]);
                }
                return true;
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setFastScrollEnabled(true);

        adapter[0] = new listClientsAdapter(context, clientList);
        listView.setAdapter(adapter[0]);

        final TextView emptyText = (TextView) view.findViewById(R.id.empty);
        listView.setEmptyView(emptyText);

        builderSingle.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClientMainListPojo selected_client = adapter[0].getSelected();
                if (selected_client != null) {

                    edt_clientname.setText(selected_client.getName());
                    clientId = selected_client.getId();
                } else {
                    Utilities.showSnackBar(ll_parent, "Please select client");

                }


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

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        img_save = findViewById(R.id.img_save);
        mToolbar.setTitle("Edit Vehicle Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void selectDate(View view) {
        final EditText edt_servicedate = (EditText) view;
        final EditText edt_otheredate = (EditText) view;
        DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edt_servicedate.setError(null);
                edt_servicedate.setText(
                        changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy",
                                Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))
                );

            }
        }, mYear2, mMonth2, mDay2);
        try {
            dpd1.getDatePicker().setCalendarViewShown(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        dpd1.show();
    }

    public void selectDocuments(View view) {
        documentType = "selectedImage";
        if (doesAppNeedPermissions()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, 1);
                return;
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    edt_selectdocuments = (EditText) view;
                    //edt_name = (EditText) view;
                    final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Choose a Document"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setCancelable(false);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take a Photo")) {
                                photoFile = new File(vehicledealerPicFolder, "doc_image.png");
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
                                        .pickFile(EditOtherVehicle_Activity.this);
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
                            photoFile = new File(vehicledealerPicFolder, "doc_image.png");
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
                                    .pickFile(EditOtherVehicle_Activity.this);
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(EditOtherVehicle_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(EditOtherVehicle_Activity.this);
            }

            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                Uri fileUri = data.getData();
                ArrayList<String> filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                File fileToBeUploaded = new File(filePath.get(0));
                new UploadProductPhoto().execute(fileToBeUploaded);
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
                        if (documentType.equals("selectedImage")) {
                            edt_selectdocuments.setText(document_name);
                        }

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
        String destinationFilename = Environment.getExternalStorageDirectory() + "/Insurance/"
                + "/Life Insurance/" + File.separatorChar + "img.png";

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
        new UploadProductPhoto().execute(photoFileToUpload);
//        doc_image_uri = Uri.fromFile(imageFile);
    }

    public void removeOtherDates(View view) {
        ll_Otherdates.removeView((View) view.getParent());
        otherLayoutsList.remove(view.getParent());
    }

    public void deleteDocument(View view) {
        ll_documents.removeView((View) view.getParent());
        documentsLayoutsList.remove(view.getParent());
    }

    public void removeserviceDates(View view) {
        ll_servicedates.removeView((View) view.getParent());
        serviceDatesLayoutsList.remove(view.getParent());
    }

    public void removeWheelDates(View view) {
        ll_wheeldates.removeView((View) view.getParent());
        wheelLayoutsList.remove(view.getParent());
    }

}
