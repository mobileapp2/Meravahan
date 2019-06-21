package in.rto.collections.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.RTOAgentPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class Tem_Registration_Activity extends Activity {
TextView tem_reg;
private String val, title;
private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tem__registration);
        context = Tem_Registration_Activity.this;
        tem_reg = findViewById(R.id.tem_reg);
        val =  getIntent().getStringExtra("type");
       new GetInformation().execute();
        setUpToolbar();
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class GetInformation extends AsyncTask<String, Void, String> {

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
            param.add(new ParamsPojo("type", "getAllInfo"));
            param.add(new ParamsPojo("id", val));
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
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                RTOAgentPojo summary = new RTOAgentPojo();
                                JSONObject jsonObj = jsonarr.getJSONObject(i);
                                if (!jsonObj.getString("description").equals("")) {
                                 String text =  jsonObj.getString("description");
                                 title =  jsonObj.getString("name");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        tem_reg.setText(Html.fromHtml(text,Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        tem_reg.setText(Html.fromHtml(text));
                                    }

                                   // tem_reg.setText(Html.fromHtml(text).toString());


                                 //tem_reg.setText(Html.fromHtml(text).toString());
                                    setUpToolbar();
                                }
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

}
