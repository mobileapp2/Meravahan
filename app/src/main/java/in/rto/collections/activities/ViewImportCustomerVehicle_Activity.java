package in.rto.collections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.fragments.Vehicle_Customer_Fragment;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.FrequencyPojo;
import in.rto.collections.models.RTOAgentPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class ViewImportCustomerVehicle_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private LinearLayout ll_servicedates, ll_documents,ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments,btn_addotherdates;
    private EditText edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_rtoagent,edt_vehicledealer,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_purcasedate, edt_temregno, edt_remark,edt_hypothecatedto,edt_taxvalidupto, edt_permitvalidupto
            ,edt_satepermitvalidupto,nationalpermitvalidupto,pucrenewaldate,fitnessvalidupto,edt_bank, edt_branch, edt_borrowername,
            edt_loanamount, edt_accountnumber, edt_sactiondate, edt_installmentanount, edt_startdate,edt_selectVehicleImage,
            edt_enddate, edt_frequency;
    private ImageView img_save;
    private List<LinearLayout> serviceDatesLayoutsList;
    private ArrayList<ClientMainListPojo> clientList;
    private List<LinearLayout> documentsLayoutsList;
    private List<LinearLayout> otherLayoutsList;
    private ArrayList<RTOAgentPojo> rtoagentlist;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String user_id,callType;
    private Uri photoURI;
    private File file, customerPicFolder;
    private CustomerPojo customerPojo;
    private ImageView img_delete, img_edit;
    private ArrayList<FrequencyPojo> frequencylist;
    private LinearLayout bank_details,bank_feild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_import_customer_vehicle);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }
    private void init() {
        context = ViewImportCustomerVehicle_Activity.this;
        session = new UserSessionManager(context);
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


        ll_servicedates = findViewById(R.id.ll_servicedates);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);

        serviceDatesLayoutsList = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        customerPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Vehicle_dealer");
        if (!customerPicFolder.exists())
            customerPicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        bank_feild.setVisibility(View.GONE);
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
        customerPojo  = (CustomerPojo) getIntent().getSerializableExtra("customerDetails");

        // edt_clientname.setText(customerPojo.getClient_name());
        edt_chassisno.setText(customerPojo.getChassis_no());
        edt_state.setText(customerPojo.getStateName());
        edt_description.setText(customerPojo.getDescription());
        edt_renewaldate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",customerPojo.getInsurance_renewal_date()));
        edt_purcasedate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                customerPojo.getPurchase_date()));
        edt_engineno.setText(customerPojo.getEngine_no());
        edt_hypothecatedto.setText(customerPojo.getHypothecated_to());
        edt_insurancepolicyno.setText(customerPojo.getInsurance_policy_no());
        edt_remark.setText(customerPojo.getRemark());
        edt_rtoagent.setText(customerPojo.getRto_agent_name());
        edt_vehicledealer.setText(customerPojo.getVehicle_dealer_name());
        edt_temregno.setText(customerPojo.getTem_reg_no());
        edt_type.setText(customerPojo.getType_name());
        edt_vehicleownername.setText(customerPojo.getVehicle_owner());
        edt_vehicleno.setText(customerPojo.getVehicle_no());
        edt_selectVehicleImage.setText(customerPojo.getVehicle_image());
        if(customerPojo.getBank_name() !=  null) {
            edt_bank.setText(customerPojo.getBank_name());
            edt_branch.setText(customerPojo.getBranch_name());
            edt_sactiondate.setText(changeDateFormat("yyyy-MM-dd",
                    "dd/MM/yyyy", customerPojo.getDate_to_section()));
            edt_enddate.setText(changeDateFormat("yyyy-MM-dd",
                    "dd/MM/yyyy",
                    customerPojo.getInstallment_end_date()));
            edt_startdate.setText(changeDateFormat("yyyy-MM-dd",
                    "dd/MM/yyyy",
                    customerPojo.getInstallment_start_date()));
            edt_loanamount.setText(customerPojo.getLoan_amount());
            edt_accountnumber.setText(customerPojo.getLoan_account_number());
            edt_installmentanount.setText(customerPojo.getInstallment_amount());
            edt_frequency.setText(customerPojo.getFrequency());
            edt_borrowername.setText(customerPojo.getBorrower_name());
        }

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
    }

    public void viewDocument(View view) {

        if (Utilities.isInternetAvailable(context)) {
            EditText edt_selectdocuments = (EditText) view;
            new ViewImportCustomerVehicle_Activity.DownloadDocument().execute(edt_selectdocuments.getText().toString().trim());
        } else {

        }

    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.list_import_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.img_edit){
                    if (Utilities.isInternetAvailable(context)) {
                        new ViewImportCustomerVehicle_Activity.isImportDetails().execute(customerPojo.getId());
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }

                }else if(id == R.id.img_link){
                    if (Utilities.isInternetAvailable(context)) {
                        Intent intent = new Intent(context, LinkCustomerDealer_Activity.class);
                        intent.putExtra("customerDetails", customerPojo);
                        context.startActivity(intent);
                        finish();
                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }
                }else if(id == R.id.img_delete){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.ic_alert_red_24dp);
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (Utilities.isInternetAvailable(context)) {
                                new ViewImportCustomerVehicle_Activity.DeleteCustomerDetails().execute(customerPojo.getId());
                            } else {
                                Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                            }

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();
                }
                return false;
            }
        });

        mToolbar.setTitle("Import Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class DownloadDocument extends AsyncTask<String, Integer, Boolean> {
        int lenghtOfFile = -1;
        int count = 0;
        int content = -1;
        int counter = 0;
        int progress = 0;
        URL downloadurl = null;
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context, R.style.CustomDialogTheme);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("Downloading Document");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            int read = -1;
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = null;
            long total = 0;


            try {
                downloadurl = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) downloadurl.openConnection();
                lenghtOfFile = httpURLConnection.getContentLength();
                inputStream = httpURLConnection.getInputStream();

                file = new File(customerPicFolder, Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    publishProgress(counter);
                }
                success = true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress = (int) (((double) values[0] / lenghtOfFile) * 100);
            mProgressDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + file);
                if (downloadurl.toString().contains(".doc") || downloadurl.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if (downloadurl.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (downloadurl.toString().contains(".ppt") || downloadurl.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (downloadurl.toString().contains(".xls") || downloadurl.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (downloadurl.toString().contains(".zip") || downloadurl.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if (downloadurl.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (downloadurl.toString().contains(".wav") || downloadurl.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (downloadurl.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (downloadurl.toString().contains(".jpg") || downloadurl.toString().contains(".jpeg") || downloadurl.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (downloadurl.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (downloadurl.toString().contains(".3gp") || downloadurl.toString().contains(".mpg") || downloadurl.toString().contains(".mpeg") || downloadurl.toString().contains(".mpe") || downloadurl.toString().contains(".mp4") || downloadurl.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }


    public class DeleteCustomerDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isdeleteDealer");
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

                        //new Vehicle_Customer_Fragment.GetCustomerList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Vehicle Details Deleted Successfully");
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
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class isImportDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isRecoredImportedDealer");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage(message);
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, ImportVehicleCustomer_Activity.class);
                                intent.putExtra("customerDetails", customerPojo);
                                context.startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    } else {

                        Intent intent = new Intent(context, ImportVehicleCustomer_Activity.class);
                        intent.putExtra("customerDetails", customerPojo);
                        context.startActivity(intent);
                        finish();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
