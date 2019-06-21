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

public class LinkCustomerBanker_Activity extends Activity {
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
            edt_satepermitvalidupto,nationalpermitvalidupto,pucrenewaldate,fitnessvalidupto,edt_bank, edt_branch, edt_borrowername,
            edt_loanamount, edt_accountnumber, edt_sactiondate, edt_installmentanount, edt_startdate,
            edt_enddate, edt_frequency,edt_selectVehicleImage;

    private CheckBox chkname, chkvehicleno, chkvehicleownername, chkvehicledealer,chkdescription,
            chktype, chkengineno, chkchassisno, chkpolicyno, chkrenewaldate, chkroadtax,
            chkpermit, chkstatepermit, chknationalpermit,chkpuc,chkfitness,
            chkremark,chkother,chkdocument,chkbank, chkbranch, chkborrowername,
            chkloanamount, chkaccountnumber, chksactiondate, chkinstallmentanount, chkstartdate,
            chkenddate, chkfrequency,chkvehicleimage;
    private CardView statecard, vehiclenocard, ownernamecard, rtonamecard, vehicledealercard,descriptioncard,
            typecard, enginenocard, chassisnocard, policynocard, renewaldatecard, roadtaxcard,
            permitcard, statepermitcard, nationalpermitcard,puccard,fitnesscard,purchasecard,
            temregcard,hypothecatedcard,remarkcard,servicecard,
            othercard,documentcard,bankcard, branchcard, borrowernamecard,
            loanamountcard, accountnumbercard, sactiondatecard, installmentanountcard, startdatecard,
            enddatecard, frequencycard,vehicleimagecard;

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
    private String user_id, stateId, clientId, typeId,rtoId,statename,id,serviceDateId,otherDateId,createdId,bankId="0",branchId="0",frequency="0";
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File photoFile, customerPicFolder;
    private CustomerPojo customerPojo;
    private static  ArrayList<LinkPojo> linkPojos;
    private LinkPojo linkMainObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_customer_banker);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = LinkCustomerBanker_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_vehicledealer = findViewById(R.id.edt_vehicledealer);
        edt_description = findViewById(R.id.edt_description);
        edt_remark = findViewById(R.id.edt_remark);
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
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);
        ll_documents = findViewById(R.id.ll_documents);
        img_save =findViewById(R.id.img_save);


        chkdescription = findViewById(R.id.chkdescription);
        chkdocument = findViewById(R.id.chkdocument);
        chkremark = findViewById(R.id.chkremark);
        chkvehicledealer = findViewById(R.id.chkvehicledealer);
        chkvehicleno = findViewById(R.id.chkvehicleno);
        chkvehicleownername = findViewById(R.id.chkvehicleownername);
        chkvehicleimage = findViewById(R.id.chkvehicleimage);

        chkbank = findViewById(R.id.chkstate);
        chkbranch = findViewById(R.id.chkbranch);
        chkborrowername = findViewById(R.id.chkborrowername);
        chksactiondate = findViewById(R.id.chkdateofsaction);
        chkloanamount = findViewById(R.id.chkloanamount);
        chkaccountnumber = findViewById(R.id.chkaccountno);
        chkinstallmentanount = findViewById(R.id.chkinstallmentamount);
        chkstartdate = findViewById(R.id.chkstartdate);
        chkenddate = findViewById(R.id.chkenddate);
        chkfrequency = findViewById(R.id.chkfrequency);
        ll_documents = findViewById(R.id.ll_documents);


        vehiclenocard = findViewById(R.id.vehiclenocard);
        descriptioncard = findViewById(R.id.descriptioncard);
        documentcard = findViewById(R.id.documentcard);
        remarkcard = findViewById(R.id.remarkcard);
        vehicledealercard = findViewById(R.id.vehicledealercard);
        ownernamecard = findViewById(R.id.ownercard);
        bankcard  = findViewById(R.id.bankcard);
        branchcard = findViewById(R.id.branchcard);
        borrowernamecard = findViewById(R.id.borrowernamecard);
        sactiondatecard = findViewById(R.id.dateofsactioncard);
        loanamountcard = findViewById(R.id.loanamountcard);
        accountnumbercard = findViewById(R.id.accountnocard);
        installmentanountcard = findViewById(R.id.installmentamountcard);
        startdatecard = findViewById(R.id.startdatecard);
        enddatecard = findViewById(R.id.enddatecard);
        frequencycard = findViewById(R.id.frequencycard);
        ll_documents = findViewById(R.id.ll_documents);
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

        frequency = customerPojo.getFrequency_id();

        // edt_clientname.setText(customerPojo.getClient_name());
        edt_bank.setText(customerPojo.getBank_name());
        edt_branch.setText(customerPojo.getBranch_name());
        edt_description.setText(customerPojo.getDescription());
        edt_sactiondate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",customerPojo.getDate_to_section()));

        edt_startdate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                customerPojo.getInstallment_start_date()));

        edt_enddate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", customerPojo.getInstallment_end_date()));

        edt_borrowername.setText(customerPojo.getBorrower_name());
        edt_frequency.setText(customerPojo.getFrequency());
        edt_loanamount.setText(customerPojo.getLoan_amount());
        edt_remark.setText(customerPojo.getRemark());
        edt_vehicledealer.setText(customerPojo.getVehicle_dealer_name());
        // edt_temregno.setText(customerPojo.getTem_reg_no());
        edt_accountnumber.setText(customerPojo.getLoan_account_number());
        edt_installmentanount.setText(customerPojo.getInstallment_amount());
        edt_vehicleownername.setText(customerPojo.getVehicle_owner());
        //edt_description.setText(customerPojo.getDescription());
        edt_vehicleno.setText(customerPojo.getVehicle_no());
        edt_selectVehicleImage.setText(customerPojo.getVehicle_image());

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
        bankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkbank.isChecked()) {
                    chkbank.setChecked(false);
                    bankcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkbank.setChecked(true);
                    bankcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        branchcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkbranch.isChecked()) {
                    chkbranch.setChecked(false);
                    branchcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkbranch.setChecked(true);
                    branchcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        borrowernamecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkborrowername.isChecked()) {
                    chkborrowername.setChecked(false);
                    borrowernamecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkborrowername.setChecked(true);
                    borrowernamecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });

        installmentanountcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkinstallmentanount.isChecked()) {
                    chkinstallmentanount.setChecked(false);
                    installmentanountcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkinstallmentanount.setChecked(true);
                    installmentanountcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });

        loanamountcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkloanamount.isChecked()) {
                    chkloanamount.setChecked(false);
                    loanamountcard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkloanamount.setChecked(true);
                    loanamountcard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        accountnumbercard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkaccountnumber.isChecked()) {
                    chkaccountnumber.setChecked(false);
                    accountnumbercard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkaccountnumber.setChecked(true);
                    accountnumbercard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        sactiondatecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chksactiondate.isChecked()) {
                    chksactiondate.setChecked(false);
                    sactiondatecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chksactiondate.setChecked(true);
                    sactiondatecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
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
        startdatecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkstartdate.isChecked()) {
                    chkstartdate.setChecked(false);
                    startdatecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkstartdate.setChecked(true);
                    startdatecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        enddatecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkenddate.isChecked()) {
                    chkenddate.setChecked(false);
                    enddatecard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkenddate.setChecked(true);
                    enddatecard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
        frequencycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkfrequency.isChecked()) {
                    chkfrequency.setChecked(false);
                    frequencycard.setBackgroundColor(getResources().getColor(android.R.color.white));
                }else{
                    chkfrequency.setChecked(true);
                    frequencycard.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
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
                if(chkvehicleimage.isChecked()) {
                    linkMainObj.setVehicle_image(customerPojo.getVehicle_image());
                    linkMainObj.setVehicle_image_url(customerPojo.getVehicle_image_url());
                }
                if(chkbank.isChecked()) {
                    linkMainObj.setBank_name(customerPojo.getBank_name());
                }

                if(chkfrequency.isChecked()) {
                    linkMainObj.setFrequency(customerPojo.getFrequency());
                    linkMainObj.setFrequency_id(customerPojo.getFrequency_id());
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

                if(chkbranch.isChecked()){
                    linkMainObj.setBranch_name(customerPojo.getBranch_name());
                }

                if(chkborrowername.isChecked()){
                    linkMainObj.setBorrower_name(customerPojo.getBorrower_name());
                }
                if(chkloanamount.isChecked()){
                    linkMainObj.setLoan_amount(customerPojo.getLoan_amount());
                }

                if(chkaccountnumber.isChecked()){
                    linkMainObj.setLoan_account_number(customerPojo.getLoan_account_number());
                }
                if(chkremark.isChecked()){
                    linkMainObj.setRemark(customerPojo.getRemark());
                }

                if(chksactiondate.isChecked()){
                    if(customerPojo.getDate_to_section() == null){
                        linkMainObj.setDate_to_section("");
                    } else {
                        linkMainObj.setDate_to_section(changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy", customerPojo.getDate_to_section()));
                    }
                }
                if(chkstartdate.isChecked()){
                    if(customerPojo.getInstallment_start_date() == null){
                        linkMainObj.setInstallment_start_date("");
                    } else {
                        linkMainObj.setInstallment_start_date(changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy", customerPojo.getInstallment_start_date()));
                    }
                }
                if(chkenddate.isChecked()){
                    if(customerPojo.getInstallment_end_date() == null){
                        linkMainObj.setInstallment_end_date("");
                    } else {
                        linkMainObj.setInstallment_end_date(changeDateFormat("yyyy-MM-dd",
                                "dd/MM/yyyy", customerPojo.getInstallment_end_date()));
                    }
                }
                if(chkinstallmentanount.isChecked()){
                        linkMainObj.setInstallment_amount(customerPojo.getInstallment_amount());
                }


                if(chkdescription.isChecked()){
                    linkMainObj.setDescription(customerPojo.getDescription());
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
