package in.rto.collections.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import in.rto.collections.R;
import in.rto.collections.activities.CarIqMapViewFromNotification_Activity;
import in.rto.collections.activities.CarIqWeeklySummary_Activity;
import in.rto.collections.models.CariqNotificationModel;

public class GetNotificationCarIqListAdapter extends RecyclerView.Adapter<GetNotificationCarIqListAdapter.MyViewHolder> {

    private List<CariqNotificationModel.RowsBean> resultArrayList;
    private Context context;

    public GetNotificationCarIqListAdapter(Context context, List<CariqNotificationModel.RowsBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_notification_cariq, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CariqNotificationModel.RowsBean cardetails = resultArrayList.get(position);
        PrettyTime p = new PrettyTime();

        holder.tv_message.setText(cardetails.getMessage());
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            holder.tv_time.setText(p.format(formatter.parse(cardetails.getLogtime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardetails.getItsType().equals("WeeklySummary")) {
                    context.startActivity(new Intent(context, CarIqWeeklySummary_Activity.class)
                            .putExtra("url", cardetails.getItsValue()));
                } else {
                    context.startActivity(new Intent(context, CarIqMapViewFromNotification_Activity.class)
                            .putExtra("latitude", cardetails.getLatitude())
                            .putExtra("longitude", cardetails.getLongitude())
                            .putExtra("datetime", cardetails.getLogtime())
                            .putExtra("type", cardetails.getItsType()));
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
        private TextView tv_message, tv_time;

        public MyViewHolder(View view) {
            super(view);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            tv_message = view.findViewById(R.id.tv_message);
            tv_time = view.findViewById(R.id.tv_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

