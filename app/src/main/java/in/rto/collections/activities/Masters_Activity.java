package in.rto.collections.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

//import com.insurance.todojee.R;

import org.json.JSONArray;
import org.json.JSONObject;

import in.rto.collections.R;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

public class Masters_Activity extends Activity {

    private Context context;
    private UserSessionManager session;
    private String user_id,role_id;
    private CardView cc,rto_agent,vehicle_dealer;
    private LinearLayout banksection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masters);


        init();
        getSessionData();
        setUpToolbar();
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
            role_id = json.getString("role_id");
            if(role_id.equals("1")) {
                rto_agent.setVisibility(View.GONE);
                vehicle_dealer.setVisibility(View.VISIBLE);
                cc.setVisibility(View.VISIBLE);
            }
            if(role_id.equals("2")) {
                rto_agent.setVisibility(View.VISIBLE);
                vehicle_dealer.setVisibility(View.GONE);
                cc.setVisibility(View.VISIBLE);
            }
            if(role_id.equals("3")){
                rto_agent.setVisibility(View.GONE);
                vehicle_dealer.setVisibility(View.GONE);
               // banksection.setVisibility(View.VISIBLE);
            }
            if(role_id.equals("4")){
                rto_agent.setVisibility(View.GONE);
              //  banksection.setVisibility(View.VISIBLE);
            }
            if(role_id.equals("6")) {
                rto_agent.setVisibility(View.GONE);
                vehicle_dealer.setVisibility(View.GONE);
                cc.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {

        context = Masters_Activity.this;
        cc = findViewById(R.id.client_code);
        rto_agent = findViewById(R.id.rto_agent);
        vehicle_dealer = findViewById(R.id.vehicle_dealer);
        session = new UserSessionManager(context);
        banksection = findViewById(R.id.banksection);

    }

    public void openClientCode(View v) {
        startActivity(new Intent(context, ClientCode.class));
    }

    public void openRtoAgent(View v) {
       startActivity(new Intent(context, RtoAgent_Activity.class));
    }

    public void openVehicleDealer(View v) {
       startActivity(new Intent(context, VehicleDealer_Activity.class));
    }

    public void openFamilyCode(View v) {
       // startActivity(new Intent(context, MastersFamiliyCode_Activity.class));
    }
    public void openNewPoducts(View v) {
        startActivity(new Intent(context, MastersProductList_Activity.class));
    }

    public void openBank(View v) {
        startActivity(new Intent(context, Master_Bank.class));
    }
    public void openBranch(View v) {
        startActivity(new Intent(context, Master_branch_details.class));
    }
    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Masters");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
