package in.rto.collections.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.EditRTOAgent_Activity;
import in.rto.collections.activities.Edit_BankerDetails_Activity;
import in.rto.collections.activities.Import_Vehicle_Dealer_Activity;
import in.rto.collections.activities.LInkRTODealer_Activity;
import in.rto.collections.activities.ViewImportVehicleDealer_Activity;
import in.rto.collections.activities.ViewRTOAgent_Activity;
import in.rto.collections.activities.View_Banker_details_Activity;
import in.rto.collections.fragments.Fragment_Banker_Vehicle_Details;
import in.rto.collections.fragments.Fragment_RTO_Agent;
import in.rto.collections.fragments.Fragment_RTO_Dealer_Details;
import in.rto.collections.fragments.Self_Fragment;
import in.rto.collections.models.BankerDetailsPojo;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class GetBankerDetailsListAdapter extends RecyclerView.Adapter<GetBankerDetailsListAdapter.MyViewHolder> {

    private List<BankerDetailsPojo> resultArrayList;
    // private List<CustomerPojo> customerPojo;
    private Context context;
    private CustomerPojo customerPojo;
    BankerDetailsPojo bankerDetailsPojo;
    private UserSessionManager session;
    private String user_id;
    public GetBankerDetailsListAdapter(Context context, List<BankerDetailsPojo> resultArrayList, String user_id) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.user_id = user_id;
    }
    @Override
    public GetBankerDetailsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_rto_agent, parent, false);
        GetBankerDetailsListAdapter.MyViewHolder myViewHolder = new GetBankerDetailsListAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GetBankerDetailsListAdapter.MyViewHolder holder, final int position) {
        bankerDetailsPojo = new BankerDetailsPojo();
        bankerDetailsPojo = resultArrayList.get(position);
        final  BankerDetailsPojo  bankerDetailsPojo1 = bankerDetailsPojo;

        holder.tv_vehicalownername.setText(bankerDetailsPojo.getBorrower_name());
        holder.tv_renewaldate.setText(bankerDetailsPojo.getVehicle_number());

        holder.img_link.setVisibility(View.GONE);
        holder.img_edit.setImageResource(R.drawable.edit);
        holder.img_show.setVisibility(View.VISIBLE);
            holder.tv_identify.setVisibility(View.GONE);
            holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, View_Banker_details_Activity.class);
                    intent.putExtra("bankerdetails", bankerDetailsPojo1);
                    context.startActivity(intent);
                }
            });
        holder.img_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.prompt_fullvehiclepic, null);
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setView(promptView);

                ImageView imv_fullpic = promptView.findViewById(R.id.imv_fullpic);
                TextView imv_text = promptView.findViewById(R.id.imv_text);
                if (!bankerDetailsPojo1.getVehicle_image().equals("")) {
                    Picasso.with(context)
                            .load(bankerDetailsPojo1.getVehicle_image_url())
                            .placeholder(R.drawable.img_product)
                            .into(imv_fullpic);
                }else{
                    imv_fullpic.setVisibility(View.GONE);
                    imv_text.setVisibility(View.VISIBLE);
                    imv_text.setText("Image not available.");
                }
                android.support.v7.app.AlertDialog alertD = alertDialogBuilder.create();
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
            }
        });
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Utilities.isInternetAvailable(context)) {
                            new GetBankerDetailsListAdapter.DeleteBankerDetails().execute(bankerDetailsPojo.getId());
                        } else {
                           // Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                alertD.show();
            }
        });
        holder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Edit_BankerDetails_Activity.class);
                intent.putExtra("bankerDetails", bankerDetailsPojo);
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class DeleteBankerDetails extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "delete");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.BANKERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {

                        new Fragment_Banker_Vehicle_Details.GetBankerList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Vehicle Details Deleted Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    } else {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class DeleteRTOAgentDetails extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "delete");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.RTOAGENTAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        // new Fragment_RTO_Agent.GetRTOAgentList().execute();
                        new Fragment_RTO_Agent.GetRTOAgentList().execute(user_id);

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Vehicle Details Deleted Successfully");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //finish();
                            }
                        });
                        android.support.v7.app.AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    } else {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_vehicalownername,tv_renewaldate,tv_identify;
        private CardView ll_mainlayout;
        ImageView img_delete,img_edit,img_vehicle,img_link,img_show;
        public MyViewHolder(View view) {
            super(view);
            tv_vehicalownername = view.findViewById(R.id.tv_ownername   );
            tv_renewaldate = view.findViewById(R.id.tv_renewaldate);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
            img_delete =view.findViewById(R.id.img_delete);
            img_edit =view.findViewById(R.id.img_edit);
            img_link =view.findViewById(R.id.img_link);
            img_vehicle =view.findViewById(R.id.img_vehicle);
            tv_identify = view.findViewById(R.id.tv_identify);
            img_show = view.findViewById(R.id.img_show);
        }
    }
}



