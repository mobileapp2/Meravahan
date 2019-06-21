package in.rto.collections.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
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

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class LinkCustomerOther_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents,ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments,btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private TextView edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_rtoagent,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_purcasedate, edt_temregno, edt_remark,edt_hypothecatedto,edt_taxvalidupto,
            edt_permitvalidupto,edt_satepermitvalidupto,nationalpermitvalidupto,pucrenewaldate,fitnessvalidupto,edt_selectVehicleImage;

    private CheckBox chkname, chkvehicleno, chkvehicleownername, chkrtoname,chkdescription,
            chktype, chkengineno, chkchassisno, chkpolicyno, chkrenewaldate, chkroadtax,
            chkpurchase,chktemreg,chkhypothecated,chkremark,chkservice,chkother,chkdocument,chkvehicleimage;
    private CardView statecard, vehiclenocard, ownernamecard, rtonamecard, vehicledealercard,descriptioncard,
            typecard, enginenocard, chassisnocard, policynocard, renewaldatecard, roadtaxcard,
            permitcard, statepermitcard, nationalpermitcard,puccard,fitnesscard,purchasecard,temregcard,hypothecatedcard,remarkcard,servicecard,
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
        setContentView(R.layout.activity_link_customer_other);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }
    private void init() {
        context = LinkCustomerOther_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_description = findViewById(R.id.edt_description);
        edt_remark = findViewById(R.id.edt_remark);
        ll_servicedates = findViewById(R.id.ll_servicedates);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        img_save =findViewById(R.id.img_save);
        chkname = findViewById(R.id.chkstate);
        chkdescription = findViewById(R.id.chkdescription);
        chkremark = findViewById(R.id.chkremark);
        chkother = findViewById(R.id.chkother);
        chkservice = findViewById(R.id.chkservice);
        chkvehicleno = findViewById(R.id.chkvehicleno);
        chkvehicleownername = findViewById(R.id.chkvehicleownername);
        chkdocument = findViewById(R.id.chkdocument);
        ll_documents = findViewById(R.id.ll_documents);

        statecard = findViewById(R.id.statecard);
        vehiclenocard = findViewById(R.id.vehiclenocard);
        descriptioncard = findViewById(R.id.descriptioncard);
        documentcard = findViewById(R.id.documentcard);
        remarkcard = findViewById(R.id.remarkcard);
        othercard = findViewById(R.id.othercard);
        servicecard = findViewById(R.id.servicecard);
        vehiclenocard = findViewById(R.id.vehiclenocard);
        ownernamecard = findViewById(R.id.ownercard);



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
        edt_state.setText(customerPojo.getStateName());
        edt_description.setText(customerPojo.getDescription());
        edt_remark.setText(customerPojo.getRemark());
        edt_vehicleownername.setText(customerPojo.getVehicle_owner());
        edt_description.setText(customerPojo.getDescription());
        edt_vehicleno.setText(customerPojo.getVehicle_no());
        ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        serviceDatesList = customerPojo.getService_date();

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
        servicecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkservice.isChecked()) {
                    chkservice.setChecked(false);
                    servicecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkservice.setChecked(true);
                    servicecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
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

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkPojos = new ArrayList<LinkPojo>();
                linkMainObj = new LinkPojo();
                linkMainObj.setDealerId(id);
                linkMainObj.setCreated_by(createdId);

                if(chkvehicleownername.isChecked()){
                    linkMainObj.setVehicle_owner(customerPojo.getVehicle_owner());
                }
                if(chkvehicleno.isChecked()){
                    linkMainObj.setVehicle_no(customerPojo.getVehicle_no());
                }
                if(chkremark.isChecked()){
                    linkMainObj.setRemark(customerPojo.getRemark());
                }

                if(chkdescription.isChecked()){
                    linkMainObj.setDescription(customerPojo.getDescription());
                }
                if(chkname.isChecked()){
                    linkMainObj.setState_id(customerPojo.getStateId());
                    linkMainObj.setStateName(customerPojo.getStateName());
                }

                if(chkservice.isChecked()){
                    ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
                    serviceDatesList = customerPojo.getService_date();
                    ArrayList<LinkPojo.ServiceDatesListPojo> serviceDatesListPojos = new ArrayList<>();

                    for (int j = 0; j < serviceDatesList.size(); j++) {
                        LinkPojo.ServiceDatesListPojo servicedateobj = new LinkPojo.ServiceDatesListPojo();
                        servicedateobj.setService_date(changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy",serviceDatesList.get(j).getService_date()));
                        servicedateobj.setText(serviceDatesList.get(j).getText());
                        servicedateobj.setService_date_id(serviceDatesList.get(j).getText());
                        serviceDatesListPojos.add(servicedateobj);
                    }
                    linkMainObj.setService_date(serviceDatesListPojos);
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

                Intent intent = new Intent(context, LinkToCustomer_Activity.class);
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
