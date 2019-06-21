package in.rto.collections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import in.rto.collections.R;
import in.rto.collections.fragments.Client_Fragment;
import in.rto.collections.models.ClientMainListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

import static in.rto.collections.utilities.Utilities.changeDateFormat;

public class ViewClientDetails_Activity extends Activity {
    private Context context;
    private ScrollView scrollView;
    private LinearLayout ll_parent;
    private EditText edt_name, edt_alias, edt_mobile, edt_whatsapp, edt_email, edt_dob, edt_anniversary, edt_ClientCode,edt_remark,edt_language;
    private ClientMainListPojo clientDetails;
    private ImageView img_delete, img_edit;
    private UserSessionManager session;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_client_details);
        init();
        setUpToolbar();
        getSessionData();
        setDefaults();
        setEventHandler();

    } private void init() {
        context = ViewClientDetails_Activity.this;
        session = new UserSessionManager(context);
        scrollView = findViewById(R.id.scrollView);
        ll_parent = findViewById(R.id.ll_parent);

        edt_name = findViewById(R.id.edt_name);
        edt_alias = findViewById(R.id.edt_alias);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_whatsapp = findViewById(R.id.edt_whatsapp);
        edt_email = findViewById(R.id.edt_email);
        edt_dob = findViewById(R.id.edt_dob);
        edt_anniversary = findViewById(R.id.edt_anniversary);
        edt_ClientCode = findViewById(R.id.edt_clientcode);
        edt_remark = findViewById(R.id.edt_remark);
        edt_language = findViewById(R.id.edt_language);
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
        clientDetails = (ClientMainListPojo) getIntent().getSerializableExtra("clientDetails");

        edt_name.setText(clientDetails.getName());
        edt_alias.setText(clientDetails.getAlias());
        edt_mobile.setText(clientDetails.getMobile());
        edt_whatsapp.setText(clientDetails.getWhats_app_no());
        edt_email.setText(clientDetails.getEmail());
        edt_dob.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                clientDetails.getDob()));
        edt_anniversary.setText(changeDateFormat("yyyy-MM-dd",
                "dd/MM/yyyy",
                clientDetails.getAnniversary_date()));
        edt_ClientCode.setText(clientDetails.getClient_code());
        edt_remark.setText(clientDetails.getRemark());
        edt_language.setText(clientDetails.getLanguage());

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
                 if(id == R.id.img_edit){
                     Intent intent = new Intent(context, EditClientDetails_Activity.class);
                     intent.putExtra("clientDetails", clientDetails);
                     context.startActivity(intent);
                     finish();
                 }else if(id == R.id.img_delete){
                     AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                     builder.setMessage("Are you sure you want to delete this item?");
                     builder.setTitle("Alert");
                     builder.setIcon(R.drawable.ic_alert_red_24dp);
                     builder.setCancelable(false);
                     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                             if (Utilities.isInternetAvailable(context)) {
                                 new DeleteClientDetails().execute(user_id, clientDetails.getId());
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
        mToolbar.setTitle("Client Details");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class DeleteClientDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("user_id", params[0]);
            obj.addProperty("id", params[1]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CLIENTAPI, obj.toString());
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

                       // new Client_Fragment.GetClientList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Client Details Deleted Successfully");
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

