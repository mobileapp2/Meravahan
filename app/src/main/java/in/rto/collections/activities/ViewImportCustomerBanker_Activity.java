package in.rto.collections.activities;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
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
import in.rto.collections.fragments.Self_Fragment;
import in.rto.collections.models.BankPojo;
import in.rto.collections.models.BankerDetailsPojo;
import in.rto.collections.models.BranchPojo;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.FrequencyPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.VehicleDealerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class ViewImportCustomerBanker_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents,ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments,btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_bank, edt_branch, edt_clientname, edt_borrowername, edt_vehicledealer,
            edt_description, edt_loanamount, edt_accountnumber, edt_sactiondate, edt_installmentanount, edt_startdate,
            edt_enddate, edt_frequency, edt_remark,edt_vehiclenumber,purchasedate,edt_selectVehicleImage;
    private CheckBox isshow_to_dealer,isshow_to_customer;
    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private int mYear3, mMonth3, mDay3;
    private int mYear4, mMonth4, mDay4;
    private int mYear5, mMonth5, mDay5;
    private int mYear6, mMonth6, mDay6;
    private int mYear7, mMonth7, mDay7;
    private String isshowtocustomer , isshowtorto;

    private EditText edt_selectdocuments = null,edt_name = null;
    private ImageView img_save;
    private ArrayList<BankPojo> bankList;
    private ArrayList<BranchPojo> branchList;
    private List<LinearLayout> documentsLayoutsList;
    private ArrayList<VehicleDealerPojo> vehicleDealerPojos;
    private ArrayList<StatePojo> statelist;
    private ArrayList<ClientMainListPojo> clientlist;
    private ArrayList<FrequencyPojo> frequencylist;
    private UserSessionManager session;
    private String companyAliasName = "";
    private String user_id, stateId, clientId, typeId,dealerId,statename="",bankId,branchId,frequency;
    private String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File file, rtoagentPicFolder;
    private CustomerPojo customerPojo;
    private ImageView img_delete, img_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imp);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
    }
    private void init() {
        context = ViewImportCustomerBanker_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);
        edt_bank = findViewById(R.id.edt_bank);
        edt_branch = findViewById(R.id.edt_branch);
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
        edt_sactiondate = findViewById(R.id.edt_saction);
        purchasedate = findViewById(R.id.edt_purchasedate);
        edt_vehiclenumber = findViewById(R.id.edt_vehicleno);
        ll_documents = findViewById(R.id.ll_documents);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);

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
        documentsLayoutsList = new ArrayList<>();
        customerPojo  = (CustomerPojo) getIntent().getSerializableExtra("bankerdetails");
        edt_bank.setText(customerPojo.getBank_name());
        edt_branch.setText(customerPojo.getBranch_name());
        edt_description.setText(customerPojo.getDescription());
        edt_sactiondate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",customerPojo.getDate_to_section()));
        edt_enddate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                customerPojo.getInstallment_end_date()));
        edt_startdate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                customerPojo.getInstallment_start_date()));

        purchasedate.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy", customerPojo.getPurchase_date()));

        edt_loanamount.setText(customerPojo.getLoan_amount());
        edt_accountnumber.setText(customerPojo.getLoan_account_number());
        edt_installmentanount.setText(customerPojo.getInstallment_amount());
        edt_remark.setText(customerPojo.getRemark());
       // edt_clientname.setText(customerPojo.getClient_name());
        edt_vehicledealer.setText(customerPojo.getVehicle_dealer_name());
        edt_frequency.setText(customerPojo.getFrequency());
        edt_borrowername.setText(customerPojo.getBorrower_name());
        edt_description.setText(customerPojo.getDescription());
        edt_vehiclenumber.setText(customerPojo.getVehicle_no());
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
                final ArrayList<CustomerPojo.DocumentListPojo> finalDocumentsList = documentsList;
                final int finalI = i;
                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // viewDocument(finalDocumentsList.get(finalI).getDocument());
                    }
                });
            }
        } else {
            // tv_documents.setText("No Documents Added");
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
                        if (Utilities.isInternetAvailable(context)) {
                            new ViewImportCustomerBanker_Activity.isImportDetails().execute(customerPojo.getId());
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }

                    } else {
                        Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                    }

                }else if(id == R.id.img_link){
                    if (Utilities.isInternetAvailable(context)) {
                        Intent intent = new Intent(context, LinkCustomerBanker_Activity.class);
                        intent.putExtra("customerDetails", customerPojo);
                        context.startActivity(intent);
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
                               new ViewImportCustomerBanker_Activity.DeleteBankerDetails().execute(customerPojo.getId());
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
        mToolbar.setTitle("Vehicle Details");
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

                file = new File(rtoagentPicFolder, Uri.parse(params[0]).getLastPathSegment());
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


    public class DeleteBankerDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isdeleteBanker");
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
            obj.addProperty("type", "isRecoredImportedBanker");
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
                                Intent intent = new Intent(context, Import_Banker_Customer_Activity.class);
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
                        Intent intent = new Intent(context, Import_Banker_Customer_Activity.class);
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
