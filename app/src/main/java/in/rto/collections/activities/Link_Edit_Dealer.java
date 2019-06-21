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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.FrequencyPojo;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.RTOAgentPojo;
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

public class Link_Edit_Dealer extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_rtoagent,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_purcasedate, edt_temregno, edt_remark, edt_hypothecatedto, edt_bank, edt_branch, edt_borrowername,
            edt_loanamount, edt_accountnumber, edt_sactiondate, edt_installmentanount, edt_startdate,
            edt_enddate, edt_frequency, edt_selectVehicleImage;

    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private int mYear3, mMonth3, mDay3;
    private int mYear8, mMonth8, mDay8;
    private int mYear9, mMonth9, mDay9;
    private int mYear10, mMonth10, mDay10;
    private EditText edt_selectdocuments = null;
    private ImageView img_save;
    private List<LinearLayout> serviceDatesLayoutsList;
    private ArrayList<ClientMainListPojo> clientList;
    private List<LinearLayout> documentsLayoutsList, documentsLayoutsList1;
    private List<LinearLayout> otherLayoutsList, otherLayoutsList1;
    private ArrayList<RTOAgentPojo> rtoagentlist;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String companyAliasName = "", documentType;
    private String user_id, stateId, clientId = "0", typeId, rtoId, id, serviceDateId = "0", frequency, otherDateId = "0", documentId, rtocreatedby, vehicleId, dealerId;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private VehicleDealerListPojo vehicaldealerdetails;
    private File photoFile, vehicledealerPicFolder;
    private CheckBox isshow_to_rto, isshow_to_customer;
    private String isshowtocustomer, isshowtorto;
    private LinkPojo linkPojo;
    private FrameLayout frameLayout;
    private FragmentManager supportFragmentManager;
    private ArrayList<FrequencyPojo> frequencylist;
    private LinearLayout bank_details, bank_feild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link__edit__dealer);
        init();
        getSessionData();
        setDefaults();
        setUpToolbar();
        setEventHandler();
    }

    private void init() {
        context = Link_Edit_Dealer.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
        frameLayout = findViewById(R.id.frameLayout);
        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_rtoagent = findViewById(R.id.edt_rtoagent);
        edt_description = findViewById(R.id.edt_description);
        edt_type = findViewById(R.id.edt_type);
        edt_engineno = findViewById(R.id.edt_engineno);
        edt_chassisno = findViewById(R.id.edt_chassisno);
        edt_insurancepolicyno = findViewById(R.id.edt_insurancepolicyno);
        edt_renewaldate = findViewById(R.id.edt_renewaldate);
        edt_purcasedate = findViewById(R.id.edt_purchasedate);
        edt_temregno = findViewById(R.id.edt_temregno);
        edt_remark = findViewById(R.id.edt_remark);
        edt_hypothecatedto = findViewById(R.id.edt_hypothecated);
        isshow_to_rto = findViewById(R.id.is_show_rto);
        isshow_to_customer = findViewById(R.id.is_show_customer);
        ll_servicedates = findViewById(R.id.ll_servicedates);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);


        edt_bank = findViewById(R.id.edt_bank);
        edt_branch = findViewById(R.id.edt_branch);
        edt_borrowername = findViewById(R.id.edt_borrower);
        edt_accountnumber = findViewById(R.id.edt_accountno);
        edt_loanamount = findViewById(R.id.edt_loanamount);
        edt_enddate = findViewById(R.id.edt_enddate);
        edt_startdate = findViewById(R.id.edt_startdate);
        edt_installmentanount = findViewById(R.id.edt_installmentamount);
        edt_frequency = findViewById(R.id.edt_frequency);
        edt_sactiondate = findViewById(R.id.edt_saction);
        bank_details = findViewById(R.id.bank_details);
        bank_feild = findViewById(R.id.bank_feild);

        btn_addservicedates = findViewById(R.id.btn_addservicedates);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);

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


        mYear8 = cal.get(Calendar.YEAR);
        mMonth8 = cal.get(Calendar.MONTH);
        mDay8 = cal.get(Calendar.DAY_OF_MONTH);

        mYear9 = cal.get(Calendar.YEAR);
        mMonth9 = cal.get(Calendar.MONTH);
        mDay9 = cal.get(Calendar.DAY_OF_MONTH);

        mYear10 = cal.get(Calendar.YEAR);
        mMonth10 = cal.get(Calendar.MONTH);
        mDay10 = cal.get(Calendar.DAY_OF_MONTH);

        typelist = new ArrayList<>();
        clientList = new ArrayList<>();
        statelist = new ArrayList<>();
        frequencylist = new ArrayList<>();
        rtoagentlist = new ArrayList<>();
        serviceDatesLayoutsList = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        otherLayoutsList1 = new ArrayList<>();
        documentsLayoutsList1 = new ArrayList<>();
        vehicaldealerdetails = (VehicleDealerListPojo) getIntent().getSerializableExtra("vehicleDetails");

        id = vehicaldealerdetails.getId();
        stateId = vehicaldealerdetails.getStateId();
        clientId = vehicaldealerdetails.getClient_id();

        typeId = vehicaldealerdetails.getType_id();
        rtoId = vehicaldealerdetails.getRto_agent_id();
        edt_clientname.setText(vehicaldealerdetails.getClient_name());
        edt_chassisno.setText(vehicaldealerdetails.getChassis_no());
        edt_state.setText(vehicaldealerdetails.getStateName());
        edt_description.setText(vehicaldealerdetails.getDescription());
        edt_renewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", vehicaldealerdetails.getInsurance_renewal_date()));
        edt_purcasedate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                vehicaldealerdetails.getPurchase_date()));
        edt_engineno.setText(vehicaldealerdetails.getEngine_no());
        edt_hypothecatedto.setText(vehicaldealerdetails.getHypothecated_to());
        edt_insurancepolicyno.setText(vehicaldealerdetails.getInsurance_policy_no());
        edt_remark.setText(vehicaldealerdetails.getRemark());
        edt_rtoagent.setText(vehicaldealerdetails.getRto_agent_name());
        edt_temregno.setText(vehicaldealerdetails.getTem_reg_no());
        edt_type.setText(vehicaldealerdetails.getType_name());
        edt_vehicleownername.setText(vehicaldealerdetails.getVehicle_owner());
        edt_description.setText(vehicaldealerdetails.getDescription());
        edt_vehicleno.setText(vehicaldealerdetails.getVehicle_no());
        edt_insurancepolicyno.setText(vehicaldealerdetails.getInsurance_policy_no());
        edt_selectVehicleImage.setText(vehicaldealerdetails.getVehicle_image());

        if (vehicaldealerdetails.getIsshowto_customer().equals("1")) {
            isshow_to_customer.setChecked(true);
        } else {
            isshow_to_customer.setChecked(false);
        }
        if (vehicaldealerdetails.getIsshowto_rto().equals("1")) {
            isshow_to_rto.setChecked(true);
        } else {
            isshow_to_rto.setChecked(false);
        }
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

        linkPojo = (LinkPojo) getIntent().getSerializableExtra("rtoDetails");
        rtocreatedby = linkPojo.getCreated_by();
        if (linkPojo.getVehiclertoId() != null) {
            vehicleId = linkPojo.getVehiclertoId();
        } else {
            vehicleId = "0";
        }
        if (linkPojo.getDealerId() != null) {
            dealerId = linkPojo.getDealerId();
        } else {
            dealerId = "0";
        }
        if (linkPojo.getStateId() != null) {
            stateId = linkPojo.getStateId();
            edt_state.setText(linkPojo.getStateName());
        } else {
            stateId = vehicaldealerdetails.getStateId();
            edt_state.setText(vehicaldealerdetails.getStateName());
        }
        if (linkPojo.getChassis_no() != null) {
            edt_chassisno.setText(linkPojo.getChassis_no());
        } else {
            edt_chassisno.setText(vehicaldealerdetails.getChassis_no());
        }
        if (linkPojo.getDescription() != null) {
            edt_description.setText(linkPojo.getDescription());
        } else {
            edt_description.setText(vehicaldealerdetails.getDescription());
        }
        if (linkPojo.getVehicle_owner() != null) {
            edt_vehicleownername.setText(linkPojo.getVehicle_owner());
        } else {
            edt_vehicleownername.setText(vehicaldealerdetails.getVehicle_owner());
        }
        if (linkPojo.getInsurance_renewal_date() != null) {
            edt_renewaldate.setText(linkPojo.getInsurance_renewal_date());
        } else {
            edt_renewaldate.setText(changeDateFormat("yyyy-MM-dd",
                    "dd/MM/yyyy",
                    vehicaldealerdetails.getInsurance_renewal_date()));
        }


        if (linkPojo.getEngine_no() != null) {
            edt_engineno.setText(linkPojo.getEngine_no());
        } else {
            edt_engineno.setText(vehicaldealerdetails.getEngine_no());
        }
        if (linkPojo.getRemark() != null) {
            edt_remark.setText(linkPojo.getRemark());
        } else {
            edt_remark.setText(vehicaldealerdetails.getRemark());
        }

        if (linkPojo.getType_name() != null) {
            edt_type.setText(linkPojo.getType_name());
            if (linkPojo.getType_id() != null) {
                typeId = vehicaldealerdetails.getType_id();
            } else {
                typeId = "0";

            }
            typeId = linkPojo.getType_id();
        } else {
            edt_type.setText(vehicaldealerdetails.getType_name());
            if (vehicaldealerdetails.getType_id() != null) {
                typeId = vehicaldealerdetails.getType_id();
            } else {
                typeId = "0";
            }
            typeId = vehicaldealerdetails.getType_id();
        }
        if (linkPojo.getInsurance_policy_no() != null) {
            edt_insurancepolicyno.setText(linkPojo.getInsurance_policy_no());
        } else {
            edt_insurancepolicyno.setText(vehicaldealerdetails.getInsurance_policy_no());
        }
        if (linkPojo.getVehicle_no() != null) {
            edt_vehicleno.setText(linkPojo.getVehicle_no());
        } else {
            edt_vehicleno.setText(vehicaldealerdetails.getVehicle_no());
        }

        if (linkPojo.getBank_name() != null) {
            edt_bank.setText(linkPojo.getBank_name());
        } else {
            edt_bank.setText(vehicaldealerdetails.getBank_name());
        }

        if (linkPojo.getBranch_name() != null) {
            edt_branch.setText(linkPojo.getBranch_name());
        } else {
            edt_branch.setText(vehicaldealerdetails.getBranch_name());
        }

        if (linkPojo.getFrequency_id() != null) {
            frequency = linkPojo.getFrequency_id();
            edt_frequency.setText(linkPojo.getFrequency());
        } else {
            if (vehicaldealerdetails.getFrequency_id() != null) {
                frequency = vehicaldealerdetails.getFrequency_id();
                edt_frequency.setText(vehicaldealerdetails.getFrequency());
            } else {
                frequency = "0";
            }
        }
        if (linkPojo.getLoan_amount() != null) {
            edt_loanamount.setText(linkPojo.getLoan_amount());
        } else {
            edt_loanamount.setText(vehicaldealerdetails.getLoan_amount());
        }
        if (linkPojo.getLoan_account_number() != null) {
            edt_accountnumber.setText(linkPojo.getLoan_account_number());
        } else {
            edt_accountnumber.setText(vehicaldealerdetails.getLoan_account_number());
        }
        if (linkPojo.getInstallment_amount() != null) {
            edt_installmentanount.setText(linkPojo.getInstallment_amount());
        } else {
            edt_installmentanount.setText(vehicaldealerdetails.getInstallment_amount());
        }
        if (linkPojo.getDate_to_section() != null) {
            edt_sactiondate.setText(linkPojo.getDate_to_section());
        } else {
            edt_sactiondate.setText(changeDateFormat("yyyy-MM-dd",
                    "dd/MM/yyyy",
                    vehicaldealerdetails.getDate_to_section()));
        }
        if (linkPojo.getInstallment_start_date() != null) {
            edt_startdate.setText(linkPojo.getInstallment_start_date());
        } else {
            edt_startdate.setText(changeDateFormat("yyyy-MM-dd",
                    "dd/MM/yyyy", vehicaldealerdetails.getInstallment_start_date()));
        }
        if (linkPojo.getInstallment_end_date() != null) {
            edt_enddate.setText(linkPojo.getInstallment_end_date());
        } else {
            edt_enddate.setText(changeDateFormat("yyyy-MM-dd",
                    "dd/MM/yyyy", vehicaldealerdetails.getInstallment_end_date()));
        }
        if (linkPojo.getBorrower_name() != null) {
            edt_borrowername.setText(linkPojo.getBorrower_name());
        } else {
            edt_borrowername.setText(vehicaldealerdetails.getBorrower_name());
        }
        if (linkPojo.getVehicle_image() != null) {
            edt_selectVehicleImage.setText(linkPojo.getVehicle_image());
        } else {
            edt_selectVehicleImage.setText(vehicaldealerdetails.getVehicle_image());
        }
        ArrayList<LinkPojo.OtherDatesListPojo> otherDatesList1 = new ArrayList<>();
        otherDatesList1 = linkPojo.getOther_date();
        if (linkPojo.getOther_date() != null) {
            if (otherDatesList1.size() != 0) {
                for (int i = 0; i < otherDatesList1.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.add_layout_otherdates, null);
                    otherLayoutsList1.add((LinearLayout) rowView);
                    ll_Otherdates.addView(rowView, ll_Otherdates.getChildCount());

                    ((EditText) otherLayoutsList1.get(i).findViewById(R.id.edt_otherdate)).setText(otherDatesList1.get(i).getOther_date());
                    ((EditText) otherLayoutsList1.get(i).findViewById(R.id.edt_text)).setText(otherDatesList1.get(i).getText());
                    otherDateId = otherDatesList1.get(i).getOther_date_id();
                }

            }
        } else {
            ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesList2 = new ArrayList<>();
            otherDatesList2 = vehicaldealerdetails.getOther_date();

            if (otherDatesList2.size() != 0) {
                for (int i = 0; i < otherDatesList2.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.add_layout_otherdates, null);
                    otherLayoutsList.add((LinearLayout) rowView);
                    ll_Otherdates.addView(rowView, ll_Otherdates.getChildCount());

                    ((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).setText(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy",
                            otherDatesList2.get(i).getOther_date()));
                    ((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_text)).setText(otherDatesList2.get(i).getText());
                    otherDateId = otherDatesList2.get(i).getOther_date_id();
                }
            } else {
                //tv_ser.setText("No Maturity Dates Added");
            }
        }

        ArrayList<LinkPojo.DocumentListPojo> documentsList1 = new ArrayList<>();
        documentsList1 = linkPojo.getDocument();
        if (linkPojo.getDocument() != null) {
            if (documentsList1.size() != 0) {
                for (int i = 0; i < documentsList1.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.add_layout_document, null);
                    documentsLayoutsList1.add((LinearLayout) rowView);
                    ll_documents.addView(rowView, ll_documents.getChildCount());
//                Uri uri = Uri.parse(documentsList.get(i).getDocument());
//                String document_name = uri.getLastPathSegment();
//                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_documentname)).setText(document_name);
                    ((EditText) documentsLayoutsList1.get(i).findViewById(R.id.edt_selectdocuments)).setText(documentsList1.get(i).getOriginal_name());
                    ((EditText) documentsLayoutsList1.get(i).findViewById(R.id.edt_name)).setText(documentsList1.get(i).getDocument_name());
                }
            }
        } else {
            ArrayList<VehicleDealerListPojo.DocumentListPojo> documentsList2 = new ArrayList<>();
            documentsList2 = vehicaldealerdetails.getDocument();

            if (documentsList2.size() != 0) {
                for (int i = 0; i < documentsList2.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.add_layout_document, null);
                    documentsLayoutsList.add((LinearLayout) rowView);
                    ll_documents.addView(rowView, ll_documents.getChildCount());
//                Uri uri = Uri.parse(documentsList.get(i).getDocument());
//                String document_name = uri.getLastPathSegment();
//                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_documentname)).setText(document_name);
                    ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).setText(documentsList2.get(i).getDocument());
                    ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).setText(documentsList2.get(i).getDocument_name());
                }
            } else {
                // tv_documents.setText("No Documents Added");
            }
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventHandler() {
        bank_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bank_feild.getVisibility() == View.VISIBLE) {
                    bank_feild.setVisibility(View.GONE);
                } else {
                    bank_feild.setVisibility(View.VISIBLE);
                }
            }
        });
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

        edt_rtoagent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rtoagentlist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetRTOAgentList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    RTOAgentListDialog(rtoagentlist);
                }
            }
        });

        edt_renewaldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_renewaldate.setText("");

                        edt_renewaldate.setText(
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

        edt_purcasedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_purcasedate.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear1 = year;
                        mMonth1 = monthOfYear;
                        mDay1 = dayOfMonth;
                    }
                }, mYear1, mMonth1, mDay1);
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

        edt_frequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frequencylist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new Link_Edit_Dealer.GetFrqList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    feqListDialog(frequencylist);
                }
            }
        });

        edt_sactiondate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_sactiondate.setText("");

                        edt_sactiondate.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))
                        );

                        mYear8 = year;
                        mMonth8 = monthOfYear;
                        mDay8 = dayOfMonth;
                    }
                }, mYear8, mMonth8, mDay8);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }
        });

        edt_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_startdate.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear9 = year;
                        mMonth9 = monthOfYear;
                        mDay9 = dayOfMonth;
                    }
                }, mYear9, mMonth9, mDay9);

                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                    // dpd1.getDatePicker().setMinDate(c.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });


        edt_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_enddate.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear10 = year;
                        mMonth10 = monthOfYear;
                        mDay10 = dayOfMonth;
                    }
                }, mYear10, mMonth10, mDay10);

                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                    // dpd1.getDatePicker().setMinDate(c.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vehicaldealerdetails.getType_id().equals(linkPojo.getType_id())) {
                    submitData();
                } else if (linkPojo.getType_id() == null) {
                    submitData();
                } else {
                    String message = "";
                    if (vehicaldealerdetails.getType_id() == null) {
                        message = "You are changing vehicle type none to " + linkPojo.getType_name() + ". Do You want to continue?  .";
                    } else if (vehicaldealerdetails.getType_id() == null && linkPojo.getType_id() == null) {
                        message = "You are changing vehicle type none to . Do You want to continue?  .";
                    } else {
                        message = "You are changing vehicle type " + vehicaldealerdetails.getType_name() + " to " + linkPojo.getType_name() + ". Do You want to continue?  .";
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage(message);
                    builder.setIcon(R.drawable.ic_success_24dp);
                    builder.setTitle("Alert");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            submitData();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();

                }
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

    private void feqListDialog(final ArrayList<FrequencyPojo> feqList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Frequency");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);
        final SearchView searchView = findViewById(R.id.searchView);
        for (int i = 0; i < feqList.size(); i++) {
            arrayAdapter.add(String.valueOf(feqList.get(i).getFeq()));
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
                edt_frequency.setText(feqList.get(which).getFeq());
                frequency = feqList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetFrqList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getAllfrequency"));
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
                        frequencylist = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                FrequencyPojo summary = new FrequencyPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("frequency").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setFeq(jsonObj.getString("frequency"));
                                    frequencylist.add(summary);
                                }
                            }
                            if (frequencylist.size() != 0) {
                                feqListDialog(frequencylist);
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


    private void RTOAgentListDialog(final ArrayList<RTOAgentPojo> rtoagentlist) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select RTO Agent");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < rtoagentlist.size(); i++) {

            arrayAdapter.add(String.valueOf(rtoagentlist.get(i).getName()));
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
                edt_rtoagent.setText(rtoagentlist.get(which).getName());
                rtoId = rtoagentlist.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetRTOAgentList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getRTOAgent"));
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
                        statelist = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                RTOAgentPojo summary = new RTOAgentPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("name").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setName(jsonObj.getString("name"));
                                    rtoagentlist.add(summary);
                                }
                            }
                            if (rtoagentlist.size() != 0) {
                                RTOAgentListDialog(rtoagentlist);
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
        mToolbar.setTitle("Link TO " + vehicaldealerdetails.getVehicle_no());
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
        if (!Utilities.isVehicleNo(edt_vehicleno)) {
            Utilities.showSnackBar(ll_parent, "Please Enter Valid Vehicle Number");
            return;
        }
        if (!edt_bank.getText().toString().trim().equals("")) {
            if (edt_bank.getText().toString().trim().equals("")) {
                Utilities.showSnackBar(ll_parent, "Please Enter Bank");
                return;
            }
            if (edt_branch.getText().toString().trim().equals("")) {
                Utilities.showSnackBar(ll_parent, "Please Enter Branch");
                return;
            }
            if (edt_startdate.getText().toString().trim().equals("")) {
                Utilities.showSnackBar(ll_parent, "Please Select Start Date");
                return;
            }
            if (edt_enddate.getText().toString().trim().equals("")) {
                Utilities.showSnackBar(ll_parent, "Please Select End Date");
                return;
            }
            if (edt_frequency.getText().toString().trim().equals("")) {
                Utilities.showSnackBar(ll_parent, "Please Select Frequency");
                return;
            }

        }


        if (isshow_to_customer.isChecked()) {
            isshowtocustomer = "1";
        } else {
            isshowtocustomer = "0";
        }

        if (isshow_to_rto.isChecked()) {
            isshowtorto = "1";
        } else {
            isshowtorto = "0";
        }

        JsonArray otherDatesJSONArray = new JsonArray();
        if (otherLayoutsList1.size() != 0) {
            ArrayList<LinkPojo.OtherDatesListPojo> otherDatesList1 = new ArrayList<>();
            for (int i = 0; i < otherLayoutsList1.size(); i++) {

                if (!((EditText) otherLayoutsList1.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {
                    if (!((EditText) otherLayoutsList1.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {

                        LinkPojo.OtherDatesListPojo otherDateObj = new LinkPojo.OtherDatesListPojo();
                        otherDateObj.setOther_date_id("0");

                        otherDateObj.setOther_date(changeDateFormat("dd/MM/yyyy", "yyyy-MM-dd",
                                ((EditText) otherLayoutsList1.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim()));
                        otherDateObj.setText(((EditText) otherLayoutsList1.get(i).findViewById(R.id.edt_text)).getText().toString().trim());

                        otherDatesList1.add(otherDateObj);
                    }
                } else {
                    ((EditText) otherLayoutsList1.get(i).findViewById(R.id.edt_text)).setError("Enter Text");
                    return;
                }
            }
            for (int i = 0; i < otherDatesList1.size(); i++) {
                JsonObject otherDatesJSONObj = new JsonObject();
                otherDatesJSONObj.addProperty("date", otherDatesList1.get(i).getOther_date());
                otherDatesJSONObj.addProperty("des", otherDatesList1.get(i).getText());
                otherDatesJSONObj.addProperty("id", otherDatesList1.get(i).getOther_date_id());
                otherDatesJSONArray.add(otherDatesJSONObj);
            }

        } else {
            ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
            for (int i = 0; i < otherLayoutsList.size(); i++) {

                if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {
                    if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {

                        VehicleDealerListPojo.OtherDatesListPojo otherDateObj = new VehicleDealerListPojo.OtherDatesListPojo();
                        if (i < otherDatesList.size()) {
                            otherDateObj.setOther_date_id(otherDateId);
                        } else {
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


            for (int i = 0; i < otherDatesList.size(); i++) {
                JsonObject otherDatesJSONObj = new JsonObject();
                otherDatesJSONObj.addProperty("date", otherDatesList.get(i).getOther_date());
                otherDatesJSONObj.addProperty("des", otherDatesList.get(i).getText());
                otherDatesJSONObj.addProperty("id", otherDatesList.get(i).getOther_date_id());
                otherDatesJSONArray.add(otherDatesJSONObj);
            }

        }
        JsonArray documentJSONArray = new JsonArray();
        if (documentsLayoutsList1.size() != 0) {
            ArrayList<LinkPojo.DocumentListPojo> documentsList1 = new ArrayList<>();
            for (int i = 0; i < documentsLayoutsList1.size(); i++) {

                if (!((EditText) documentsLayoutsList1.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim().equals("")) {

                    LinkPojo.DocumentListPojo documentObj = new LinkPojo.DocumentListPojo();
                    documentObj.setDocument_id("0");
                    documentObj.setDocument(((EditText) documentsLayoutsList1.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim());
                    documentObj.setDocument_name(((EditText) documentsLayoutsList1.get(i).findViewById(R.id.edt_name)).getText().toString().trim());

                    documentsList1.add(documentObj);
                }
            }
            for (int i = 0; i < documentsList1.size(); i++) {
                JsonObject documentJSONObj = new JsonObject();
                documentJSONObj.addProperty("photo", documentsList1.get(i).getDocument());
                documentJSONObj.addProperty("document_name", documentsList1.get(i).getDocument_name());
                documentJSONObj.addProperty("id", documentsList1.get(i).getDocument_id());
                documentJSONArray.add(documentJSONObj);
            }
        } else {
            ArrayList<VehicleDealerListPojo.DocumentListPojo> documentsList = new ArrayList<>();
            for (int i = 0; i < documentsLayoutsList.size(); i++) {

                if (!((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim().equals("")) {

                    VehicleDealerListPojo.DocumentListPojo documentObj = new VehicleDealerListPojo.DocumentListPojo();
                    if (i < documentsList.size()) {
                        documentObj.setDocument_id(documentId);
                    } else {
                        documentObj.setDocument_id("0");
                    }
                    documentObj.setDocument(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim());
                    documentObj.setDocument_name(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).getText().toString().trim());

                    documentsList.add(documentObj);
                }
            }
            for (int i = 0; i < documentsList.size(); i++) {
                JsonObject documentJSONObj = new JsonObject();
                documentJSONObj.addProperty("photo", documentsList.get(i).getDocument());
                documentJSONObj.addProperty("document_name", documentsList.get(i).getDocument_name());
                documentJSONObj.addProperty("id", documentsList.get(i).getDocument_id());
                documentJSONArray.add(documentJSONObj);
            }
        }

        ArrayList<VehicleDealerListPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        for (int i = 0; i < serviceDatesLayoutsList.size(); i++) {

            if (!((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).getText().toString().trim().equals("")) {
                if (!((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).getText().toString().trim().equals("")) {

                    VehicleDealerListPojo.ServiceDatesListPojo serviceDateObj = new VehicleDealerListPojo.ServiceDatesListPojo();

                    if (i < serviceDatesList.size()) {
                        serviceDateObj.setService_date_id(serviceDateId);
                        //serviceDatesList.setFamily_details_id(clientDetails.getRelation_details().get(i).getFamily_details_id());
                    } else {
                        // clientFamilyObj.setFamily_details_id("0");
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

        JsonArray serviceDatesJSONArray = new JsonArray();

        for (int i = 0; i < serviceDatesList.size(); i++) {
            JsonObject serviceDatesJSONObj = new JsonObject();
            serviceDatesJSONObj.addProperty("date", serviceDatesList.get(i).getService_date());
            serviceDatesJSONObj.addProperty("text", serviceDatesList.get(i).getText());
            serviceDatesJSONObj.addProperty("id", serviceDatesList.get(i).getService_date_id());
            serviceDatesJSONArray.add(serviceDatesJSONObj);
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "linkrtoTovehicle");
        mainObj.addProperty("state", stateId);
        mainObj.addProperty("client_name", clientId);
        mainObj.addProperty("vehicle_owner", edt_vehicleownername.getText().toString().trim());
        mainObj.addProperty("rto_agent", rtoId);
        mainObj.addProperty("v_type", typeId);
        mainObj.addProperty("engine_no", edt_engineno.getText().toString().trim());
        mainObj.addProperty("chassis_no", edt_chassisno.getText().toString().trim());
        mainObj.addProperty("insu_renewal_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_renewaldate.getText().toString().trim()));
        mainObj.addProperty("purchase_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_purcasedate.getText().toString().trim()));
        mainObj.addProperty("insu_policy_no", edt_insurancepolicyno.getText().toString().trim());
        mainObj.addProperty("tem_reg_no", edt_temregno.getText().toString().trim());
        mainObj.addProperty("hypothecated_to", edt_hypothecatedto.getText().toString().trim());
        mainObj.add("documents", documentJSONArray);
        mainObj.addProperty("remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("description", edt_description.getText().toString().trim());
        mainObj.addProperty("vehicle_no", edt_vehicleno.getText().toString().trim());
        mainObj.add("service_dates", serviceDatesJSONArray);
        mainObj.add("other_dates", otherDatesJSONArray);
        mainObj.addProperty("user_id", user_id);
        mainObj.addProperty("id", id);
        mainObj.addProperty("is_show_to_customer", isshowtocustomer);
        mainObj.addProperty("is_show_to_rto", isshowtorto);
        mainObj.addProperty("rtoId", dealerId);
        mainObj.addProperty("rtocreatedby", rtocreatedby);

        mainObj.addProperty("bank_name", edt_bank.getText().toString().trim());
        mainObj.addProperty("branch_name", edt_branch.getText().toString().trim());
        mainObj.addProperty("frequency", frequency);
        mainObj.addProperty("borrower_name", edt_borrowername.getText().toString().trim());
        mainObj.addProperty("loan_amount", edt_loanamount.getText().toString().trim());
        mainObj.addProperty("loan_account_number", edt_accountnumber.getText().toString().trim());
        mainObj.addProperty("installment_amount", edt_installmentanount.getText().toString().trim());
        mainObj.addProperty("date_of_section", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_sactiondate.getText().toString().trim()));
        mainObj.addProperty("start_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_startdate.getText().toString().trim()));

        mainObj.addProperty("end_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_enddate.getText().toString().trim()));

        mainObj.addProperty("vehicle_image", edt_selectVehicleImage.getText().toString().trim());


        // Log.i("LifeInsuranceJson", mainObj.toString());

        if (Utilities.isInternetAvailable(context)) {
            new UpdatevehicleDealerDetails().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }


    }

    public class UpdatevehicleDealerDetails extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.LINKAPI, params[0]);
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
                        builder.setMessage("Vehicle Details Updated Successfully.");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(context, MainDrawer_Activity.class);
                                i.putExtra("linking", "linking");
                                startActivity(i);
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
                            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
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

    public void selectVehicleImage(View view) {
        documentType = "vehicleImage";
        if (doesAppNeedPermissions()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, 1);
                return;
            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    edt_selectVehicleImage = (EditText) view;
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
                                        .pickFile(Link_Edit_Dealer.this);
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
                edt_selectVehicleImage = (EditText) view;
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
                                    .pickFile(Link_Edit_Dealer.this);
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
                                        .pickFile(Link_Edit_Dealer.this);
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
                                    .pickFile(Link_Edit_Dealer.this);
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Link_Edit_Dealer.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Link_Edit_Dealer.this);
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
                        } else if (documentType.equals("vehicleImage")) {
                            edt_selectVehicleImage.setText(document_name);
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

}

