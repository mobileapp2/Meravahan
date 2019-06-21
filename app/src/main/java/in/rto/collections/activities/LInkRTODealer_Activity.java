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
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.models.VehicleDealerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class LInkRTODealer_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private TextView edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_vehicledealer,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_taxvalidupto, edt_permitvalidupto, edt_remark, edt_satepermitvalidupto, nationalpermitvalidupto,
            pucrenewaldate, fitnessvalidupto, edt_selectVehicleImage;
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
    private int mYear4, mMonth4, mDay4;
    private int mYear5, mMonth5, mDay5;
    private int mYear6, mMonth6, mDay6;
    private int mYear7, mMonth7, mDay7;


    private EditText edt_selectdocuments = null, edt_name = null;
    private ImageView img_save;
    private ArrayList<ClientMainListPojo> clientList;
    private List<LinearLayout> documentsLayoutsList;
    private List<LinearLayout> otherLayoutsList;
    private ArrayList<VehicleDealerPojo> vehicleDealerPojos;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String companyAliasName = "";
    private String user_id, stateId, clientId, typeId, dealerId, otherId, documentId, id, createdId = "0";
    private String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File photoFile, rtoagentPicFolder;
    private RTOAgentListPojo rtoAgentListPojo;
    private String isshowtocustomer, isshowtorto;
    private static ArrayList<LinkPojo> linkPojos;
    private LinkPojo linkMainObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_rtodealer);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = LInkRTODealer_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_description = findViewById(R.id.edt_description);
        edt_type = findViewById(R.id.edt_type);
        edt_engineno = findViewById(R.id.edt_engineno);
        edt_chassisno = findViewById(R.id.edt_chassisno);
        edt_insurancepolicyno = findViewById(R.id.edt_insurancepolicyno);
        edt_renewaldate = findViewById(R.id.edt_renewaldate);
        edt_remark = findViewById(R.id.edt_remark);
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        img_save = findViewById(R.id.img_save);
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
        btn_addservicedates = findViewById(R.id.btn_addservicedates);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);
        vehicleimagecard = findViewById(R.id.vehicleimagecard);
        rtoagentPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "RTODaler");
        if (!rtoagentPicFolder.exists())
            rtoagentPicFolder.mkdirs();

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

        mYear4 = cal.get(Calendar.YEAR);
        mMonth4 = cal.get(Calendar.MONTH);
        mDay4 = cal.get(Calendar.DAY_OF_MONTH);

        mYear5 = cal.get(Calendar.YEAR);
        mMonth5 = cal.get(Calendar.MONTH);
        mDay5 = cal.get(Calendar.DAY_OF_MONTH);

        mYear6 = cal.get(Calendar.YEAR);
        mMonth6 = cal.get(Calendar.MONTH);
        mDay6 = cal.get(Calendar.DAY_OF_MONTH);

        mYear7 = cal.get(Calendar.YEAR);
        mMonth7 = cal.get(Calendar.MONTH);
        mDay7 = cal.get(Calendar.DAY_OF_MONTH);


        typelist = new ArrayList<>();
        clientList = new ArrayList<>();
        statelist = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        rtoAgentListPojo = (RTOAgentListPojo) getIntent().getSerializableExtra("rtoDetails");
        id = rtoAgentListPojo.getId();
        stateId = rtoAgentListPojo.getStateId();
        typeId = rtoAgentListPojo.getType_id();
        createdId = rtoAgentListPojo.getCreated_by();

        // edt_clientname.setText(customerPojo.getClient_name());
        edt_chassisno.setText(rtoAgentListPojo.getChassis_no());
        edt_state.setText(rtoAgentListPojo.getStateName());
        edt_description.setText(rtoAgentListPojo.getDescription());
        edt_renewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", rtoAgentListPojo.getInsurance_renewal_date()));

        edt_engineno.setText(rtoAgentListPojo.getEngine_no());
        edt_insurancepolicyno.setText(rtoAgentListPojo.getInsurance_policy_no());
        edt_remark.setText(rtoAgentListPojo.getRemark());
        edt_type.setText(rtoAgentListPojo.getType_name());
        edt_vehicleownername.setText(rtoAgentListPojo.getVehicle_owner());
        edt_description.setText(rtoAgentListPojo.getDescription());
        edt_vehicleno.setText(rtoAgentListPojo.getVehicle_no());
        edt_selectVehicleImage.setText(rtoAgentListPojo.getVehicle_image());
        ArrayList<RTOAgentListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        otherDatesList = rtoAgentListPojo.getOther_date();

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

            }
        } else {
            //tv_ser.setText("No Maturity Dates Added");
        }


        ArrayList<RTOAgentListPojo.DocumentListPojo> documentsList = new ArrayList<>();
        documentsList = rtoAgentListPojo.getDocument();

        if (documentsList.size() != 0) {
            for (int i = 0; i < documentsList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_viewdocuments, null);
                documentsLayoutsList.add((LinearLayout) rowView);
                ll_documents.addView(rowView, ll_documents.getChildCount());
//                Uri uri = Uri.parse(documentsList.get(i).getDocument());
//                String document_name = uri.getLastPathSegment();
//                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_documentname)).setText(document_name);
                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).setText(documentsList.get(i).getDoc_name());
                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).setText(documentsList.get(i).getName());

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
                    linkMainObj.setChassis_no(rtoAgentListPojo.getChassis_no());
                }

                if (chkvehicleimage.isChecked()) {
                    linkMainObj.setVehicle_image(rtoAgentListPojo.getVehicle_image());
                    linkMainObj.setVehicle_image_url(rtoAgentListPojo.getVehicle_image_url());
                }
                if (chkvehicleownername.isChecked()) {
                    linkMainObj.setVehicle_owner(rtoAgentListPojo.getVehicle_owner());
                }
                if (chkvehicleno.isChecked()) {
                    linkMainObj.setVehicle_no(rtoAgentListPojo.getVehicle_no());
                }

                if (chktype.isChecked()) {
                    linkMainObj.setType_id(rtoAgentListPojo.getType_id());
                    linkMainObj.setType_name(rtoAgentListPojo.getType_name());
                }
                if (chkrenewaldate.isChecked()) {
                    linkMainObj.setInsurance_renewal_date(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy", rtoAgentListPojo.getInsurance_renewal_date()));
                }
                if (chkremark.isChecked()) {
                    linkMainObj.setRemark(rtoAgentListPojo.getRemark());
                }
                if (chkpolicyno.isChecked()) {
                    linkMainObj.setInsurance_policy_no(rtoAgentListPojo.getInsurance_policy_no());
                }
                if (chkengineno.isChecked()) {
                    linkMainObj.setEngine_no(rtoAgentListPojo.getEngine_no());
                }
                if (chkdescription.isChecked()) {
                    linkMainObj.setDescription(rtoAgentListPojo.getDescription());
                }
                if (chkname.isChecked()) {
                    linkMainObj.setState_id(rtoAgentListPojo.getStateId());
                    linkMainObj.setStateName(rtoAgentListPojo.getStateName());
                }

                if (chkother.isChecked()) {
                    ArrayList<RTOAgentListPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();
                    otherDatesListPojos = rtoAgentListPojo.getOther_date();
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
                    ArrayList<RTOAgentListPojo.DocumentListPojo> documentListPojos = new ArrayList<>();
                    documentListPojos = rtoAgentListPojo.getDocument();
                    ArrayList<LinkPojo.DocumentListPojo> documentsList = new ArrayList<>();

                    for (int j = 0; j < documentListPojos.size(); j++) {
                        LinkPojo.DocumentListPojo documentObj = new LinkPojo.DocumentListPojo();
                        documentObj.setDocument(documentListPojos.get(j).getDocument());
                        documentObj.setDocument_name(documentListPojos.get(j).getDoc_name());
                        documentObj.setOriginal_name(documentListPojos.get(j).getName());
                        documentsList.add(documentObj);
                    }
                    linkMainObj.setDocument(documentsList);

                }

                Intent intent = new Intent(context, LinkToRTO_Activity.class);
                intent.putExtra("vehicleDetails", linkMainObj);
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
