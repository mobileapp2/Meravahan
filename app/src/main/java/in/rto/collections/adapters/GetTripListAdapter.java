package in.rto.collections.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.rto.collections.R;
import in.rto.collections.activities.TripListAccToLocation_Activity;
import in.rto.collections.models.TripListModel;

public class GetTripListAdapter extends RecyclerView.Adapter<GetTripListAdapter.MyViewHolder> {

    private List<TripListModel> resultArrayList;
    private Context context;

    public GetTripListAdapter(Context context, List<TripListModel> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_trip, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TripListModel tripDetails = resultArrayList.get(position);

        holder.tv_date.setText(changeDateFormat(tripDetails.getTripDate()));
        holder.tv_kmcovered.setText(String.format("%.2f", Double.parseDouble(tripDetails.getKmCovered())) + " km");

        holder.ll_mainrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, TripListAccToLocation_Activity.class)
                        .putExtra("selectedDate", tripDetails.getTripDate()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_mainrow;
        private TextView tv_date, tv_kmcovered;

        public MyViewHolder(View view) {
            super(view);
            ll_mainrow = view.findViewById(R.id.ll_mainrow);
            tv_date = view.findViewById(R.id.tv_date);
            tv_kmcovered = view.findViewById(R.id.tv_kmcovered);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String changeDateFormat(String dateString) {
        String result = "";
        if (dateString.equals("")) {
            return "";
        }
        SimpleDateFormat formatterOld = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formatterNew = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = formatterOld.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            result = formatterNew.format(date);
        }
        return result;
    }

}


