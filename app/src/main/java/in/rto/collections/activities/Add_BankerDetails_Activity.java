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
import in.rto.collections.fragments.Fragment_Banker_Vehicle_Details;
import in.rto.collections.models.BankPojo;
import in.rto.collections.models.BankerDetailsPojo;
import in.rto.collections.models.BranchPojo;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.FrequencyPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.VehicleDealerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.MultipartUtility;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class Add_BankerDetails_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents, ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments, btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_bank, edt_branch, edt_clientname, edt_borrowername, edt_vehicledealer,
            edt_description, edt_loanamount, edt_accountnumber, edt_sactiondate, edt_installmentanount, edt_startdate,
            edt_enddate, edt_frequency, edt_remark, edt_vehiclenumber, purchasedate, edt_selectVehicleImage;
    private CheckBox isshow_to_dealer, isshow_to_customer;
    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private int mYear3, mMonth3, mDay3;
    private int mYear4, mMonth4, mDay4;
    private int mYear5, mMonth5, mDay5;
    private int mYear6, mMonth6, mDay6;
    private int mYear7, mMonth7, mDay7;
    private String isshowtocustomer, isshowtorto;

    private EditText edt_selectdocuments = null, edt_name = null;
    private ImageView img_save;
    private ArrayList<BankPojo> bankList;
    private ArrayList<BranchPojo> branchList;
    private List<LinearLayout> documentsLayoutsList;
    private ArrayList<VehicleDealerPojo> vehicleDealerPojos;
    private ArrayList<StatePojo> statelist;
    private ArrayList<ClientMainListPojo> clientlist;
    private ArrayList<FrequencyPojo> frequencylist;
    private UserSessionManager session;
    private String companyAliasName = "", documentType;
    private String user_id, stateId, clientId, typeId, dealerId, statename = "", bankId, branchId, frequency;
    private String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File photoFile, rtoagentPicFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__banker_details_);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = Add_BankerDetails_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);

        edt_bank = findViewById(R.id.edt_bank);
        edt_branch = findViewById(R.id.edt_branch);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_borrowername = findViewById(R.id.edt_borrower);
        edt_vehicledealer = findViewById(R.id.edt_vehicledealer);
        edt_description = findViewById(R.id.edt_description);
        edt_accountnumber = findViewById(R.id.edt_accountno);
        edt_loanamount = findViewById(R.id.edt_loanamount);
        edt_enddate = findViewById(R.id.edt_enddate);
        edt_startdate = findViewById(R.id.edt_startdate);
        edt_installmentanount = findViewById(R.id.edt_installmentamount);
        edt_remark = findViewById(R.id.edt_remark);
        edt_frequency = findViewById(R.id.edt_frequency);
        edt_sactiondate = findViewById(R.id.edt_section);
        isshow_to_dealer = findViewById(R.id.is_show_dealer);
        isshow_to_customer = findViewById(R.id.is_show_customer);
        purchasedate = findViewById(R.id.edt_purchasedate);
        edt_vehiclenumber = findViewById(R.id.edt_vehiclenumber);
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);
        ll_documents = findViewById(R.id.ll_documents);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);

        rtoagentPicFolder = new File(Environment.getExternalStorageDirectory() + "/Banker/" + "Banker");
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


        clientlist = new ArrayList<>();
        vehicleDealerPojos = new ArrayList<>();
        frequencylist = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventHandler() {


        edt_clientname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clientlist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new Add_BankerDetails_Activity.GetClientList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    clientListDialog(clientlist);
                }
            }
        });


        edt_frequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frequencylist.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new Add_BankerDetails_Activity.GetFrqList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    feqListDialog(frequencylist);
                }
            }
        });
        edt_vehicledealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vehicleDealerPojos.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new Add_BankerDetails_Activity.GetVehicleDealerList().execute(user_id);
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                } else {
                    vehicledelaerListDialog(vehicleDealerPojos);
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

                        mYear1 = year;
                        mMonth1 = monthOfYear;
                        mDay1 = dayOfMonth;
                    }
                }, mYear1, mMonth1, mDay1);

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

                        mYear2 = year;
                        mMonth2 = monthOfYear;
                        mDay2 = dayOfMonth;
                    }
                }, mYear2, mMonth2, mDay2);

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
        purchasedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        purchasedate.setText(
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
                        clientlist = new ArrayList<>();
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                ClientMainListPojo summary = new ClientMainListPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("name").equals("")) {
                                    summary.setId(jsonObj.getString("id"));
                                    summary.setName(jsonObj.getString("name"));
                                    clientlist.add(summary);
                                }
                            }
                            if (clientlist.size() != 0) {
                                clientListDialog(clientlist);
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
        mToolbar.setTitle("Add Loan Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void submitData() {

        if (edt_bank.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select Bank");
            return;
        }

        if (edt_branch.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select Branch.");
            return;
        }
        if (edt_clientname.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select client");
            return;
        }

        if (edt_vehicledealer.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please select dealer.");
            return;
        }
        if (edt_startdate.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please Select start date");
            return;
        }

        if (edt_enddate.getText().toString().trim().equals("")) {
            Utilities.showSnackBar(ll_parent, "Please select end date.");
            return;
        }
        if (!Utilities.isVehicleNo(edt_vehiclenumber)) {
            Utilities.showSnackBar(ll_parent, "Please Enter Valid Vehicle Number");
            return;
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

        ArrayList<BankerDetailsPojo.DocumentListPojo> documentsList = new ArrayList<>();
        for (int i = 0; i < documentsLayoutsList.size(); i++) {

            if (!((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim().equals("")) {

                BankerDetailsPojo.DocumentListPojo documentObj = new BankerDetailsPojo.DocumentListPojo();

                documentObj.setDocument(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).getText().toString().trim());
                documentObj.setName(((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_name)).getText().toString().trim());
                documentsList.add(documentObj);
            }
        }

        JsonArray documentJSONArray = new JsonArray();

        for (int i = 0; i < documentsList.size(); i++) {
            JsonObject documentJSONObj = new JsonObject();
            documentJSONObj.addProperty("photos", documentsList.get(i).getDocument());
            documentJSONObj.addProperty("doc_name", documentsList.get(i).getName());
            documentJSONArray.add(documentJSONObj);
        }


        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "add");
        mainObj.addProperty("bank_name", edt_bank.getText().toString().trim());
        mainObj.addProperty("branch_name", edt_branch.getText().toString().trim());
        mainObj.addProperty("client_id", clientId);
        mainObj.addProperty("frequency", frequency);
        mainObj.addProperty("borrower_name", edt_borrowername.getText().toString().trim());
        mainObj.addProperty("dealer_id", dealerId);
        mainObj.addProperty("description", edt_description.getText().toString().trim());
        mainObj.addProperty("loan_amount", edt_loanamount.getText().toString().trim());
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

        mainObj.addProperty("date_of_purchase", changeDateFormat("dd/MM/yyyy",
                "yyyy/MM/dd",
                purchasedate.getText().toString().trim()));
        mainObj.addProperty("installment_amount", edt_installmentanount.getText().toString().trim());
        mainObj.add("document", documentJSONArray);
        mainObj.addProperty("remark", edt_remark.getText().toString().trim());
        mainObj.addProperty("vehicle_number", edt_vehiclenumber.getText().toString().trim());
        mainObj.addProperty("user_id", user_id);
        mainObj.addProperty("is_imported", "0");
        mainObj.addProperty("is_show_to_customer", isshowtocustomer);
        mainObj.addProperty("is_show_to_dealer", isshowtorto);
        mainObj.addProperty("vehicle_image", edt_selectVehicleImage.getText().toString().trim());

        // Log.i("LifeInsuranceJson", mainObj.toString());

        if (Utilities.isInternetAvailable(context)) {
            new Add_BankerDetails_Activity.AddBankerDetails().execute(mainObj.toString());
        } else {
            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
        }


    }

    public class AddBankerDetails extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.BANKERAPI, params[0]);
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

                        new Fragment_Banker_Vehicle_Details.GetBankerList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Banker Details Saved Successfully.");
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
        }, mYear7, mMonth7, mDay7);
        try {
            dpd2.getDatePicker().setCalendarViewShown(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dpd2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        dpd2.show();
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
                                        .pickFile(Add_BankerDetails_Activity.this);
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
                                    .pickFile(Add_BankerDetails_Activity.this);
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
                                        .pickFile(Add_BankerDetails_Activity.this);
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
                                    .pickFile(Add_BankerDetails_Activity.this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(Add_BankerDetails_Activity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(Add_BankerDetails_Activity.this);
            }

            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                Uri fileUri = data.getData();
                ArrayList<String> filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                File fileToBeUploaded = new File(filePath.get(0));
                new Add_BankerDetails_Activity.UploadProductPhoto().execute(fileToBeUploaded);
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
        String destinationFilename = Environment.getExternalStorageDirectory() + "/Banker/" + "Banker" + File.separatorChar + "img.png";

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
        new Add_BankerDetails_Activity.UploadProductPhoto().execute(photoFileToUpload);
//        doc_image_uri = Uri.fromFile(imageFile);
    }

    public void deleteDocument(View view) {
        ll_documents.removeView((View) view.getParent());
        documentsLayoutsList.remove(view.getParent());
    }

}
