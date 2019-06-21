package in.rto.collections.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.RTOAgentPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.models.VehicleDealerListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class LInkDealerRTO_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private TextView edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_rtoagent,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_purcasedate, edt_temregno, edt_remark, edt_hypothecatedto, edt_selectVehicleImage;
    private CheckBox chkname, chkvehicleno, chkvehicleownername, chkrtoname, chkdescription,
            chktype, chkengineno, chkchassisno, chkpolicyno, chkrenewaldate, chkroadtax,
            chkpurchase, chktemreg, chkhypothecated, chkremark, chkservice, chkother, chkdocument, chkvehicleimage;
    private CardView statecard, vehiclenocard, ownernamecard, rtonamecard, vehicledealercard, descriptioncard,
            typecard, enginenocard, chassisnocard, policynocard, renewaldatecard, roadtaxcard,
            permitcard, statepermitcard, nationalpermitcard, puccard, fitnesscard, purchasecard, temregcard, hypothecatedcard, remarkcard, servicecard,
            othercard, documentcard, vehicleimagecard;

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
    private ArrayList<RTOAgentPojo> rtoagentlist;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String companyAliasName = "";
    private String user_id, stateId, clientId = "0", typeId, rtoId, id, serviceDateId = "0", otherDateId = "0", documentId, createdId = "0";
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private VehicleDealerListPojo vehicaldealerdetails;
    private File photoFile, vehicledealerPicFolder;
    private CheckBox isshow_to_rto, isshow_to_customer;
    private String isshowtocustomer, isshowtorto;
    private static ArrayList<LinkPojo> linkPojos;
    private LinkPojo linkMainObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_dealer_rto);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    public void init() {
        context = LInkDealerRTO_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
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
        img_save = findViewById(R.id.img_save);
        btn_addservicedates = findViewById(R.id.btn_addservicedates);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);
        chkname = findViewById(R.id.chkstate);
        chkchassisno = findViewById(R.id.chkchassisno);
        chkdescription = findViewById(R.id.chkdescription);
        chkengineno = findViewById(R.id.chkengineno);
        chkhypothecated = findViewById(R.id.chkhypothecated);
        chkpolicyno = findViewById(R.id.chkpolicyno);
        chkdocument = findViewById(R.id.chkdocument);
        chkremark = findViewById(R.id.chkremark);
        chkrenewaldate = findViewById(R.id.chkrenewaldate);
        chktemreg = findViewById(R.id.chktemreg);
        chktype = findViewById(R.id.chktype);
        chkother = findViewById(R.id.chkother);
        chkroadtax = findViewById(R.id.chkroadtax);
        chkrtoname = findViewById(R.id.chkrtoname);
        chkservice = findViewById(R.id.chkservice);
        chkvehicleno = findViewById(R.id.chkvehicleno);
        chkvehicleownername = findViewById(R.id.chkvehicleownername);
        ll_documents = findViewById(R.id.ll_documents);
        chkvehicleimage = findViewById(R.id.chkvehicleimage);
        statecard = findViewById(R.id.statecard);
        vehiclenocard = findViewById(R.id.vehiclenocard);
        chassisnocard = findViewById(R.id.chassisnocard);
        descriptioncard = findViewById(R.id.descriptioncard);
        enginenocard = findViewById(R.id.enginenocard);
        chassisnocard = findViewById(R.id.chassisnocard);
        fitnesscard = findViewById(R.id.fitnesscard);
        hypothecatedcard = findViewById(R.id.hypothecatedcard);
        nationalpermitcard = findViewById(R.id.nationalpermitcard);
        statepermitcard = findViewById(R.id.statepermitcard);
        permitcard = findViewById(R.id.permitcard);
        policynocard = findViewById(R.id.policynocard);
        documentcard = findViewById(R.id.documentcard);
        remarkcard = findViewById(R.id.remarkcard);
        renewaldatecard = findViewById(R.id.renewaldatecard);
        temregcard = findViewById(R.id.temregcard);
        typecard = findViewById(R.id.typecard);
        othercard = findViewById(R.id.othercard);
        roadtaxcard = findViewById(R.id.roadtaxcard);
        puccard = findViewById(R.id.puccard);
        rtonamecard = findViewById(R.id.rtonamecard);
        servicecard = findViewById(R.id.servicecard);
        vehicledealercard = findViewById(R.id.vehicledealercard);
        vehiclenocard = findViewById(R.id.vehiclenocard);
        ownernamecard = findViewById(R.id.ownercard);
        vehicleimagecard = findViewById(R.id.vehicleimagecard);
        vehicledealerPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Vehicle_dealer");
        if (!vehicledealerPicFolder.exists())
            vehicledealerPicFolder.mkdirs();
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
        rtoagentlist = new ArrayList<>();
        serviceDatesLayoutsList = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        vehicaldealerdetails = (VehicleDealerListPojo) getIntent().getSerializableExtra("vehicleDetails");

        id = vehicaldealerdetails.getId();
        stateId = vehicaldealerdetails.getStateId();
        clientId = vehicaldealerdetails.getClient_id();
        typeId = vehicaldealerdetails.getType_id();
        rtoId = vehicaldealerdetails.getRto_agent_id();
        createdId = vehicaldealerdetails.getCreated_by();


        edt_chassisno.setText(vehicaldealerdetails.getChassis_no());
        edt_state.setText(vehicaldealerdetails.getStateName());
        edt_description.setText(vehicaldealerdetails.getDescription());
        edt_renewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", vehicaldealerdetails.getInsurance_renewal_date()));

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

        //edt_insurancepolicyno.setText(vehicaldealerdetails.getVehicle_no());
        ArrayList<VehicleDealerListPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        serviceDatesList = vehicaldealerdetails.getService_date();

        if (serviceDatesList.size() != 0) {
            for (int i = 0; i < serviceDatesList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_viewservicedates, null);
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


        ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        otherDatesList = vehicaldealerdetails.getOther_date();

        if (otherDatesList.size() != 0) {
            for (int i = 0; i < otherDatesList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_viewotherdates, null);
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
                final View rowView = inflater.inflate(R.layout.add_layout_viewdocuments, null);
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

    public void setEventHandler() {
        chassisnocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkchassisno.isChecked()) {
                    chkchassisno.setChecked(false);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkchassisno.setChecked(true);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        statecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkname.isChecked()) {
                    chkname.setChecked(false);
                    statecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkname.setChecked(true);
                    statecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        vehiclenocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkvehicleno.isChecked()) {
                    chkvehicleno.setChecked(false);
                    vehiclenocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkvehicleno.setChecked(true);
                    vehiclenocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        ownernamecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkvehicleownername.isChecked()) {
                    chkvehicleownername.setChecked(false);
                    ownernamecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkvehicleownername.setChecked(true);
                    ownernamecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        rtonamecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkrtoname.isChecked()) {
                    chkrtoname.setChecked(false);
                    rtonamecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkrtoname.setChecked(true);
                    rtonamecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });

        descriptioncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkdescription.isChecked()) {
                    chkdescription.setChecked(false);
                    descriptioncard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkdescription.setChecked(true);
                    descriptioncard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        typecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chktype.isChecked()) {
                    chktype.setChecked(false);
                    typecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chktype.setChecked(true);
                    typecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        enginenocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkengineno.isChecked()) {
                    chkengineno.setChecked(false);
                    enginenocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkengineno.setChecked(true);
                    enginenocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        chassisnocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkchassisno.isChecked()) {
                    chkchassisno.setChecked(false);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkchassisno.setChecked(true);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        policynocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkpolicyno.isChecked()) {
                    chkpolicyno.setChecked(false);
                    policynocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkpolicyno.setChecked(true);
                    policynocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        renewaldatecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkrenewaldate.isChecked()) {
                    chkrenewaldate.setChecked(false);
                    renewaldatecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkrenewaldate.setChecked(true);
                    renewaldatecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });


        hypothecatedcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkhypothecated.isChecked()) {
                    chkhypothecated.setChecked(false);
                    hypothecatedcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkhypothecated.setChecked(true);
                    hypothecatedcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        temregcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chktemreg.isChecked()) {
                    chktemreg.setChecked(false);
                    temregcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chktemreg.setChecked(true);
                    temregcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        remarkcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkremark.isChecked()) {
                    chkremark.setChecked(false);
                    remarkcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkremark.setChecked(true);
                    remarkcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });

        othercard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkother.isChecked()) {
                    chkother.setChecked(false);
                    othercard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkother.setChecked(true);
                    othercard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        documentcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkdocument.isChecked()) {
                    chkdocument.setChecked(false);
                    documentcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkdocument.setChecked(true);
                    documentcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        vehicleimagecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkvehicleimage.isChecked()) {
                    chkvehicleimage.setChecked(false);
                    vehicleimagecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    chkvehicleimage.setChecked(true);
                    vehicleimagecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkPojos = new ArrayList<LinkPojo>();
                linkMainObj = new LinkPojo();
                linkMainObj.setDealerId(id);
                linkMainObj.setCreated_by(createdId);

                if (chkchassisno.isChecked()) {
                    linkMainObj.setChassis_no(vehicaldealerdetails.getChassis_no());
                }
                if (chkvehicleimage.isChecked()) {
                    linkMainObj.setVehicle_image(vehicaldealerdetails.getVehicle_image());
                    linkMainObj.setVehicle_image_url(vehicaldealerdetails.getVehicle_image_url());
                }

                if (chkvehicleownername.isChecked()) {
                    linkMainObj.setVehicle_owner(vehicaldealerdetails.getVehicle_owner());
                }
                if (chkvehicleno.isChecked()) {
                    linkMainObj.setVehicle_no(vehicaldealerdetails.getVehicle_no());
                }

                if (chktype.isChecked()) {
                    linkMainObj.setType_id(vehicaldealerdetails.getType_id());
                    linkMainObj.setType_name(vehicaldealerdetails.getType_name());
                }
                if (chkrenewaldate.isChecked()) {
                    linkMainObj.setInsurance_renewal_date(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy", vehicaldealerdetails.getInsurance_renewal_date()));
                }
                if (chkremark.isChecked()) {
                    linkMainObj.setRemark(vehicaldealerdetails.getRemark());
                }
                if (chkpolicyno.isChecked()) {
                    linkMainObj.setInsurance_policy_no(vehicaldealerdetails.getInsurance_policy_no());
                }
                if (chkengineno.isChecked()) {
                    linkMainObj.setEngine_no(vehicaldealerdetails.getEngine_no());
                }
                if (chkdescription.isChecked()) {
                    linkMainObj.setDescription(vehicaldealerdetails.getDescription());
                }
                if (chkname.isChecked()) {
                    linkMainObj.setState_id(vehicaldealerdetails.getStateId());
                    linkMainObj.setStateName(vehicaldealerdetails.getStateName());
                }

                if (chkother.isChecked()) {
                    ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();
                    otherDatesListPojos = vehicaldealerdetails.getOther_date();
                    ArrayList<LinkPojo.OtherDatesListPojo> otherDatesListPojoArrayList = new ArrayList<>();
                    for (int j = 0; j < otherDatesListPojos.size(); j++) {
                        LinkPojo.OtherDatesListPojo otherdateobj = new LinkPojo.OtherDatesListPojo();
                        otherdateobj.setOther_date(changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy", otherDatesListPojos.get(j).getOther_date()));
                        otherdateobj.setText(otherDatesListPojos.get(j).getText());
                        otherdateobj.setOther_date_id(otherDatesListPojos.get(j).getOther_date_id());
                        otherDatesListPojoArrayList.add(otherdateobj);
                    }
                    linkMainObj.setOther_date(otherDatesListPojoArrayList);
                }

                if (chkdocument.isChecked()) {
                    ArrayList<VehicleDealerListPojo.DocumentListPojo> documentListPojos = new ArrayList<>();
                    documentListPojos = vehicaldealerdetails.getDocument();
                    ArrayList<LinkPojo.DocumentListPojo> documentsList = new ArrayList<>();

                    for (int j = 0; j < documentListPojos.size(); j++) {
                        LinkPojo.DocumentListPojo documentObj = new LinkPojo.DocumentListPojo();
                        documentObj.setDocument(documentListPojos.get(j).getDocument());
                        documentObj.setDocument_name(documentListPojos.get(j).getDocument_name());
                        documentObj.setOriginal_name(documentListPojos.get(j).getOriginal_name());
                        documentsList.add(documentObj);
                    }
                    linkMainObj.setDocument(documentsList);
                }

                Intent intent = new Intent(context, LinkToDealer_Activity.class);
                intent.putExtra("rtoDetails", linkMainObj);
                //intent.putExtra("cuggg",customerPojo);
                context.startActivity(intent);


            }
        });

    }

    public void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Linking");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
