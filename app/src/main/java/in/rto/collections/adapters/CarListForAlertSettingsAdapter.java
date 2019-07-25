package in.rto.collections.adapters;

import android.content.Context;
import android.content.Intent;
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
import in.rto.collections.activities.CariqCarAlertSettings_Activity;
import in.rto.collections.activities.MainDrawer_Activity;
import in.rto.collections.fragments.LastSeenTracking_Fragment;
import in.rto.collections.fragments.LiveTracking_Fragment;
import in.rto.collections.fragments.Tracking_Fragment;
import in.rto.collections.fragments.VehicleForTracking_Fragment;
import in.rto.collections.fragments.VehicleTripList_Fragment;
import in.rto.collections.models.MyCarListModel;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;

public class CarListForAlertSettingsAdapter extends RecyclerView.Adapter<CarListForAlertSettingsAdapter.MyViewHolder> {

    private List<MyCarListModel> resultArrayList;
    private Context context;

    public CarListForAlertSettingsAdapter(Context context, List<MyCarListModel> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
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
        holder.tv_carname.setText(cardetails.getMake() + " (" + cardetails.getModel() + " " + cardetails.getFuelType() + ")");
        holder.tv_carnumber.setText(cardetails.getRegistrationNumber());
        holder.ll_enable.setVisibility(View.GONE);

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, CariqCarAlertSettings_Activity.class)
                        .putExtra("carId", cardetails.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView ll_mainlayout;
        private LinearLayout ll_enable;
        private TextView tv_carname, tv_carnumber;

        public MyViewHolder(View view) {
            super(view);
            tv_carname = view.findViewById(R.id.tv_carname);
            tv_carnumber = view.findViewById(R.id.tv_carnumber);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            ll_enable = view.findViewById(R.id.ll_enable);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}


