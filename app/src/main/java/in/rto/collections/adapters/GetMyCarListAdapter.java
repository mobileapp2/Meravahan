package in.rto.collections.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.CarIqCarsList_Activity;
import in.rto.collections.activities.MainDrawer_Activity;
import in.rto.collections.fragments.LastSeenTracking_Fragment;
import in.rto.collections.fragments.LiveTracking_Fragment;
import in.rto.collections.fragments.Tracking_Fragment;
import in.rto.collections.fragments.VehicleForTracking_Fragment;
import in.rto.collections.fragments.VehicleTripList_Fragment;
import in.rto.collections.models.MyCarListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

public class GetMyCarListAdapter extends RecyclerView.Adapter<GetMyCarListAdapter.MyViewHolder> {

    private List<MyCarListModel> resultArrayList;
    private Context context;
    private String type;
    private UserSessionManager session;
    private String enabledCarIq;

    public GetMyCarListAdapter(Context context, List<MyCarListModel> resultArrayList, String type) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.type = type;
        session = new UserSessionManager(context);


        enabledCarIq = session.getEnableCarTrackingDetails().get(
                ApplicationConstants.CARIQ_ENABLED_CARID);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_car, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MyCarListModel cardetails = resultArrayList.get(position);

        if (enabledCarIq != null) {
            if (enabledCarIq.equals(cardetails.getId())) {
                holder.btn_enabletrack.setText("Tracking Enabled");
            }
        }

        holder.tv_carname.setText(cardetails.getMake() + " (" + cardetails.getModel() + " " + cardetails.getFuelType() + ")");
        holder.tv_carnumber.setText(cardetails.getRegistrationNumber());
        holder.tv_poweredbycariq.setText(Html.fromHtml("Powered by " + "<b>" + "cariq" + "</b>"));

        holder.btn_enabletrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btn_enabletrack.setText("Tracking Enabled");
                session.createEnableCarTrackingSession(cardetails.getId());
//                new CarIqCarsList_Activity.GetCarList().execute();
                VehicleForTracking_Fragment.setDefault();
                LiveTracking_Fragment.getSessionDetails();
                LastSeenTracking_Fragment.getSessionDetails();
                VehicleTripList_Fragment.getSessionDetails();

                if (type.equals("1")) {
                    CarIqCarsList_Activity.activity.finish();
                    MainDrawer_Activity.changePage();
                    Tracking_Fragment.changePage();
                } else if (type.equals("2")) {
                    Tracking_Fragment.changePage();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView ll_mainlayout;
        private Button btn_enabletrack;
        private LinearLayout ll_enable;
        private TextView tv_carname, tv_carnumber, tv_poweredbycariq;

        public MyViewHolder(View view) {
            super(view);
            tv_carname = view.findViewById(R.id.tv_carname);
            tv_carnumber = view.findViewById(R.id.tv_carnumber);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            tv_poweredbycariq = view.findViewById(R.id.tv_poweredbycariq);
            ll_enable = view.findViewById(R.id.ll_enable);
            btn_enabletrack = view.findViewById(R.id.btn_enabletrack);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}


