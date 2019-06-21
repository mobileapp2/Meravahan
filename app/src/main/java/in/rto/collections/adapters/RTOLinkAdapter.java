package in.rto.collections.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.EditRTOAgent_Activity;
import in.rto.collections.activities.Import_Vehicle_Dealer_Activity;
import in.rto.collections.activities.LinkToDealer_Activity;
import in.rto.collections.activities.LinkToRTO_Activity;
import in.rto.collections.activities.Link_Edit_RTO;
import in.rto.collections.activities.ViewImportVehicleDealer_Activity;
import in.rto.collections.activities.ViewRTOAgent_Activity;
import in.rto.collections.fragments.Fragment_RTO_Agent;
import in.rto.collections.fragments.Fragment_RTO_Dealer_Details;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.LinkPojo;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class RTOLinkAdapter extends RecyclerView.Adapter<RTOLinkAdapter.MyViewHolder> {

    private List<RTOAgentListPojo> resultArrayList;
    // private List<CustomerPojo> customerPojo;
    private Context context;
    private CustomerPojo customerPojo;
    RTOAgentListPojo rtoagentdetails;
    private UserSessionManager session;
    private String user_id;
    private List<LinkPojo> resultArrayListLink;
    int selectedPosition=-1;
    public RTOLinkAdapter(Context context, List<RTOAgentListPojo> resultArrayList,List<LinkPojo> resultArrayListLink) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.resultArrayListLink = resultArrayListLink;
    }

    @Override
    public RTOLinkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_link, parent, false);
        RTOLinkAdapter.MyViewHolder myViewHolder = new RTOLinkAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final RTOLinkAdapter.MyViewHolder holder, final int position) {
        rtoagentdetails = new RTOAgentListPojo();
        rtoagentdetails = resultArrayList.get(position);
        final RTOAgentListPojo finalRtoagentdetails = rtoagentdetails;

        LinkPojo linkPojo = new LinkPojo();
        linkPojo = resultArrayListLink.get(position);
        final LinkPojo linkPojo1 = linkPojo;

        holder.tv_vehicalownername.setText(rtoagentdetails.getVehicle_owner());
        holder.tv_renewaldate.setText(rtoagentdetails.getVehicle_no());
        if (rtoagentdetails.getType_id().equals("1")) {
            holder.img_vehicle.setImageResource(R.drawable.twowheeler);
        } else if (rtoagentdetails.getType_id().equals("2")) {
            holder.img_vehicle.setImageResource(R.drawable.car);
        } else if (rtoagentdetails.getType_id().equals("3")) {
            holder.img_vehicle.setImageResource(R.drawable.passenger);
        } else if (rtoagentdetails.getType_id().equals("4")) {
            holder.img_vehicle.setImageResource(R.drawable.commercial);
        } else if (rtoagentdetails.getType_id().equals("5")) {
            holder.img_vehicle.setImageResource(R.drawable.three_wheeler);
        } else if (rtoagentdetails.getType_id().equals("6")) {
            holder.img_vehicle.setImageResource(R.drawable.other);
        }

        if(selectedPosition==position){
            LinkToRTO_Activity.selectedposition = position;
            holder.ll_mainlayout.setBackgroundColor(Color.parseColor("#ff33b5e5"));}
        else{
        LinkToRTO_Activity.selectedposition = selectedPosition;
            holder.ll_mainlayout.setBackgroundColor(Color.parseColor("#ffffff"));}

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=position;
               // LinkToRTO_Activity.selectedposition = position;
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
           // tv_identify = view.findViewById(R.id.tv_identify);
        }
    }
}