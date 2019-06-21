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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.EditVehicleDealer_Activity;
import in.rto.collections.activities.Import_Banker_Dealer_Activity;
import in.rto.collections.activities.Import_RTO_Agent_Activity;
import in.rto.collections.activities.Import_Vehicle_Dealer_Activity;
import in.rto.collections.activities.LInkDealerRTO_Activity;
import in.rto.collections.activities.LinkDealerBanker_Activity;
import in.rto.collections.activities.ViewImportCustomerBanker_Activity;
import in.rto.collections.activities.ViewImportDealerBanker_Activity;
import in.rto.collections.activities.ViewRTODetails_Activity;
import in.rto.collections.activities.ViewVehicleDetails_Activity;
import in.rto.collections.fragments.Fragment_vehical_rto_details;
import in.rto.collections.fragments.Fragment_vehicle_dealer;
import in.rto.collections.models.VehicleDealerListPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class GetVehicleDealerDetailsListAdapter extends RecyclerView.Adapter<GetVehicleDealerDetailsListAdapter.MyViewHolder> {

private List<VehicleDealerListPojo> resultArrayList;
private Context context;
private  String user_id;
public GetVehicleDealerDetailsListAdapter(Context context, List<VehicleDealerListPojo> resultArrayList,String user_id) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.user_id = user_id;
        }

@Override
public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_vehicle_dealer, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
        }

@Override
public void onBindViewHolder(final MyViewHolder holder, final int position) {
        VehicleDealerListPojo vehicleDetails = new VehicleDealerListPojo();
        vehicleDetails = resultArrayList.get(position);
final VehicleDealerListPojo finalvehicleDetails = vehicleDetails;

        holder.tv_vehicalownername.setText(vehicleDetails.getVehicle_owner());
        holder.tv_renewaldate.setText(vehicleDetails.getVehicle_no());
    if(!vehicleDetails.getImportR().equals("ImportBanker")) {
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
    }
    if(vehicleDetails.getImportR().equals("Import")) {
        holder.img_link.setVisibility(View.VISIBLE);
        holder.img_edit.setImageResource(R.drawable.import_r);
        holder.img_link.setImageResource(R.drawable.link);
        holder.tv_identify.setText("Created By - "+ vehicleDetails.getCreaterName()+" (RTO Agent)");
        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewRTODetails_Activity.class);
                intent.putExtra("vehicleDetails", finalvehicleDetails);
                context.startActivity(intent);
            }
        });
    } else if(vehicleDetails.getImportR().equals("ImportBanker")) {
        holder.img_link.setVisibility(View.VISIBLE);
        holder.img_edit.setImageResource(R.drawable.import_r);
        holder.img_link.setImageResource(R.drawable.link);
        holder.tv_identify.setText("Created By - "+ vehicleDetails.getCreaterName()+" (Banker)");
        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewImportDealerBanker_Activity.class);
                intent.putExtra("bankerdetails", finalvehicleDetails);
                context.startActivity(intent);
            }
        });
    }else{
        holder.img_link.setVisibility(View.GONE);
        holder.img_show.setVisibility(View.VISIBLE);
        holder.img_edit.setImageResource(R.drawable.edit);
        holder.tv_identify.setVisibility(View.GONE);
        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Intent intent = new Intent(context, ViewVehicleDetails_Activity.class);
        intent.putExtra("vehicleDetails", finalvehicleDetails);
        context.startActivity(intent);
            }
        });
    }

    final VehicleDealerListPojo finalVehicleDetails = vehicleDetails;
    holder.img_delete.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(finalVehicleDetails.getImportR().equals("Import")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Utilities.isInternetAvailable(context)) {
                            new GetVehicleDealerDetailsListAdapter.DeleteVehicleDealerDetails().execute(finalVehicleDetails.getId());
                        } else {
                           // Utilities.showSnackBar(ll_ma, "Please Check Internet Connection");
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
            }  else if(finalVehicleDetails.getImportR().equals("ImportBanker")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Utilities.isInternetAvailable(context)) {
                            new GetVehicleDealerDetailsListAdapter.DeleteBankerDetails().execute(finalVehicleDetails.getId());
                        } else {
                            // Utilities.showSnackBar(ll_ma, "Please Check Internet Connection");
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
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.ic_alert_red_24dp);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Utilities.isInternetAvailable(context)) {
                            new GetVehicleDealerDetailsListAdapter.DeleteVehicleDealerDetails1().execute(finalVehicleDetails.getId());
                        } else {
                            //Utilities.showSnackBar(ll_parent, "Please Check Internet Connection");
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
        }
    });


    holder.img_edit.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(finalVehicleDetails.getImportR().equals("Import")) {
                if(finalVehicleDetails.getIsimport().equals("1")) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("This record is already imported. Do you really want to import this record?");
                    builder.setIcon(R.drawable.ic_success_24dp);
                    builder.setTitle("Success");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(context, Import_RTO_Agent_Activity.class);
                            intent.putExtra("vehicleDetails", finalVehicleDetails);
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
                    Intent intent = new Intent(context, Import_RTO_Agent_Activity.class);
                    intent.putExtra("vehicleDetails", finalVehicleDetails);
                    context.startActivity(intent);
                }
            } else if(finalVehicleDetails.getImportR().equals("ImportBanker")) {
                if(finalVehicleDetails.getIsimport().equals("1")) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("This record is already imported. Do you really want to import this record?");
                    builder.setIcon(R.drawable.ic_success_24dp);
                    builder.setTitle("Success");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(context, Import_Banker_Dealer_Activity.class);
                            intent.putExtra("vehicleDetails", finalVehicleDetails);
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
                    Intent intent = new Intent(context, Import_Banker_Dealer_Activity.class);
                    intent.putExtra("vehicleDetails", finalVehicleDetails);
                    context.startActivity(intent);
                }
            }else{
                Intent intent = new Intent(context, EditVehicleDealer_Activity.class);
                intent.putExtra("vehicleDetails", finalVehicleDetails);
                context.startActivity(intent);
            }
        }
    });
    holder.img_link.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(finalVehicleDetails.getImportR().equals("ImportBanker")){
                Intent intent = new Intent(context, LinkDealerBanker_Activity.class);
                intent.putExtra("vehicleDetails", finalVehicleDetails);
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, LInkDealerRTO_Activity.class);
                intent.putExtra("vehicleDetails", finalVehicleDetails);
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
            if (!finalVehicleDetails.getVehicle_image().equals("")) {
                Picasso.with(context)
                        .load(finalVehicleDetails.getVehicle_image_url())
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
        img_link=view.findViewById(R.id.img_link);
        img_vehicle =view.findViewById(R.id.img_vehicle);
        tv_identify = view.findViewById(R.id.tv_identify);
        img_show = view.findViewById(R.id.img_show);
    }
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.VEHICLEDEALER, obj.toString());
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

                        new Fragment_vehical_rto_details.GetVehicleDealerList().execute(user_id);

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
            obj.addProperty("type", "isdeleteBanker");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.VEHICLEDEALER, obj.toString());
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

                        new Fragment_vehical_rto_details.GetVehicleDealerList().execute(user_id);

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

    public class DeleteVehicleDealerDetails1 extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.VEHICLEDEALER, obj.toString());
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

                        new Fragment_vehicle_dealer.GetVehicleDealerList().execute(user_id);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Vehicle Dealer Details Deleted Successfully");
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


}

