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
import android.support.v7.widget.SearchView;
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
import in.rto.collections.fragments.Self_Fragment;
import in.rto.collections.models.BankPojo;
import in.rto.collections.models.BranchPojo;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.FrequencyPojo;
import in.rto.collections.models.RTOAgentPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.MultipartUtility;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class ImportVehicleOtherCustomer_Activity extends Activity {

    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_rtoagent, edt_vehicledealer,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_purcasedate, edt_temregno, edt_remark, edt_hypothecatedto, edt_taxvalidupto, edt_permitvalidupto,
            edt_satepermitvalidupto, nationalpermitvalidupto, pucrenewaldate, fitnessvalidupto, edt_bank, edt_branch, edt_borrowername,
            edt_loanamount, edt_accountnumber, edt_sactiondate, edt_installmentanount, edt_startdate,
            edt_enddate, edt_frequency, edt_selectVehicleImage;

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
    private int mYear10, mMonth10, mDay10;
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
    private String companyAliasName = "", documentType;
    private String user_id, stateId, clientId, typeId, rtoId, statename, id, serviceDateId, otherDateId, createdId, bankId = "0", branchId, frequency;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File photoFile, customerPicFolder;
    private CustomerPojo customerPojo;
    private ArrayList<BankPojo> bankList;
    private ArrayList<BranchPojo> branchList;
    private ArrayList<FrequencyPojo> frequencylist;
    private LinearLayout bank_details, bank_feild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_vehicle_other_customer);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = ImportVehicleOtherCustomer_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_rtoagent = findViewById(R.id.edt_rtoagent);
        edt_vehicledealer = findViewById(R.id.edt_vehicledealer);
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
        edt_taxvalidupto = findViewById(R.id.edt_taxpaidupto);
        edt_permitvalidupto = findViewById(R.id.edt_permitvalidupto);
        edt_satepermitvalidupto = findViewById(R.id.edt_statepermitvalidupto);
        nationalpermitvalidupto = findViewById(R.id.edt_nationalpermitvalidupto);
        pucrenewaldate = findViewById(R.id.edt_pucrenewaldate);
        fitnessvalidupto = findViewById(R.id.edt_fitnessvalidupto);
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

        customerPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Customer");
        if (!customerPicFolder.exists())
            customerPicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        bank_feild.setVisibility(View.GONE);
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
        bankList = new ArrayList<>();
        branchList = new ArrayList<>();
        frequencylist = new ArrayList<>();
        rtoagentlist = new ArrayList<>();
        serviceDatesLayoutsList = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        customerPojo = (CustomerPojo) getIntent().getSerializableExtra("customerDetails");
        id = customerPojo.getId();
        stateId = customerPojo.getStateId();
        typeId = customerPojo.getType_id();
        createdId = customerPojo.getCreated_by();

        // edt_clientname.setText(customerPojo.getClient_name());
        edt_state.setText(customerPojo.getStateName());
        edt_description.setText(customerPojo.getDescription());
        edt_remark.setText(customerPojo.getRemark());
        edt_type.setText(customerPojo.getType_name());
        edt_vehicleownername.setText(customerPojo.getVehicle_owner());
        edt_description.setText(customerPojo.getDescription());
        edt_vehicleno.setText(customerPojo.getVehicle_no());

        ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        serviceDatesList = customerPojo.getService_date();

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


        ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        otherDatesList = customerPojo.getOther_date();

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


        ArrayList<CustomerPojo.DocumentListPojo> documentsList = new ArrayList<>();
        documentsList = customerPojo.getDocument();

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
                        new ImportVehicleOtherCustomer_Activity.GetStateList().execute(user_id, "2");
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    stateListDialog(statelist);
                }
            }
        });

        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typelist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new ImportVehicleOtherCustomer_Activity.GetTypeList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    typeListDialog(typelist);
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
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });
        edt_taxvalidupto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_taxvalidupto.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear2 = year;
                        mMonth2 = monthOfYear;
                        mDay2 = dayOfMonth;
                    }
                }, mYear2, mMonth2, mDay2);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });


        edt_permitvalidupto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_permitvalidupto.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear3 = year;
                        mMonth3 = monthOfYear;
                        mDay3 = dayOfMonth;
                    }
                }, mYear3, mMonth3, mDay3);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });
        edt_satepermitvalidupto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edt_satepermitvalidupto.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear4 = year;
                        mMonth4 = monthOfYear;
                        mDay4 = dayOfMonth;
                    }
                }, mYear4, mMonth4, mDay4);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });
        nationalpermitvalidupto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        nationalpermitvalidupto.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear5 = year;
                        mMonth5 = monthOfYear;
                        mDay5 = dayOfMonth;
                    }
                }, mYear5, mMonth5, mDay5);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });
        pucrenewaldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        pucrenewaldate.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear6 = year;
                        mMonth6 = monthOfYear;
                        mDay6 = dayOfMonth;
                    }
                }, mYear6, mMonth6, mDay6);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dpd1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                dpd1.show();
            }

        });

        fitnessvalidupto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        fitnessvalidupto.setText(
                                changeDateFormat("yyyy-MM-dd",
                                        "dd/MM/yyyy",
                                        Utilities.ConvertDateFormat(Utilities.dfDate, dayOfMonth, monthOfYear + 1, year))

                        );

                        mYear7 = year;
                        mMonth7 = monthOfYear;
                        mDay7 = dayOfMonth;
                    }
                }, mYear7, mMonth7, mDay7);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
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
                        new ImportVehicleOtherCustomer_Activity.GetFrqList().execute(user_id);
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

    private void bankListDialog(final ArrayList<BankPojo> bankList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Bank");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);
        final SearchView searchView = findViewById(R.id.searchView);
        for (int i = 0; i < bankList.size(); i++) {
            arrayAdapter.add(String.valueOf(bankList.get(i).getName()));
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
                edt_bank.setText(bankList.get(which).getName());
                bankId = bankList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetBankList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getAllBank"));
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
                        bankList = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                BankPojo summary = new BankPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("name").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setName(jsonObj.getString("name"));
                                    bankList.add(summary);
                                }
                            }
                            if (bankList.size() != 0) {
                                bankListDialog(bankList);
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

    private void branchListDialog(final ArrayList<BranchPojo> branchList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Branch");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);
        final SearchView searchView = findViewById(R.id.searchView);
        for (int i = 0; i < branchList.size(); i++) {
            arrayAdapter.add(String.valueOf(branchList.get(i).getName()));
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
                edt_branch.setText(branchList.get(which).getName());
                branchId = branchList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetBranchList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getAllBranch"));
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
                        branchList = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                BranchPojo summary = new BranchPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("branch").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setName(jsonObj.getString("branch"));
                                    branchList.add(summary);
                                }
                            }
                            if (branchList.size() != 0) {
                                branchListDialog(branchList);
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

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        img_save = findViewById(R.id.img_save);
        mToolbar.setTitle("Import Details");
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

        ArrayList<CustomerPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        for (int i = 0; i < serviceDatesLayoutsList.size(); i++) {

            if (!((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).getText().toString().trim().equals("")) {
                if (!((EditText) serviceDatesLayoutsList.get(i).findViewById(R.id.edt_servicedate)).getText().toString().trim().equals("")) {

                    CustomerPojo.ServiceDatesListPojo serviceDateObj = new CustomerPojo.ServiceDatesListPojo();

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
            serviceDatesJSONArray.add(serviceDatesJSONObj);
        }


        ArrayList<CustomerPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        for (int i = 0; i < otherLayoutsList.size(); i++) {

            if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {
                if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {

                    CustomerPojo.OtherDatesListPojo otherDateObj = new CustomerPojo.OtherDatesListPojo();

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

        ArrayList<CustomerPojo.DocumentListPojo> documentsList = new ArrayList<>();
        for (int i = 0; i < documentsLayoutsList.size(); i++) {

            if (!((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim().equals("")) {

                CustomerPojo.DocumentListPojo documentObj = new CustomerPojo.DocumentListPojo();

                documentObj.setOriginal_name(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim());
                documentObj.setDocument_name(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).getText().toString().trim());
                documentsList.add(documentObj);
            }
        }

        JsonArray documentJSONArray = new JsonArray();

        for (int i = 0; i < documentsList.size(); i++) {
            JsonObject documentJSONObj = new JsonObject();
            documentJSONObj.addProperty("photo", documentsList.get(i).getOriginal_name());
            documentJSONObj.addProperty("document_name", documentsList.get(i).getDocument_name());
            documentJSONArray.add(documentJSONObj);
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "add");
        mainObj.addProperty("state", stateId);
        mainObj.addProperty("Name_of_the_Vehicle_Owner", edt_vehicleownername.getText().toString().trim());
        mainObj.addProperty("RTO_Agent", edt_rtoagent.getText().toString().trim());
        mainObj.addProperty("vehicle_dealer", edt_vehicledealer.getText().toString().trim());
        mainObj.addProperty("rtotype", typeId);
        mainObj.addProperty("c_Type", typeId);
        mainObj.addProperty("Engine_No", edt_engineno.getText().toString().trim());
        mainObj.addProperty("Chassis_No", edt_chassisno.getText().toString().trim());
        mainObj.addProperty("Description", edt_description.getText().toString().trim());
        mainObj.addProperty("Date_of_Purchase", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_purcasedate.getText().toString().trim()));
        mainObj.addProperty("Insurenace_Renewal_Date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_renewaldate.getText().toString().trim()));
        mainObj.addProperty("Permit_Valid_Upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_permitvalidupto.getText().toString().trim()));

        mainObj.addProperty("Tax_Paid_Upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_taxvalidupto.getText().toString().trim()));

        mainObj.addProperty("State_Permit_Valid_upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_satepermitvalidupto.getText().toString().trim()));

        mainObj.addProperty("National_Permit_Valid_Upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                nationalpermitvalidupto.getText().toString().trim()));

        mainObj.addProperty("PUC_Renewal_Date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                pucrenewaldate.getText().toString().trim()));

        mainObj.addProperty("Fitness_Valid_Upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                fitnessvalidupto.getText().toString().trim()));
        mainObj.addProperty("Insurance_Policy_No", edt_insurancepolicyno.getText().toString().trim());
        mainObj.addProperty("Temporary_Registration_No", edt_temregno.getText().toString().trim());
        mainObj.addProperty("Hypothecated_to", edt_hypothecatedto.getText().toString().trim());
        mainObj.add("documents", documentJSONArray);
        mainObj.addProperty("Remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("Vehicle_No", edt_vehicleno.getText().toString().trim());
        mainObj.add("service_dates", serviceDatesJSONArray);
        mainObj.add("other_dates", otherDatesJSONArray);
        mainObj.addProperty("created_by", user_id);
        mainObj.addProperty("updated_by", user_id);
        mainObj.addProperty("created_id", createdId);
        mainObj.addProperty("is_imported", "1");
        mainObj.addProperty("vehicle_image", edt_selectVehicleImage.getText().toString().trim());


        mainObj.addProperty("bank_name", edt_bank.getText().toString().trim());
        mainObj.addProperty("branch_name", edt_branch.getText().toString().trim());
        mainObj.addProperty("frequency", frequency);
        mainObj.addProperty("borrower_name", edt_borrowername.getText().toString().trim());
        mainObj.addProperty("loan_amount", edt_loanamount.getText().toString().trim());
        mainObj.addProperty("installment_amount", edt_installmentanount.getText().toString().trim());
        mainObj.addProperty("loan_account_number", edt_accountnumber.getText().toString().trim());
        mainObj.addProperty("date_of_section", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_sactiondate.getText().toString().trim()));
        mainObj.addProperty("start_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_startdate.getText().toString().trim()));

        mainObj.addProperty("end_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_enddate.getText().toString().trim()));

        // Log.i("LifeInsuranceJson", mainObj.toString());

        if (Utilities.isInternetAvailable(context)) {
            new ImportVehicleOtherCustomer_Activity.AddCustomerDetails().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }


    }

    public class AddCustomerDetails extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CUSTOMERAPI, params[0]);
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
                        new ImportVehicleOtherCustomer_Activity.IsImport().execute(id);
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
        }, mYear8, mMonth8, mDay8);
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
        }, mYear9, mMonth9, mDay9);
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
                                photoFile = new File(customerPicFolder, "doc_image.png");
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
                                        .pickFile(ImportVehicleOtherCustomer_Activity.this);
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
                            photoFile = new File(customerPicFolder, "doc_image.png");
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
                                    .pickFile(ImportVehicleOtherCustomer_Activity.this);
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
                    final CharSequence[] options = {"Take a Photo", "Choose from Gallery", "Choose a Document"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setCancelable(false);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take a Photo")) {
                                photoFile = new File(customerPicFolder, "doc_image.png");
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
                                        .pickFile(ImportVehicleOtherCustomer_Activity.this);
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
                            photoFile = new File(customerPicFolder, "doc_image.png");
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
                                    .pickFile(ImportVehicleOtherCustomer_Activity.this);
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(ImportVehicleOtherCustomer_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(ImportVehicleOtherCustomer_Activity.this);
            }

            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                Uri fileUri = data.getData();
                ArrayList<String> filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                File fileToBeUploaded = new File(filePath.get(0));
                new ImportVehicleOtherCustomer_Activity.UploadProductPhoto().execute(fileToBeUploaded);
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
        String destinationFilename = Environment.getExternalStorageDirectory() + "/RTO/"
                + "/Customer/" + File.separatorChar + "img.png";

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
        new ImportVehicleOtherCustomer_Activity.UploadProductPhoto().execute(photoFileToUpload);
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

    public class IsImport extends AsyncTask<String, Void, String> {
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
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "isImportOther");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
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

                        new Self_Fragment.GetCustomerList().execute(user_id);
                        //new Import_Vehicle_Dealer_Activity.IsImport().execute(user_id);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage(message);
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

}
