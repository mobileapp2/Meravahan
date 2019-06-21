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
import in.rto.collections.fragments.Fragment_RTO_Dealer_Details;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.models.VehicleDealerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class ViewImportVehicleDealer_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private static final int CAMERA_REQUEST = 100;
    private LinearLayout ll_servicedates, ll_documents,ll_Otherdates;
    private ImageView btn_addservicedates, btn_adddocuments,btn_addotherdates;
    private static final int GALLERY_REQUEST = 200;
    private EditText edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername, edt_vehicledealer,
            edt_description, edt_type, edt_engineno, edt_chassisno, edt_insurancepolicyno, edt_renewaldate,
            edt_taxvalidupto, edt_permitvalidupto, edt_remark,edt_satepermitvalidupto,nationalpermitvalidupto,
            pucrenewaldate,fitnessvalidupto,edt_selectVehicleImage;

    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    private int mYear2, mMonth2, mDay2;
    private int mYear3, mMonth3, mDay3;
    private int mYear4, mMonth4, mDay4;
    private int mYear5, mMonth5, mDay5;
    private int mYear6, mMonth6, mDay6;
    private int mYear7, mMonth7, mDay7;


    private EditText edt_selectdocuments = null;
    private ImageView img_save;
    private ArrayList<ClientMainListPojo> clientList;
    private List<LinearLayout> documentsLayoutsList;
    private List<LinearLayout> otherLayoutsList;
    private ArrayList<VehicleDealerPojo> vehicleDealerPojos;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String companyAliasName = "";
    private String user_id, stateId, clientId, typeId,dealerId,role;
    private String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri photoURI;
    private File file, rtoagentPicFolder;
    private RTOAgentListPojo rtoAgentListPojo;
    private ImageView img_delete, img_edit;
   // private CheckBox isshow_to_dealer,isshow_to_customer;
    private String isshowtocustomer , isshowtorto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_import_vehicle_dealer);

      //  setContentView(R.layout.activity_view_rtoagent);
        init();
        getSessionData();
        setUpToolbar();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = ViewImportVehicleDealer_Activity.this;
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
        edt_remark = findViewById(R.id.edt_remark);
        edt_taxvalidupto = findViewById(R.id.edt_taxpaidupto);
        edt_permitvalidupto = findViewById(R.id.edt_permitvalidupto);
        edt_satepermitvalidupto = findViewById(R.id.edt_statepermitvalidupto);
        nationalpermitvalidupto = findViewById(R.id.edt_nationalpermitvalidupto);
        pucrenewaldate = findViewById(R.id.edt_pucrenewaldate);
        fitnessvalidupto = findViewById(R.id.edt_fitnessvalidupto);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        btn_addotherdates = findViewById(R.id.btn_addotherdates);
        btn_adddocuments = findViewById(R.id.btn_adddocuments);
        edt_selectVehicleImage = findViewById(R.id.edt_selectvehicle);
        //isshow_to_dealer = findViewById(R.id.is_show_dealer);
        //isshow_to_customer = findViewById(R.id.is_show_customer);
       // isshow_to_dealer.setClickable(false);
       // isshow_to_customer.setClickable(false);

        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
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
            role = json.getString("role_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setDefaults() {
        rtoAgentListPojo = (RTOAgentListPojo) getIntent().getSerializableExtra("rtoagentDetails");

        //edt_clientname.setText(rtoAgentListPojo.getClient_name());
        edt_chassisno.setText(rtoAgentListPojo.getChassis_no());
        edt_state.setText(rtoAgentListPojo.getStateName());
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
        edt_insurancepolicyno.setText(rtoAgentListPojo.getInsurance_policy_no());
        edt_selectVehicleImage.setText(rtoAgentListPojo.getVehicle_image());
//        if (rtoAgentListPojo.getIsshowto_customer().equals("1")) {
//            isshow_to_customer.setChecked(true);
//        } else{
//            isshow_to_customer.setChecked(false);
//        }
//        if (rtoAgentListPojo.getIsshowto_dealer().equals("1")) {
//            isshow_to_dealer.setChecked(true);
//        }else{
//            isshow_to_dealer.setChecked(false);
//        }

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
    private void setEventHandler() {
    }

    public void viewDocument(View view) {

        if (Utilities.isInternetAvailable(context)) {
            EditText edt_selectdocuments = (EditText) view;
            new ViewImportVehicleDealer_Activity.DownloadDocument().execute(edt_selectdocuments.getText().toString().trim());
        } else {

        }

    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        if(!role.equals("3")) {
            mToolbar.inflateMenu(R.menu.list_import_menu);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.img_edit) {
                        if (Utilities.isInternetAvailable(context)) {
                            new ViewImportVehicleDealer_Activity.isImportDetails().execute(rtoAgentListPojo.getId());
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }

                    }else if(id == R.id.img_link){
                        if (Utilities.isInternetAvailable(context)) {
                            Intent intent = new Intent(context, LInkRTODealer_Activity.class);
                            intent.putExtra("rtoDetails", rtoAgentListPojo);
                            context.startActivity(intent);
                        } else {
                            Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }
                    } else if (id == R.id.img_delete) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Are you sure you want to delete this item?");
                        builder.setTitle("Alert");
                        builder.setIcon(R.drawable.ic_alert_red_24dp);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (Utilities.isInternetAvailable(context)) {
                                    new ViewImportVehicleDealer_Activity.DeleteRTOAgentDetails().execute(rtoAgentListPojo.getId());
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

            img_delete = findViewById(R.id.img_delete);
            img_edit = findViewById(R.id.img_edit);
        }
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


    public class DeleteRTOAgentDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isdelete");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.RTOAGENTAPI, obj.toString());
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

                        new Fragment_RTO_Dealer_Details.GetRTOAgentList().execute(user_id);

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
            obj.addProperty("type", "isRecoredImported");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.RTOAGENTAPI, obj.toString());
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
                                Intent intent = new Intent(context, Import_Vehicle_Dealer_Activity.class);
                                intent.putExtra("rtoagentDetails", rtoAgentListPojo);
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
                        Intent intent = new Intent(context, Import_Vehicle_Dealer_Activity.class);
                        intent.putExtra("rtoagentDetails", rtoAgentListPojo);
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

