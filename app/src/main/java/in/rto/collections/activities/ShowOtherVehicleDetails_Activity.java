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
import in.rto.collections.fragments.Fragment_Other_Vehicle_Details;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.RTOAgentPojo;
import in.rto.collections.models.StatePojo;
import in.rto.collections.models.TypePojo;
import in.rto.collections.models.VehicleDealerListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class ShowOtherVehicleDetails_Activity extends Activity {

    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private LinearLayout ll_servicedates, ll_documents,ll_Otherdates,ll_wheeldates;
    private ImageView btn_addservicedates, btn_adddocuments,btn_addotherdates,btn_addwheeldates;
    private EditText edt_state, edt_vehicleno, edt_clientname, edt_vehicleownername,
            edt_description, edt_type,
              edt_remark;
    private ImageView img_save;
    private List<LinearLayout> serviceDatesLayoutsList;
    private ArrayList<ClientMainListPojo> clientList;
    private List<LinearLayout> documentsLayoutsList;
    private List<LinearLayout> otherLayoutsList;
    private List<LinearLayout> wheelLayoutsList;
    private ArrayList<TypePojo> typelist;
    private ArrayList<StatePojo> statelist;
    private UserSessionManager session;
    private String user_id,callType,role;
    private Uri photoURI;
    private File file, vehicledealerPicFolder;
    private VehicleDealerListPojo vehicaldealerdetails;
    private ImageView img_delete, img_edit;
    private CheckBox isshow_to_customer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_other_vehicle_details);
        init();
        getSessionData();
        setUpToolbar();
        setDefaults();
        setEventHandler();
    }
    private void init() {
        context = ShowOtherVehicleDetails_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);

        edt_state = findViewById(R.id.edt_state);
        edt_vehicleno = findViewById(R.id.edt_vehicleno);
        edt_clientname = findViewById(R.id.edt_clientname);
        edt_vehicleownername = findViewById(R.id.edt_vehicleownername);
        edt_description = findViewById(R.id.edt_description);
        edt_type = findViewById(R.id.edt_type);
        edt_remark = findViewById(R.id.edt_remark);
        ll_servicedates = findViewById(R.id.ll_servicedates);
        ll_Otherdates = findViewById(R.id.ll_otherdates);
        ll_documents = findViewById(R.id.ll_documents);
        ll_wheeldates = findViewById(R.id.ll_wheeldates);
        isshow_to_customer = findViewById(R.id.is_show_customer);
        isshow_to_customer.setClickable(false);

        serviceDatesLayoutsList = new ArrayList<>();
        otherLayoutsList = new ArrayList<>();
        documentsLayoutsList = new ArrayList<>();
        wheelLayoutsList = new ArrayList<>();
        vehicledealerPicFolder = new File(Environment.getExternalStorageDirectory() + "/RTO/" + "Vehicle_dealer");
        if (!vehicledealerPicFolder.exists())
            vehicledealerPicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

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
        vehicaldealerdetails  = (VehicleDealerListPojo) getIntent().getSerializableExtra("vehicleDetails");

        edt_clientname.setText(vehicaldealerdetails.getClient_name());
        edt_state.setText(vehicaldealerdetails.getStateName());
        edt_description.setText(vehicaldealerdetails.getDescription());
        edt_remark.setText(vehicaldealerdetails.getRemark());
        edt_type.setText(vehicaldealerdetails.getType_name());
        edt_vehicleownername.setText(vehicaldealerdetails.getVehicle_owner());
        edt_description.setText(vehicaldealerdetails.getDescription());
        edt_vehicleno.setText(vehicaldealerdetails.getVehicle_no());
        if (vehicaldealerdetails.getIsshowto_customer().equals("1")) {
            isshow_to_customer.setChecked(true);
        } else{
            isshow_to_customer.setChecked(false);
        }
        ArrayList<VehicleDealerListPojo.ServiceDatesListPojo> serviceDatesList = new ArrayList<>();
        serviceDatesList = vehicaldealerdetails.getService_date();

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
        ArrayList<VehicleDealerListPojo.WheelDatesListPojo> wheelDatesList = new ArrayList<>();
        wheelDatesList = vehicaldealerdetails.getWheel_date();

        if (wheelDatesList.size() != 0) {
            for (int i = 0; i < wheelDatesList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.add_layout_viewwheeldates, null);
                wheelLayoutsList.add((LinearLayout) rowView);
                ll_wheeldates.addView(rowView, ll_wheeldates.getChildCount());

                ((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_wheeldate)).setText(changeDateFormat("yyyy-MM-dd",
                        "dd/MM/yyyy",
                        wheelDatesList.get(i).getWheel_alignment_date()));
                ((EditText) wheelLayoutsList.get(i).findViewById(R.id.edt_km)).setText(wheelDatesList.get(i).getText());

            }
        } else {
            //tv_ser.setText("No Maturity Dates Added");
        }

        ArrayList<VehicleDealerListPojo.OtherDatesListPojo> otherDatesList = new ArrayList<>();
        otherDatesList = vehicaldealerdetails.getOther_date();

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




        ArrayList< VehicleDealerListPojo.DocumentListPojo> documentsList = new ArrayList<>();
        documentsList = vehicaldealerdetails.getDocument();

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
                final ArrayList<VehicleDealerListPojo.DocumentListPojo> finalDocumentsList = documentsList;
                final int finalI = i;
                ((EditText) documentsLayoutsList.get(i).findViewById(R.id.edt_selectdocuments)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewDocument(finalDocumentsList.get(finalI).getDocument());
                    }
                });
            }
        } else {
            // tv_documents.setText("No Documents Added");
        }

    }
    public void viewDocument(String url) {

        if (Utilities.isInternetAvailable(context)) {
            new DownloadDocument().execute(url);
        } else {

        }

    }
    private void setEventHandler() {

    }
    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.list_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.img_edit) {
                    Intent intent = new Intent(context, EditOtherVehicle_Activity.class);
                    intent.putExtra("vehicleDetails", vehicaldealerdetails);
                    context.startActivity(intent);
                    finish();
                } else if (id == R.id.img_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.ic_alert_red_24dp);
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (Utilities.isInternetAvailable(context)) {
                                new DeleteOtherVehicleDetails1().execute(vehicaldealerdetails.getId());
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

                file = new File(vehicledealerPicFolder, Uri.parse(params[0]).getLastPathSegment());
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
    public class DeleteOtherVehicleDetails1 extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "delete");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.OTHERVEHICLEDEALER, obj.toString());
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

                        new Fragment_Other_Vehicle_Details.GetOtherVehicleDealerList().execute(user_id);

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


}
