package in.rto.collections.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.RTOAgentPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

import static android.graphics.Color.WHITE;
import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class LinkCustomerRto_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents,ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments,btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private TextView edt_state, edt_vehicleno,  edt_vehicleownername,edt_vehicledealer,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate
    , edt_temregno, edt_remark,edt_hypothecatedto,edt_taxvalidupto, edt_permitvalidupto,
            edt_satepermitvalidupto,nationalpermitvalidupto,pucrenewaldate,fitnessvalidupto,edt_selectVehicleImage;

    private CheckBox chkname, chkvehicleno, chkvehicleownername, chkvehicledealer,chkdescription,
            chktype, chkengineno, chkchassisno, chkpolicyno, chkrenewaldate, chkroadtax,
            chkpermit, chkstatepermit, chknationalpermit,chkpuc,chkfitness,
            chkremark,chkother,chkdocument,chkvehicleimage;
    private CardView statecard, vehiclenocard, ownernamecard, rtonamecard, vehicledealercard,descriptioncard,
            typecard, enginenocard, chassisnocard, policynocard, renewaldatecard, roadtaxcard,
            permitcard, statepermitcard, nationalpermitcard,puccard,fitnesscard,purchasecard,
            temregcard,hypothecatedcard,remarkcard,servicecard,
            othercard,documentcard,vehicleimagecard;

    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private int mYear3, mMonth3, mDay3;
    private int mYear4, mMonth4, mDay4;
    private int mYear5, mMonth5, mDay5;
    private int mYear6, mMonth6, mDay6;
    private int mYear7, mMonth7, mDay7;
    private int mYear8, mMonth8, mDay8;
    private int mYear9, mMonth9, mDay9;
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
    private String user_id, stateId, clientId, typeId,rtoId,statename,id,serviceDateId,otherDateId,createdId;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File photoFile, customerPicFolder;
    private CustomerPojo customerPojo;
    private static  ArrayList<LinkPojo> linkPojos;
    private LinkPojo linkMainObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_customer_rto);

        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }
    private void init() {
        context = LinkCustomerRto_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_vehicledealer = findViewById(R.id.edt_vehicledealer);
        edt_description = findViewById(R.id.edt_description);
        edt_type = findViewById(R.id.edt_type);
        edt_engineno = findViewById(R.id.edt_engineno);
        edt_chassisno = findViewById(R.id.edt_chassisno);
        edt_insurancepolicyno = findViewById(R.id.edt_insurancepolicyno);
        edt_renewaldate = findViewById(R.id.edt_renewaldate);
        edt_remark = findViewById(R.id.edt_remark);
        edt_taxvalidupto = findViewById(R.id.edt_taxpaidupto);
        edt_permitvalidupto = findViewById(R.id.edt_permitvalidupto);
        edt_satepermitvalidupto = findViewById(R.id.edt_statepermitvalidupto);
        nationalpermitvalidupto = findViewById(R.id.edt_nationalpermitvalidupto);
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);
        pucrenewaldate = findViewById(R.id.edt_pucrenewaldate);
        fitnessvalidupto = findViewById(R.id.edt_fitnessvalidupto);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        img_save =findViewById(R.id.img_save);
        chkname = findViewById(R.id.chkstate);
        chkchassisno = findViewById(R.id.chkchassisno);
        chkdescription = findViewById(R.id.chkdescription);
        chkengineno = findViewById(R.id.chkengineno);
        chkchassisno = findViewById(R.id.chkchassisno);
        chkfitness = findViewById(R.id.chkfitness);
        chknationalpermit = findViewById(R.id.chknationalpermit);
        chkstatepermit = findViewById(R.id.chkstatepermit);
        chkpermit = findViewById(R.id.chkpermit);
        chkpolicyno = findViewById(R.id.chkpolicyno);
        chkdocument = findViewById(R.id.chkdocument);
        chkremark = findViewById(R.id.chkremark);
        chkrenewaldate = findViewById(R.id.chkrenewaldate);
        chktype = findViewById(R.id.chktype);
        chkother = findViewById(R.id.chkother);
        chkroadtax = findViewById(R.id.chkroadtax);
        chkpuc = findViewById(R.id.chkpuc);
        chkvehicledealer = findViewById(R.id.chkvehicledealer);
        chkvehicleno = findViewById(R.id.chkvehicleno);
        chkvehicleownername = findViewById(R.id.chkvehicleownername);
        chkvehicleimage = findViewById(R.id.chkvehicleimage);
        ll_documents = findViewById(R.id.ll_documents);

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



        btn_addservicedates = findViewById(R.id.btn_addservicedates);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);

        customerPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Customer");
        if (!customerPicFolder.exists())
            customerPicFolder.mkdirs();

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


        mYear8 = cal.get(Calendar.YEAR);
        mMonth8 = cal.get(Calendar.MONTH);
        mDay8 = cal.get(Calendar.DAY_OF_MONTH);

        mYear9 = cal.get(Calendar.YEAR);
        mMonth9 = cal.get(Calendar.MONTH);
        mDay9 = cal.get(Calendar.DAY_OF_MONTH);

        typelist = new ArrayList<>();
        clientList = new ArrayList<>();
        statelist = new ArrayList<>();
        rtoagentlist = new ArrayList<>();
        serviceDatesLayoutsList = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        customerPojo  = (CustomerPojo) getIntent().getSerializableExtra("customerDetails");
        id = customerPojo.getId();
        stateId = customerPojo.getStateId();
        typeId = customerPojo.getType_id();
        createdId = customerPojo.getCreated_by();

        // edt_clientname.setText(customerPojo.getClient_name());
        edt_chassisno.setText(customerPojo.getChassis_no());
        edt_state.setText(customerPojo.getStateName());
        edt_description.setText(customerPojo.getDescription());
        edt_renewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",customerPojo.getInsurance_renewal_date()));

        edt_taxvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                customerPojo.getTax_paid_up_to()));

        edt_permitvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", customerPojo.getPermit_valid_upto()));
        edt_satepermitvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                customerPojo.getState_permit_valid_upto()));
        nationalpermitvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", customerPojo.getNational_permit_valid_upto()));
        pucrenewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                customerPojo.getPuc_renewal_date()));
        fitnessvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", customerPojo.getFittness_valid_upto()));
        edt_engineno.setText(customerPojo.getEngine_no());
      //  edt_hypothecatedto.setText(customerPojo.getHypothecated_to());
        edt_insurancepolicyno.setText(customerPojo.getInsurance_policy_no());
        edt_remark.setText(customerPojo.getRemark());
        edt_vehicledealer.setText(customerPojo.getVehicle_dealer_name());
       // edt_temregno.setText(customerPojo.getTem_reg_no());
        edt_type.setText(customerPojo.getType_name());
        edt_vehicleownername.setText(customerPojo.getVehicle_owner());
        //edt_description.setText(customerPojo.getDescription());
        edt_vehicleno.setText(customerPojo.getVehicle_no());
        edt_selectVehicleImage.setText(customerPojo.getVehicle_image());

        ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        otherDatesList = customerPojo.getOther_date();

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




        ArrayList< CustomerPojo.DocumentListPojo> documentsList = new ArrayList<>();
        documentsList = customerPojo.getDocument();

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

    public void setEventHandler(){
        permitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkpermit.isChecked()) {
                    chkpermit.setChecked(false);
                    permitcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkpermit.setChecked(true);
                    permitcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        statepermitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkstatepermit.isChecked()) {
                    chkstatepermit.setChecked(false);
                    statepermitcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkstatepermit.setChecked(true);
                    statepermitcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        nationalpermitcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chknationalpermit.isChecked()) {
                    chknationalpermit.setChecked(false);
                    nationalpermitcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chknationalpermit.setChecked(true);
                    nationalpermitcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        chassisnocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkchassisno.isChecked()) {
                    chkchassisno.setChecked(false);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkchassisno.setChecked(true);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        statecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkname.isChecked()) {
                    chkname.setChecked(false);
                    statecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkname.setChecked(true);
                    statecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        vehiclenocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkvehicleno.isChecked()) {
                    chkvehicleno.setChecked(false);
                    vehiclenocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkvehicleno.setChecked(true);
                    vehiclenocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        ownernamecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkvehicleownername.isChecked()) {
                    chkvehicleownername.setChecked(false);
                    ownernamecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkvehicleownername.setChecked(true);
                    ownernamecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });

        vehicledealercard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkvehicledealer.isChecked()) {
                    chkvehicledealer.setChecked(false);
                    vehicledealercard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkvehicledealer.setChecked(true);
                    vehicledealercard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        descriptioncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkdescription.isChecked()) {
                    chkdescription.setChecked(false);
                    descriptioncard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkdescription.setChecked(true);
                    descriptioncard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        typecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chktype.isChecked()) {
                    chktype.setChecked(false);
                    typecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chktype.setChecked(true);
                    typecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        enginenocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkengineno.isChecked()) {
                    chkengineno.setChecked(false);
                    enginenocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkengineno.setChecked(true);
                    enginenocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        chassisnocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkchassisno.isChecked()) {
                    chkchassisno.setChecked(false);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkchassisno.setChecked(true);
                    chassisnocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        policynocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkpolicyno.isChecked()) {
                    chkpolicyno.setChecked(false);
                    policynocard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkpolicyno.setChecked(true);
                    policynocard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        renewaldatecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkrenewaldate.isChecked()) {
                    chkrenewaldate.setChecked(false);
                    renewaldatecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkrenewaldate.setChecked(true);
                    renewaldatecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        roadtaxcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkroadtax.isChecked()) {
                    chkroadtax.setChecked(false);
                    roadtaxcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkroadtax.setChecked(true);
                    roadtaxcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        puccard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkpuc.isChecked()) {
                    chkpuc.setChecked(false);
                    puccard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkpuc.setChecked(true);
                    puccard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        fitnesscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkfitness.isChecked()) {
                    chkfitness.setChecked(false);
                    fitnesscard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkfitness.setChecked(true);
                    fitnesscard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });

        remarkcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkremark.isChecked()) {
                    chkremark.setChecked(false);
                    remarkcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkremark.setChecked(true);
                    remarkcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });

        othercard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkother.isChecked()) {
                    chkother.setChecked(false);
                    othercard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkother.setChecked(true);
                    othercard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        documentcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkdocument.isChecked()) {
                    chkdocument.setChecked(false);
                    documentcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkdocument.setChecked(true);
                    documentcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        vehicleimagecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkvehicleimage.isChecked()) {
                    chkvehicleimage.setChecked(false);
                    vehicleimagecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
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
                linkMainObj.setVehiclertoId(id);
                linkMainObj.setCreated_by(createdId);

                        if(chkchassisno.isChecked()) {
                            linkMainObj.setChassis_no(customerPojo.getChassis_no());
                        }

                if(chkvehicleimage.isChecked()) {
                    linkMainObj.setVehicle_image(customerPojo.getVehicle_image());
                    linkMainObj.setVehicle_image_url(customerPojo.getVehicle_image_url());
                }
                if(chkvehicleownername.isChecked()){
                    linkMainObj.setVehicle_owner(customerPojo.getVehicle_owner());
                }
                if(chkvehicleno.isChecked()){
                    linkMainObj.setVehicle_no(customerPojo.getVehicle_no());
                }
                if(chkvehicledealer.isChecked()){
                    linkMainObj.setVehicle_dealer_name(customerPojo.getVehicle_dealer_name());
                }

                if(chkpuc.isChecked()){
                    linkMainObj.setPuc_renewal_date(customerPojo.getPuc_renewal_date());
                }

                if(chkroadtax.isChecked()){
                    linkMainObj.setTax_paid_up_to(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy",customerPojo.getTax_paid_up_to()));
                }
                if(chktype.isChecked()){
                    linkMainObj.setType_id(customerPojo.getType_id());
                    linkMainObj.setType_name(customerPojo.getType_name());
                }

                if(chkrenewaldate.isChecked()){
                    linkMainObj.setInsurance_renewal_date(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy",customerPojo.getInsurance_renewal_date()));
                }
                if(chkremark.isChecked()){
                    linkMainObj.setRemark(customerPojo.getRemark());
                }
                if(chkpolicyno.isChecked()){
                    linkMainObj.setInsurance_policy_no(customerPojo.getInsurance_policy_no());
                }
                if(chkstatepermit.isChecked()){
                    linkMainObj.setState_permit_valid_upto(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy",customerPojo.getState_permit_valid_upto()));
                }
                if(chkpermit.isChecked()){
                   if(customerPojo.getPermit_valid_upto() == null){
                       linkMainObj.setPermit_valid_upto("");
                   } else {
                       linkMainObj.setPermit_valid_upto(changeDateFormat("yyyy-MM-dd",
                               "dd/MM/yyyy", customerPojo.getPermit_valid_upto()));
                   }
                }
                if(chknationalpermit.isChecked()){
                    linkMainObj.setNational_permit_valid_upto(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy",customerPojo.getNational_permit_valid_upto()));
                }

                if(chkfitness.isChecked()){
                    linkMainObj.setFittness_valid_upto(changeDateFormat("yyyy-MM-dd",
                            "dd/MM/yyyy",customerPojo.getFittness_valid_upto()));
                }
                if(chkengineno.isChecked()){
                    linkMainObj.setEngine_no(customerPojo.getEngine_no());
                }
                if(chkdescription.isChecked()){
                    linkMainObj.setDescription(customerPojo.getDescription());
                }
                if(chkname.isChecked()){
                    linkMainObj.setState_id(customerPojo.getStateId());
                    linkMainObj.setStateName(customerPojo.getStateName());
                }

                if(chkother.isChecked()){
                    ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesListPojos = new ArrayList<>();
                    otherDatesListPojos = customerPojo.getOther_date();
                    ArrayList<LinkPojo.OtherDatesListPojo> otherDatesListPojoArrayList = new ArrayList<>();
                    for (int j = 0; j < otherDatesListPojos.size(); j++) {
                        LinkPojo.OtherDatesListPojo otherdateobj = new LinkPojo.OtherDatesListPojo();
                        otherdateobj.setOther_date(changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy",otherDatesListPojos.get(j).getOther_date()));
                        otherdateobj.setText(otherDatesListPojos.get(j).getText());
                        otherdateobj.setOther_date_id(otherDatesListPojos.get(j).getOther_date_id());
                        otherDatesListPojoArrayList.add(otherdateobj);
                    }
                    linkMainObj.setOther_date(otherDatesListPojoArrayList);
                }

                if(chkdocument.isChecked()){
                    ArrayList<CustomerPojo.DocumentListPojo> documentListPojos = new ArrayList<>();
                    documentListPojos = customerPojo.getDocument();
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

                Intent intent = new Intent(context,LinkToCustomer_Activity.class);
                intent.putExtra("rtoDetails", linkMainObj);
                //intent.putExtra("cuggg",customerPojo);
                context.startActivity(intent);

            }
        });

    }

    public  void  setUpToolbar() {
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
