package in.rto.collections.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.ViewTyre_Activity;
import in.rto.collections.models.TyreDetailsPojo;

public class TyreListAdapter  extends RecyclerView.Adapter<TyreListAdapter.MyViewHolder> {

    private List<TyreDetailsPojo> resultArrayList;
    private Context context;

    public TyreListAdapter(Context context, List<TyreDetailsPojo> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public TyreListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_tyre, parent, false);
        TyreListAdapter.MyViewHolder myViewHolder = new TyreListAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final TyreListAdapter.MyViewHolder holder, final int position) {
        TyreDetailsPojo tyredetails = new TyreDetailsPojo();
        tyredetails = resultArrayList.get(position);
        final TyreDetailsPojo finaltyredetails = tyredetails;

        holder.tv_tyreno.setText(((TyreDetailsPojo) tyredetails).getTyre_no());
        holder.tv_purchasedate.setText(((TyreDetailsPojo) tyredetails).getClient_name());

        holder.tv_tyreno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewTyre_Activity.class);
                intent.putExtra("tyreDetails", finaltyredetails);
                context.startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_tyreno,tv_purchasedate;
        private LinearLayout ll_mainlayout;
        public MyViewHolder(View view) {
            super(view);
            tv_tyreno = view.findViewById(R.id.tv_tyreno   );
            tv_purchasedate = view.findViewById(R.id.tv_purchasedate);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
        }
    }
}



