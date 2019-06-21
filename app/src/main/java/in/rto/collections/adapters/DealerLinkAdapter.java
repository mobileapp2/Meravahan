package in.rto.collections.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.LinkToDealer_Activity;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.VehicleDealerListPojo;

public class DealerLinkAdapter extends RecyclerView.Adapter<DealerLinkAdapter.MyViewHolder> {

    private List<VehicleDealerListPojo> resultArrayList;
    private Context context;
    private String user_id;
    private List<LinkPojo> resultArrayListLink;
    int selectedPosition = -1;

    public DealerLinkAdapter(Context context, List<VehicleDealerListPojo> resultArrayList, List<LinkPojo> resultArrayListLink) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.resultArrayListLink = resultArrayListLink;
    }

    @Override
    public DealerLinkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_link, parent, false);
        DealerLinkAdapter.MyViewHolder myViewHolder = new DealerLinkAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final DealerLinkAdapter.MyViewHolder holder, final int position) {
        VehicleDealerListPojo vehicleDetails = new VehicleDealerListPojo();
        vehicleDetails = resultArrayList.get(position);
        final VehicleDealerListPojo finalvehicleDetails = vehicleDetails;

        LinkPojo linkPojo = new LinkPojo();
        linkPojo = resultArrayListLink.get(position);
        final LinkPojo linkPojo1 = linkPojo;

        holder.tv_vehicalownername.setText(vehicleDetails.getVehicle_owner());
        holder.tv_renewaldate.setText(vehicleDetails.getVehicle_no());
        if (vehicleDetails.getType_id().equals("1")) {
            holder.img_vehicle.setImageResource(R.drawable.twowheeler);
        } else if (vehicleDetails.getType_id().equals("2")) {
            holder.img_vehicle.setImageResource(R.drawable.car);
        } else if (vehicleDetails.getType_id().equals("3")) {
            holder.img_vehicle.setImageResource(R.drawable.passenger);
        } else if (vehicleDetails.getType_id().equals("4")) {
            holder.img_vehicle.setImageResource(R.drawable.commercial);
        } else if (vehicleDetails.getType_id().equals("5")) {
            holder.img_vehicle.setImageResource(R.drawable.three_wheeler);
        } else if (vehicleDetails.getType_id().equals("6")) {
            holder.img_vehicle.setImageResource(R.drawable.other);
        }

        if (selectedPosition == position) {
            LinkToDealer_Activity.selectedposition = position;
            holder.ll_mainlayout.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            LinkToDealer_Activity.selectedposition = selectedPosition;
            holder.ll_mainlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                // LinkToDealer_Activity.selectedposition = position;
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_vehicalownername, tv_renewaldate, tv_identify;
        private CardView ll_mainlayout;
        ImageView img_delete, img_edit, img_vehicle;

        public MyViewHolder(View view) {
            super(view);
            tv_vehicalownername = view.findViewById(R.id.tv_ownername);
            tv_renewaldate = view.findViewById(R.id.tv_renewaldate);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            img_delete = view.findViewById(R.id.img_delete);
            img_edit = view.findViewById(R.id.img_edit);
            img_vehicle = view.findViewById(R.id.img_vehicle);
            tv_identify = view.findViewById(R.id.tv_identify);
        }
    }

}

