package in.rto.collections.paytm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.rto.collections.R;
import in.rto.collections.ccavenue.PlanBuySuccess_Activity;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.ParamsPojo;
import in.rto.collections.utilities.UserSessionManager;
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

public class PaytmPayment_Activity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private Context context;
    String custid = "", orderId = "";
    private UserSessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        context = PaytmPayment_Activity.this;
        session = new UserSessionManager(context);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderid");
        custid = intent.getExtras().getString("custid");

        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(PaytmPayment_Activity.this);


        String PAYTMVERIFYURL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID" + orderId;
        String CHECKSUMHASH = "";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(PaytmPayment_Activity.this);
            String param =
                    "MID=" + MID +
                            "&ORDER_ID=" + orderId +
                            "&CUST_ID=" + custid +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + getIntent().getExtras().getString("amount") + "&WEBSITE=APPSTAGING" +
                            "&CALLBACK_URL=" + PAYTMVERIFYURL + "&INDUSTRY_TYPE_ID=Retail";

            JSONObject jsonObject = jsonParser.makeHttpRequest(PAYTMURL, "POST", param);
            // yaha per PaytmPayment_Activity ke saht order id or status receive hoga..
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
            Log.e("PaytmPayment_Activity ", "param " + paramMap.toString());
            Service.initialize(Order, null);
            // start payment service call here
            Service.startPaymentTransaction(PaytmPayment_Activity.this, true, true,
                    PaytmPayment_Activity.this);


        }

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("PaytmPayment_Activity ", " respon true " + bundle.toString());
        String response = bundle.toString();
        response = response.replace("Bundle", "");

        if (response.contains("TXN_SUCCESS")) {
            new TransactionStatusAPI().execute(MID, orderId);
        } else {
            Utilities.showMessageString(context, "Paytm payment failed, please try again");
            finish();
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
            pd.dismiss();
            try {
                JSONObject object1 = new JSONObject(result);
                String string = object1.getString("OUTPUT");
                JSONObject object = new JSONObject(string);
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("type", "buyPlan");
                jsonObject.addProperty("order_gateway", "paytm");
                jsonObject.addProperty("order_status", "Success");
                jsonObject.addProperty("user_purchase_id", getIntent().getStringExtra("user_id"));
                jsonObject.addProperty("transaction_status", object.getString("STATUS"));
                jsonObject.addProperty("CHECKSUMHASH", object1.getString("CHECKSUMHASH"));
                jsonObject.addProperty("BANKNAME", object.getString("BANKNAME"));
                jsonObject.addProperty("ORDERID", object.getString("ORDERID"));
                jsonObject.addProperty("transaction_date", object.getString("TXNDATE"));
                jsonObject.addProperty("MID", object.getString("MID"));
                jsonObject.addProperty("TXNID", object.getString("TXNID"));
                jsonObject.addProperty("PAYMENTMODE", object.getString("PAYMENTMODE"));
                jsonObject.addProperty("BANKTXNID", object.getString("BANKTXNID"));
                jsonObject.addProperty("CURRENCY", "INR");
                jsonObject.addProperty("GATEWAYNAME", object.getString("GATEWAYNAME"));
                jsonObject.addProperty("RESPMSG", object.getString("RESPMSG"));
                jsonObject.addProperty("user_id", getIntent().getStringExtra("user_id"));
                jsonObject.addProperty("plan_id", getIntent().getStringExtra("plan_id"));
                jsonObject.addProperty("space", getIntent().getStringExtra("space"));
                jsonObject.addProperty("sms", getIntent().getStringExtra("sms"));
                jsonObject.addProperty("whatsApp_msg", getIntent().getStringExtra("whatsApp_msg"));
                jsonObject.addProperty("expire_date", getIntent().getStringExtra("expire_date"));
                jsonObject.addProperty("customers", getIntent().getStringExtra("clients"));
                jsonObject.addProperty("policies", getIntent().getStringExtra("policies"));
                jsonObject.addProperty("amount", getIntent().getStringExtra("amount"));
                new BuyPlan().execute(jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


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
                        if (Utilities.isInternetAvailable(context)) {
                            new UpdateCounts().execute(JSONString);
                        } else {
                            Utilities.showMessageString(context, "Please Check Internet Connection");
                        }


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
        Log.e("PaytmPayment_Activity ", " ui fail respon  " + s);
        finish();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("PaytmPayment_Activity ", " error loading pagerespon true " + s + "  s1 " + s1);
        finish();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("PaytmPayment_Activity ", " cancel call back respon  ");
        finish();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("PaytmPayment_Activity ", "  transaction cancel ");
        finish();
    }


    public class UpdateCounts extends AsyncTask<String, Void, String> {
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
            JSONString = params[0];
            String res = "[]";
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getCounts"));
            param.add(new ParamsPojo("user_id", getIntent().getStringExtra("user_id")));
            res = WebServiceCalls.FORMDATAAPICall(ApplicationConstants.PROFILEAPI, param);
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
                        JSONArray jsonArray = mainObj.getJSONArray("result");
                        JSONArray user_info = null;
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        try {
                            user_info = new JSONArray(session.getUserDetails().get(
                                    ApplicationConstants.KEY_LOGIN_INFO));
                            JSONObject json = user_info.getJSONObject(0);
                            json.put("smsCount", jsonObject.getString("smsCount"));
                            json.put("whatsappCount", jsonObject.getString("whatsappCount"));
                            json.put("maxSMSLimit", jsonObject.getString("maxSMSLimit"));
                            json.put("maxWhatsAppLimit", jsonObject.getString("maxWhatsAppLimit"));
                            json.put("maxSize", jsonObject.getString("maxSize"));
                            json.put("usedSize", jsonObject.getString("usedSize"));
                            session.updateSession(user_info.toString());

                            startActivity(new Intent(context, PlanBuySuccess_Activity.class)
                                    .putExtra("JSONString", JSONString)
                                    .putExtra("validity", getIntent().getStringExtra("validity"))
                                    .putExtra("clients", getIntent().getStringExtra("clients"))
                                    .putExtra("policies", getIntent().getStringExtra("policies")));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }


}
