package in.rto.collections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import in.rto.collections.R;
import in.rto.collections.models.ClientCodePojo;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.models.NotificationPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class ViewNotification_Activity extends Activity {
    private Context context;
    private LinearLayout ll_parent;
    private EditText edt_productinfo;
    private TextView send_by, send_at;
    private FloatingActionButton fab_add_share;
    private NotificationPojo notificationDetails;
    private ImageView img_delete, img_edit, imv_product, imv_download, imv_share;
    private UserSessionManager session;
    private String user_id, document_url, document_name;
    private ArrayList<ClientMainListPojo> clientList;
    private ArrayList<ClientCodePojo> codeList;
    private LinearLayout clientnane, clientcode;
    private EditText dialog_edt_whatsappmessage, edt_date;
    private ImageView dialog_imv_whatsapppic;
    private RecyclerView lv_checkboxlist;
    private CheckBox cb_selectallclient, cb_notification, cb_whtasapp;
    private String notification = "0", whatssapp = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();
    }

    private void init() {
        context = ViewNotification_Activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        imv_product = findViewById(R.id.imv_product);
        edt_productinfo = findViewById(R.id.edt_productinfo);
        fab_add_share = findViewById(R.id.fab_add_share);
        send_at = findViewById(R.id.send_at);
        send_by = findViewById(R.id.send_by);
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
        notificationDetails = (NotificationPojo) getIntent().getSerializableExtra("notificationDetails");

        if (!notificationDetails.getImage().equals("")) {
            imv_product.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(notificationDetails.getImageurl())
                    .placeholder(R.drawable.img_product)
                    .into(imv_product, new Callback() {
                        @Override
                        public void onSuccess() {
                            document_url = notificationDetails.getImageurl();
                            Uri uri = Uri.parse(document_url);
                            document_name = uri.getLastPathSegment();
                            imv_product.setClickable(true);
                        }

                        @Override
                        public void onError() {
                            document_name = "";
                            imv_product.setClickable(false);
                        }
                    });
        }


        edt_productinfo.setText(notificationDetails.getMessage());
        String DateTime = changeDateFormat("yyyy-MM-dd HH:MM:SS",
                "dd-MM-yyyy HH:MM", notificationDetails.getCreated_at());
        send_at.setText(DateTime);
        send_by.setText(notificationDetails.getSenderName());
    }

    private void setEventHandler() {

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isInternetAvailable(context)) {
                            new ViewNotification_Activity.DeleteNotification().execute(notificationDetails.getId());
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
        });

        fab_add_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!notificationDetails.getImage().equals("")) {
                    URL url = null;
                    try {
                        url = new URL(notificationDetails.getImageurl());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap b = null;
                    try {
                        b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            b, "Title", null);
                    Uri imageUri = Uri.parse(path);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("*/*");
                    share.putExtra(Intent.EXTRA_TEXT, notificationDetails.getMessage());
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_STREAM, imageUri);
                    context.startActivity(Intent.createChooser(share, "Share !"));
                } else {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, notificationDetails.getMessage());
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    context.startActivity(Intent.createChooser(share, "Share !"));
                }

            }
        });
        imv_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.prompt_fullpic, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setView(promptView);

                ImageView imv_fullpic = promptView.findViewById(R.id.imv_fullpic);
                if (!notificationDetails.getImage().equals("")) {
                    Picasso.with(context)
                            .load(notificationDetails.getImageurl())
                            .placeholder(R.drawable.img_product)
                            .into(imv_fullpic);
                }
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
            }
        });


    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        img_delete = findViewById(R.id.img_delete);
        mToolbar.setTitle("View Notification");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class DeleteNotification extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "deleteNotification");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.MASTERAPI, obj.toString());
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

                        new Notification_Activity.GetNotificationsInfoList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Notification Deleted Successfully");
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
