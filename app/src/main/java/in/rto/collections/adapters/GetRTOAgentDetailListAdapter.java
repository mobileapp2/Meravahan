package in.rto.collections.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.EditRTOAgent_Activity;
import in.rto.collections.activities.ImportRTOCustomer_Activity;
import in.rto.collections.activities.Import_Vehicle_Dealer_Activity;
import in.rto.collections.activities.LInkRTODealer_Activity;
import in.rto.collections.activities.LinkCustomerDealer_Activity;
import in.rto.collections.activities.LinkCustomerRto_Activity;
import in.rto.collections.activities.ViewImportRTOCustomer_Activity;
import in.rto.collections.activities.ViewImportVehicleDealer_Activity;
import in.rto.collections.activities.ViewRTOAgent_Activity;
import in.rto.collections.activities.ViewRTODetails_Activity;
import in.rto.collections.fragments.Fragment_RTO_Agent;
import in.rto.collections.fragments.Fragment_RTO_Dealer_Details;
import in.rto.collections.fragments.Fragment_vehical_rto_details;
import in.rto.collections.fragments.RTO_Customer_Fragment;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.models.RTOAgentListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class GetRTOAgentDetailListAdapter extends RecyclerView.Adapter<GetRTOAgentDetailListAdapter.MyViewHolder> {

    private List<RTOAgentListPojo> resultArrayList;
   // private List<CustomerPojo> customerPojo;
    private Context context;
    private CustomerPojo customerPojo;
    RTOAgentListPojo rtoagentdetails;
    private UserSessionManager session;
    private String user_id;
    public GetRTOAgentDetailListAdapter(Context context, List<RTOAgentListPojo> resultArrayList,String user_id) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.user_id = user_id;
    }
   @Override
    public GetRTOAgentDetailListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_rto_agent, parent, false);
        GetRTOAgentDetailListAdapter.MyViewHolder myViewHolder = new GetRTOAgentDetailListAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GetRTOAgentDetailListAdapter.MyViewHolder holder, final int position) {
        rtoagentdetails = new RTOAgentListPojo();
        rtoagentdetails = resultArrayList.get(position);
        final  RTOAgentListPojo  finalRtoagentdetails = rtoagentdetails;

        holder.tv_vehicalownername.setText(rtoagentdetails.getVehicle_owner());
        holder.tv_renewaldate.setText(rtoagentdetails.getVehicle_no());
        if(rtoagentdetails.getType_id().equals("1")){
            holder.img_vehicle.setImageResource(R.drawable.twowheeler);
        }else if(rtoagentdetails.getType_id().equals("2")){
            holder.img_vehicle.setImageResource(R.drawable.car);
        }else if(rtoagentdetails.getType_id().equals("3")){
            holder.img_vehicle.setImageResource(R.drawable.passenger);
        }else if(rtoagentdetails.getType_id().equals("4")){
            holder.img_vehicle.setImageResource(R.drawable.commercial);
        }else if(rtoagentdetails.getType_id().equals("5")){
            holder.img_vehicle.setImageResource(R.drawable.three_wheeler);
        }else if(rtoagentdetails.getType_id().equals("6")){
            holder.img_vehicle.setImageResource(R.drawable.other);
        }
        if (rtoagentdetails.getImportR().equals("Import")) {
            holder.img_link.setVisibility(View.VISIBLE);
            holder.img_edit.setImageResource(R.drawable.import_r);
            holder.img_link.setImageResource(R.drawable.link);
            holder.tv_identify.setText("Created By - "+ rtoagentdetails.getCreaterName()+" (Vehicle Dealer)");
            holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImportVehicleDealer_Activity.class);
                    intent.putExtra("rtoagentDetails", finalRtoagentdetails);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.img_link.setVisibility(View.GONE);
            holder.img_edit.setImageResource(R.drawable.edit);
            holder.img_show.setVisibility(View.VISIBLE);
            holder.tv_identify.setVisibility(View.GONE);
            holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewRTOAgent_Activity.class);
                    intent.putExtra("rtoagentDetails", finalRtoagentdetails);
                    context.startActivity(intent);
                }
            });
        }
        holder.img_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (finalRtoagentdetails.getImportR().equals("Import")) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.ic_alert_red_24dp);
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (Utilities.isInternetAvailable(context)) {
                                // new Fragment_RTO_Agent.GetRTOAgentList().execute(user_id);

                                new GetRTOAgentDetailListAdapter.DeleteVehicleDealerDetails().execute(finalRtoagentdetails.getId());
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
                    android.support.v7.app.AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();
                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.ic_alert_red_24dp);
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (Utilities.isInternetAvailable(context)) {
                                new GetRTOAgentDetailListAdapter.DeleteRTOAgentDetails().execute(finalRtoagentdetails.getId());
                            } else {
                                //Utilities.showSnackBar(ll_mainlayout, "Please Check Internet Connection");
                            }

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    android.support.v7.app.AlertDialog alertD = builder.create();
                    alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                    alertD.show();
                }
            }
        });


        holder.img_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (finalRtoagentdetails.getImportR().equals("Import")) {
                    if(finalRtoagentdetails.getIsimport().equals("1")){

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("This record is already imported. Do you really want to import this record?");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, Import_Vehicle_Dealer_Activity.class);
                                intent.putExtra("rtoagentDetails", finalRtoagentdetails);
                                context.startActivity(intent);
                                //finish();
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish();
                            }
                        });
                        android.support.v7.app.AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    }else{
                        Intent intent = new Intent(context, Import_Vehicle_Dealer_Activity.class);
                        intent.putExtra("rtoagentDetails", finalRtoagentdetails);
                        context.startActivity(intent);
                    }

                }else{
                    Intent intent = new Intent(context, EditRTOAgent_Activity.class);
                    intent.putExtra("rtoagentDetails", finalRtoagentdetails);
                    context.startActivity(intent);
                }
            }
        });
        holder.img_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalRtoagentdetails.getImportR().equals("Import")){
                    Intent intent = new Intent(context, LInkRTODealer_Activity.class);
                    intent.putExtra("rtoDetails", finalRtoagentdetails);
                    context.startActivity(intent);
                }
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
                if (!finalRtoagentdetails.getVehicle_image().equals("")) {
                    Picasso.with(context)
                            .load(finalRtoagentdetails.getVehicle_image_url())
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
    }
    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class DeleteVehicleDealerDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isdelete");
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

                        new Fragment_RTO_Dealer_Details.GetRTOAgentList().execute(user_id);


                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Vehicle  Details Deleted Successfully");
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


