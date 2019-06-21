package in.rto.collections.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.rto.collections.R;
import in.rto.collections.models.InformationListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

public class Information_activity extends Activity {
    public LinearLayout ll_parent;
    private Context context;
    private RecyclerView rv_info;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_nothingtoshow;
    private LinearLayoutManager layoutManager;
    private UserSessionManager session;
    private String user_id;
    private ArrayList<InformationListPojo> informationListPojos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        init();
        getSessionData();
        setDefault();
        //setEventHandlers();

    }

    private void init() {
        context = Information_activity.this;
        session = new UserSessionManager(context);
        ll_parent = findViewById(R.id.ll_parent);
        ll_nothingtoshow = findViewById(R.id.ll_nothingtoshow);
        rv_info = findViewById(R.id.rv_info);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);


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

    private void setDefault() {
        informationListPojos = new ArrayList<>();
        InformationListPojo informationMainObj = new InformationListPojo();
        informationMainObj.setId("1");
        informationMainObj.setName("Temporary Registration");
        informationMainObj.setDocument("https://gstkhata.com/vehicle/images/product.png");
        informationMainObj.setId("2");
        informationMainObj.setName("Permanent Registration");
        informationMainObj.setDocument("https://gstkhata.com/vehicle/images/product.png");
        informationListPojos.add(informationMainObj);
        rv_info.setAdapter(new GetInformationListAdapter());
    }

    public class GetInformationListAdapter extends RecyclerView.Adapter<Information_activity.GetInformationListAdapter.MyViewHolder> {

        @Override
        public Information_activity.GetInformationListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_information, parent, false);
            Information_activity.GetInformationListAdapter.MyViewHolder myViewHolder = new Information_activity.GetInformationListAdapter.MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final Information_activity.GetInformationListAdapter.MyViewHolder holder, final int position) {
            InformationListPojo informationDetails = new InformationListPojo();
            informationDetails = informationListPojos.get(position);

            holder.tv_text.setText(informationDetails.getName());
            // holder.img_icon.setImageIcon(informationDetails.getName());
        }

        @Override
        public int getItemCount() {
            return informationListPojos.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_text;
            private ImageView img_icon;

            public MyViewHolder(View view) {
                super(view);
                tv_text = view.findViewById(R.id.tv_text);
                img_icon = view.findViewById(R.id.img_icon);
            }
        }

    }


}
