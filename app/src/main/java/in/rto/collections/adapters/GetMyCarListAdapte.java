package in.rto.collections.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.models.MyCarListModel;

public class GetMyCarListAdapte extends RecyclerView.Adapter<GetMyCarListAdapte.MyViewHolder> {

    private List<MyCarListModel.ResultBean> resultArrayList;
    private Context context;

    public GetMyCarListAdapte(Context context, List<MyCarListModel.ResultBean> resultArrayList) {
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
        MyCarListModel.ResultBean cardetails = resultArrayList.get(position);

        holder.tv_carname.setText(cardetails.getMake() + " (" + cardetails.getModel() + " " + cardetails.getVariant() + ")");
        holder.tv_carnumber.setText(cardetails.getRegistration_number());

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView ll_mainlayout;
        private TextView tv_carname, tv_carnumber;

        public MyViewHolder(View view) {
            super(view);
            tv_carname = view.findViewById(R.id.tv_carname);
            tv_carnumber = view.findViewById(R.id.tv_carnumber);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
        }
    }
}


