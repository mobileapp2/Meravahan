package in.rto.collections.paytm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import in.rto.collections.R;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static in.rto.collections.utilities.ApplicationConstants.MID;
import static in.rto.collections.utilities.ApplicationConstants.PAYTMURL;
import static in.rto.collections.utilities.ApplicationConstants.TRANSSTATUSURL;

public class checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private Context context;
    String custid = "", orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        context = checksum.this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderid");
        custid = intent.getExtras().getString("custid");

        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(checksum.this);


        String PAYTMVERIFYURL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID" + orderId;
        String CHECKSUMHASH = "";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(checksum.this);
            String param =
                    "MID=" + MID +
                            "&ORDER_ID=" + orderId +
                            "&CUST_ID=" + custid +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + getIntent().getExtras().getString("amount") + "&WEBSITE=APPSTAGING" +
                            "&CALLBACK_URL=" + PAYTMVERIFYURL + "&INDUSTRY_TYPE_ID=Retail";

            JSONObject jsonObject = jsonParser.makeHttpRequest(PAYTMURL, "POST", param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>", jsonObject.toString());
            if (jsonObject != null) {
                Log.e("CheckSum result >>", jsonObject.toString());
                try {

                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                    Log.e("CheckSum result >>", CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ", "  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

//            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            PaytmPGService Service = PaytmPGService.getProductionService();

            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", MID); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", getIntent().getExtras().getString("amount"));
            paramMap.put("WEBSITE", "APPSTAGING");
            paramMap.put("CALLBACK_URL", PAYTMVERIFYURL);
            //paramMap.put( "EMAIL" , "");   // no need
            // paramMap.put( "MOBILE_NO" , "");  // no need
            paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");

            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param " + paramMap.toString());
            Service.initialize(Order, null);
            // start payment service call here
            Service.startPaymentTransaction(checksum.this, true, true,
                    checksum.this);


        }

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        String response = bundle.toString();

        if (response.contains("TXN_SUCCESS")) {
            new TransactionStatusAPI().execute(MID, orderId);
        }

    }

    private class TransactionStatusAPI extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("MID", params[0])
                    .add("ORDERID", params[1])
                    .build();
            Request request = new Request.Builder()
                    .url(TRANSSTATUSURL)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public class BuyPlan extends AsyncTask<String, Void, String> {
        ProgressDialog pd;
        private String JSONString = "";

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

            JSONString = params[0];
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.PLANLISTAPI, params[0]);
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
//                        if (Utilities.isInternetAvailable(context)) {
//                            new UpdateCounts().execute(JSONString);
//                        } else {
//                            Utilities.showMessageString(context, "Please Check Internet Connection");
//                        }


                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }


    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String s) {

    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  " + s);
        finish();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true " + s + "  s1 " + s1);
        finish();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  ");
        finish();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel ");
        finish();
    }


}
