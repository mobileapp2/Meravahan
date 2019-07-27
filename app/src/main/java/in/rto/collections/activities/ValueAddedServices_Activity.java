package in.rto.collections.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.rto.collections.R;
import in.rto.collections.models.VASListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

public class ValueAddedServices_Activity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private EditText edt_type, edt_mobile, edt_description;
    private Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_added_services);

        init();
        getSessionData();
        setDefault();
        setEventHandlers();
        setUpToolbar();
    }

    private void init() {
        context = ValueAddedServices_Activity.this;
        session = new UserSessionManager(context);
        edt_type = findViewById(R.id.edt_type);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_description = findViewById(R.id.edt_description);
        btn_send = findViewById(R.id.btn_send);
    }

    private void getSessionData() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            edt_mobile.setText(json.getString("mobile"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<VASListModel> vasList = new ArrayList<>();
                vasList.add(new VASListModel("1", "Request for tracking device"));
                vasList.add(new VASListModel("2", "Request for fuel card"));
                vasList.add(new VASListModel("3", "Request for Fuel efficiency"));

                showRequestListDialog(vasList);
            }
        });
    }

    private void setEventHandlers() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void showRequestListDialog(final ArrayList<VASListModel> makerList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Maker");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < makerList.size(); i++) {

            arrayAdapter.add(String.valueOf(makerList.get(i).getName()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_type.setText(makerList.get(which).getName());
            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }


    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Value Added Services");
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
