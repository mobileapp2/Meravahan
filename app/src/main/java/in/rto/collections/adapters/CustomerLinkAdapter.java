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
import in.rto.collections.activities.LinkToCustomer_Activity;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.LinkPojo;

public class CustomerLinkAdapter extends RecyclerView.Adapter<CustomerLinkAdapter.MyViewHolder> {

    private List<CustomerPojo> resultArrayList;
    private List<LinkPojo> resultArrayListLink;
    private Context context;
    private String user_id;
    int selectedPosition = -1;

    public CustomerLinkAdapter(Context context, List<CustomerPojo> resultArrayList, List<LinkPojo> resultArrayListLink) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.resultArrayListLink = resultArrayListLink;
        this.user_id = user_id;
    }

    @Override
    public CustomerLinkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_link, parent, false);
        CustomerLinkAdapter.MyViewHolder myViewHolder = new CustomerLinkAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomerLinkAdapter.MyViewHolder holder, final int position) {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo = resultArrayList.get(position);
        LinkPojo linkPojo = new LinkPojo();
        linkPojo = resultArrayListLink.get(position);
        final CustomerPojo finalcustomerPojo = customerPojo;
        final LinkPojo linkPojo1 = linkPojo;

        holder.tv_vehicalownername.setText(customerPojo.getVehicle_owner());
        holder.tv_renewaldate.setText(customerPojo.getVehicle_no());

        if (customerPojo.getType_id().equals("1")) {
            holder.img_vehicle.setImageResource(R.drawable.twowheeler);
        } else if (customerPojo.getType_id().equals("2")) {
            holder.img_vehicle.setImageResource(R.drawable.car);
        } else if (customerPojo.getType_id().equals("3")) {
            holder.img_vehicle.setImageResource(R.drawable.passenger);
        } else if (customerPojo.getType_id().equals("4")) {
            holder.img_vehicle.setImageResource(R.drawable.commercial);
        } else if (customerPojo.getType_id().equals("5")) {
            holder.img_vehicle.setImageResource(R.drawable.three_wheeler);
        } else if (customerPojo.getType_id().equals("6")) {
            holder.img_vehicle.setImageResource(R.drawable.other);
        }
        if (selectedPosition == position) {
            LinkToCustomer_Activity.selectedposition = position;
            holder.ll_mainlayout.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            LinkToCustomer_Activity.selectedposition = selectedPosition;
            holder.ll_mainlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                //LinkToCustomer_Activity.selectedposition = position;
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
