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
import android.widget.SearchView;
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
import in.rto.collections.fragments.Fragment_RTO_Agent;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.models.VehicleDealerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.MultipartUtility;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class EditRTOAgent_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_vehicledealer,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_taxvalidupto, edt_permitvalidupto, edt_remark, edt_satepermitvalidupto, nationalpermitvalidupto, pucrenewaldate, fitnessvalidupto, edt_selectVehicleImage;
    private CheckBox isshow_to_dealer, isshow_to_customer;
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
    private String companyAliasName = "", documentType;
    private String user_id, stateId, clientId, typeId, dealerId, otherId, documentId, id;
    private String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File photoFile, rtoagentPicFolder;
    private RTOAgentListPojo rtoAgentListPojo;
    private String isshowtocustomer, isshowtorto;
    private LinkPojo linkPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rtoagent);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = EditRTOAgent_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);

        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_vehicledealer = findViewById(R.id.edt_vehicledealer);
        edt_description = findViewById(R.id.edt_description);
        edt_type = findViewById(R.id.edt_type);
        edt_engineno = findViewById(R.id.edt_engineno);
        edt_chassisno = findViewById(R.id.edt_chassisno);
        edt_insurancepolicyno = findViewById(R.id.edt_insurancepolicyno);
        edt_renewaldate = findViewById(R.id.edt_renewaldate);
        edt_taxvalidupto = findViewById(R.id.edt_taxpaidupto);
        edt_permitvalidupto = findViewById(R.id.edt_permitvalidupto);
        edt_remark = findViewById(R.id.edt_remark);
        edt_satepermitvalidupto = findViewById(R.id.edt_statepermitvalidupto);
        nationalpermitvalidupto = findViewById(R.id.edt_nationalpermitvalidupto);
        pucrenewaldate = findViewById(R.id.edt_pucrenewaldate);
        fitnessvalidupto = findViewById(R.id.edt_fitnessvalidupto);
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);

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
        vehicleDealerPojos = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();


        rtoAgentListPojo = (RTOAgentListPojo) getIntent().getSerializableExtra("rtoagentDetails");
        id = rtoAgentListPojo.getId();
        stateId = rtoAgentListPojo.getStateId();
        clientId = rtoAgentListPojo.getClient_id();
        typeId = rtoAgentListPojo.getType_id();
        dealerId = rtoAgentListPojo.getVehicle_dealer_id();
        edt_clientname.setText(rtoAgentListPojo.getClient_name());
        edt_chassisno.setText(rtoAgentListPojo.getChassis_no());
        edt_state.setText(rtoAgentListPojo.getStateName());
        edt_description.setText(rtoAgentListPojo.getDescription());
        edt_renewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", rtoAgentListPojo.getInsurance_renewal_date()));
        edt_taxvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                rtoAgentListPojo.getTax_paid_up_to()));

        edt_permitvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", rtoAgentListPojo.getPermit_valid_upto()));
        edt_satepermitvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                rtoAgentListPojo.getState_permit_valid_upto()));
        nationalpermitvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", rtoAgentListPojo.getNational_permit_valid_upto()));
        pucrenewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                rtoAgentListPojo.getPuc_renewal_date()));
        fitnessvalidupto.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", rtoAgentListPojo.getFittness_valid_upto()));
        edt_engineno.setText(rtoAgentListPojo.getEngine_no());
        edt_insurancepolicyno.setText(rtoAgentListPojo.getInsurance_policy_no());
        edt_remark.setText(rtoAgentListPojo.getRemark());
        edt_vehicledealer.setText(rtoAgentListPojo.getVehicle_dealer_name());
        edt_type.setText(rtoAgentListPojo.getType_name());
        edt_vehicleownername.setText(rtoAgentListPojo.getVehicle_owner());
        edt_description.setText(rtoAgentListPojo.getDescription());
        edt_vehicleno.setText(rtoAgentListPojo.getVehicle_no());
        edt_selectVehicleImage.setText(rtoAgentListPojo.getVehicle_image());
        isshow_to_dealer = findViewById(R.id.is_show_dealer);
        isshow_to_customer = findViewById(R.id.is_show_customer);
        if (rtoAgentListPojo.getIsshowto_customer().equals("1")) {
            isshow_to_customer.setChecked(true);
        } else {
            isshow_to_customer.setChecked(false);
        }
        if (rtoAgentListPojo.getIsshowto_dealer().equals("1")) {
            isshow_to_dealer.setChecked(true);
        } else {
            isshow_to_dealer.setChecked(false);
        }


        ArrayList<RTOAgentListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        otherDatesList = rtoAgentListPojo.getOther_date();

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

            }
        } else {
            //tv_ser.setText("No Maturity Dates Added");
        }


        ArrayList<RTOAgentListPojo.DocumentListPojo> documentsList = new ArrayList<>();
        documentsList = rtoAgentListPojo.getDocument();

        if (documentsList.size() != 0) {
            for (int i = 0; i < documentsList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_document, null);
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
                        new EditRTOAgent_Activity.GetClientList().execute(user_id);
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
                        new EditRTOAgent_Activity.GetTypeList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    typeListDialog(typelist);
                }
            }
        });

        edt_vehicledealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vehicleDealerPojos.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new EditRTOAgent_Activity.GetVehicleDealerList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    vehicledelaerListDialog(vehicleDealerPojos);
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

                        mYear1 = year;
                        mMonth1 = monthOfYear;
                        mDay1 = dayOfMonth;
                    }
                }, mYear1, mMonth1, mDay1);

                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                    //dpd1.getDatePicker().setMinDate(c.getTimeInMillis());
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

                        mYear2 = year;
                        mMonth2 = monthOfYear;
                        mDay2 = dayOfMonth;
                    }
                }, mYear2, mMonth2, mDay2);
                try {
                    dpd1.getDatePicker().setCalendarViewShown(false);
                    //dpd1.getDatePicker().setMinDate(c.getTimeInMillis());
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

                        mYear3 = year;
                        mMonth3 = monthOfYear;
                        mDay3 = dayOfMonth;
                    }
                }, mYear3, mMonth3, mDay3);
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

                        mYear4 = year;
                        mMonth4 = monthOfYear;
                        mDay4 = dayOfMonth;
                    }
                }, mYear4, mMonth4, mDay4);
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

                        mYear5 = year;
                        mMonth5 = monthOfYear;
                        mDay5 = dayOfMonth;
                    }
                }, mYear5, mMonth5, mDay5);
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

                        mYear6 = year;
                        mMonth6 = monthOfYear;
                        mDay6 = dayOfMonth;
                    }
                }, mYear6, mMonth6, mDay6);
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


    private void vehicledelaerListDialog(final ArrayList<VehicleDealerPojo> vehicledealerlist) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Vehicle Dealer");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < vehicledealerlist.size(); i++) {

            arrayAdapter.add(String.valueOf(vehicledealerlist.get(i).getName()));
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
                edt_vehicledealer.setText(vehicledealerlist.get(which).getName());
                dealerId = vehicledealerlist.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }

    public class GetVehicleDealerList extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getVehicleDealer"));
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
                        vehicleDealerPojos = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                VehicleDealerPojo summary = new VehicleDealerPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("name").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setName(jsonObj.getString("name"));
                                    vehicleDealerPojos.add(summary);
                                }
                            }
                            if (vehicleDealerPojos.size() != 0) {
                                vehicledelaerListDialog(vehicleDealerPojos);
                            } else {
                                Utilities.showAlertDialog(context, "No Record Found", "Please enter  vehicle dealer manually", false);
                            }
                        }
                    } else {
                        Utilities.showAlertDialog(context, "No Record Found", "Please enter vehicle dealer manually", false);
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
        final SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            searchView.setFocusedByDefault(false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        mToolbar.setTitle("Edit Vehicle Details");
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


        ArrayList<RTOAgentListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        for (int i = 0; i < otherLayoutsList.size(); i++) {

            if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {
                if (!((EditText) otherLayoutsList.get(i).findViewById(R.id.edt_otherdate)).getText().toString().trim().equals("")) {

                    RTOAgentListPojo.OtherDatesListPojo otherDateObj = new RTOAgentListPojo.OtherDatesListPojo();
                    if (i < otherDatesList.size()) {
                        otherDateObj.setOther_date_id(otherId);
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

        ArrayList<RTOAgentListPojo.DocumentListPojo> documentsList = new ArrayList<>();
        for (int i = 0; i < documentsLayoutsList.size(); i++) {

            if (!((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim().equals("")) {

                RTOAgentListPojo.DocumentListPojo documentObj = new RTOAgentListPojo.DocumentListPojo();
                if (i < documentsList.size()) {
                    documentObj.setDocument_id(documentId);
                    //serviceDatesList.setFamily_details_id(clientDetails.getRelation_details().get(i).getFamily_details_id());
                } else {
                    // clientFamilyObj.setFamily_details_id("0");
                    documentObj.setDocument_id("0");
                }
                documentObj.setDocument(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim());
                documentObj.setName(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).getText().toString().trim());
                documentsList.add(documentObj);
            }
        }

        JsonArray documentJSONArray = new JsonArray();

        for (int i = 0; i < documentsList.size(); i++) {
            JsonObject documentJSONObj = new JsonObject();
            documentJSONObj.addProperty("photo", documentsList.get(i).getDocument());
            documentJSONObj.addProperty("doc_name", documentsList.get(i).getName());
            documentJSONObj.addProperty("id", documentsList.get(i).getDocument_id());
            documentJSONArray.add(documentJSONObj);
        }
        if (isshow_to_customer.isChecked()) {
            isshowtocustomer = "1";
        } else {
            isshowtocustomer = "0";
        }

        if (isshow_to_dealer.isChecked()) {
            isshowtorto = "1";
        } else {
            isshowtorto = "0";
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "update");
        mainObj.addProperty("state", stateId);
        mainObj.addProperty("client_name", clientId);
        mainObj.addProperty("vehicle_owner_name", edt_vehicleownername.getText().toString().trim());
        mainObj.addProperty("vehicle_dealer", dealerId);
        mainObj.addProperty("rtotype", typeId);
        mainObj.addProperty("description", edt_description.getText().toString().trim());
        mainObj.addProperty("engine_no", edt_engineno.getText().toString().trim());
        mainObj.addProperty("chassis_no", edt_chassisno.getText().toString().trim());
        mainObj.addProperty("insurance_renewal_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_renewaldate.getText().toString().trim()));
        mainObj.addProperty("permit_valid_upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_permitvalidupto.getText().toString().trim()));

        mainObj.addProperty("tax_paid_upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_taxvalidupto.getText().toString().trim()));

        mainObj.addProperty("state_permit_valid_upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                edt_satepermitvalidupto.getText().toString().trim()));

        mainObj.addProperty("national_permit_upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                nationalpermitvalidupto.getText().toString().trim()));

        mainObj.addProperty("puc_renewal_date", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                pucrenewaldate.getText().toString().trim()));

        mainObj.addProperty("fitness_valid_upto", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                fitnessvalidupto.getText().toString().trim()));


        mainObj.addProperty("insurance_policy_no", edt_insurancepolicyno.getText().toString().trim());
        mainObj.add("documents", documentJSONArray);
        mainObj.addProperty("remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("vechicle_no", edt_vehicleno.getText().toString().trim());
        mainObj.add("other_dates", otherDatesJSONArray);
        mainObj.addProperty("created_by", user_id);
        mainObj.addProperty("updated_by", user_id);
        mainObj.addProperty("id", id);
        mainObj.addProperty("is_show_to_customer", isshowtocustomer);
        mainObj.addProperty("is_show_to_dealer", isshowtorto);
        mainObj.addProperty("vehicle_image", edt_selectVehicleImage.getText().toString().trim());

        // Log.i("LifeInsuranceJson", mainObj.toString());

        if (Utilities.isInternetAvailable(context)) {
            new UpdateRTOAgentDetails().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }


    }

    public class UpdateRTOAgentDetails extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.RTOAGENTAPI, params[0]);
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

                        new Fragment_RTO_Agent.GetRTOAgentList().execute(user_id);

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
                                photoFile = new File(rtoagentPicFolder, "doc_image.png");
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
                                        .pickFile(EditRTOAgent_Activity.this);
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
                            photoFile = new File(rtoagentPicFolder, "doc_image.png");
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
                                    .pickFile(EditRTOAgent_Activity.this);
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
        }, mYear7, mMonth7, mDay7);
        try {
            dpd2.getDatePicker().setCalendarViewShown(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dpd2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        dpd2.show();
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
                                photoFile = new File(rtoagentPicFolder, "doc_image.png");
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
                                        .pickFile(EditRTOAgent_Activity.this);
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
                            photoFile = new File(rtoagentPicFolder, "doc_image.png");
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
                                    .pickFile(EditRTOAgent_Activity.this);
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(EditRTOAgent_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(EditRTOAgent_Activity.this);
            }

            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                Uri fileUri = data.getData();
                ArrayList<String> filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                File fileToBeUploaded = new File(filePath.get(0));
                new EditRTOAgent_Activity.UploadProductPhoto().execute(fileToBeUploaded);
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
                        String original_name = Obj1.getString("orignal_name");
                        if (documentType.equals("selectedImage")) {
                            edt_selectdocuments.setText(document_name);
                            edt_name = ((EditText) findViewById(R.id.edt_name));
                            edt_name.setText(original_name);
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
        String destinationFilename = Environment.getExternalStorageDirectory() + "/RTO/" + "RTODaler" + File.separatorChar + "img.png";

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
        new EditRTOAgent_Activity.UploadProductPhoto().execute(photoFileToUpload);
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
}
