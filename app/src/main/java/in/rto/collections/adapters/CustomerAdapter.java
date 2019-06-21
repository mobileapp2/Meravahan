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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.EditCustomer_Activity;
import in.rto.collections.activities.ImportRTOCustomer_Activity;
import in.rto.collections.activities.ImportVehicleCustomer_Activity;
import in.rto.collections.activities.ImportVehicleOtherCustomer_Activity;
import in.rto.collections.activities.Import_Banker_Customer_Activity;
import in.rto.collections.activities.LinkCustomerBanker_Activity;
import in.rto.collections.activities.LinkCustomerDealer_Activity;
import in.rto.collections.activities.LinkCustomerOther_Activity;
import in.rto.collections.activities.LinkCustomerRto_Activity;
import in.rto.collections.activities.ViewCustomer;
import in.rto.collections.activities.ViewImportCustomerBanker_Activity;
import in.rto.collections.activities.ViewImportCustomerOtherVehicle_Activity;
import in.rto.collections.activities.ViewImportCustomerVehicle_Activity;
import in.rto.collections.activities.ViewImportRTOCustomer_Activity;
import in.rto.collections.fragments.RTO_Customer_Fragment;
import in.rto.collections.fragments.Self_Fragment;
import in.rto.collections.fragments.Vehicle_Customer_Fragment;
import in.rto.collections.models.CustomerPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

        private List<CustomerPojo> resultArrayList;
        private Context context;
        private  String user_id;
        private String role_id;
    private UserSessionManager session;


    public CustomerAdapter(Context context, List<CustomerPojo> resultArrayList,String user_id) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.user_id = user_id;
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            role_id = json.getString("role_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CustomerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_customer, parent, false);
        CustomerAdapter.MyViewHolder myViewHolder = new CustomerAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomerAdapter.MyViewHolder holder, final int position) {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo = resultArrayList.get(position);
        final CustomerPojo finalcustomerPojo = customerPojo;
        if(customerPojo.getImportR().equals("ImportBanker")){
            holder.tv_vehicalownername.setText(customerPojo.getBorrower_name());
            holder.tv_renewaldate.setText(customerPojo.getVehicle_no());
        }else {
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
        }
        if(customerPojo.getImportR().equals("ImportAgent")) {
         holder.img_link.setVisibility(View.VISIBLE);
         holder.img_edit.setImageResource(R.drawable.import_r);
         holder.img_link.setImageResource(R.drawable.link);
         holder.tv_identify.setText("Created By - "+ customerPojo.getCreaterName()+" (RTO Agent)");
            holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewImportRTOCustomer_Activity.class);
            intent.putExtra("customerDetails", finalcustomerPojo);
            context.startActivity(intent);
        }
    });
}else if(customerPojo.getImportR().equals("ImportDealer")){
             holder.img_edit.setImageResource(R.drawable.import_r);
             holder.img_link.setImageResource(R.drawable.link);
             holder.img_link.setVisibility(View.VISIBLE);
             holder.tv_identify.setText("Created By - "+ customerPojo.getCreaterName()+" (Vehicle Dealer)");
             holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             Intent intent = new Intent(context, ViewImportCustomerVehicle_Activity.class);
             intent.putExtra("customerDetails", finalcustomerPojo);
             context.startActivity(intent);
        }
    });
}else if(customerPojo.getImportR().equals("ImportOther")){
            holder.img_edit.setImageResource(R.drawable.import_r);
            holder.img_link.setImageResource(R.drawable.link);
            holder.img_link.setVisibility(View.VISIBLE);
            holder.tv_identify.setText("Created By - "+ customerPojo.getCreaterName()+" (Other)");
            holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImportCustomerOtherVehicle_Activity.class);
                    intent.putExtra("customerDetails", finalcustomerPojo);
                    context.startActivity(intent);
                }
            });
        }else if(customerPojo.getImportR().equals("ImportBanker")){
         holder.img_edit.setImageResource(R.drawable.import_r);
         holder.img_link.setImageResource(R.drawable.link);
         holder.img_link.setVisibility(View.VISIBLE);
         holder.tv_identify.setText("Created By - "+ customerPojo.getCreaterName()+" (Banker)");
         holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(context, ViewImportCustomerBanker_Activity.class);
                 intent.putExtra("bankerdetails", finalcustomerPojo);
                 context.startActivity(intent);
             }
         });
     }else{
         holder.img_edit.setImageResource(R.drawable.edit);
         holder.tv_identify.setText("");
         holder.tv_identify.setVisibility(View.GONE);
         holder.img_link.setVisibility(View.GONE);
         holder.img_show.setVisibility(View.VISIBLE);
         holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewCustomer.class);
            intent.putExtra("customerDetails", finalcustomerPojo);
            context.startActivity(intent);
        }
    });
   }
        final CustomerPojo finalCustomerPojo = customerPojo;
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           if(finalCustomerPojo.getImportR().equals("ImportAgent")) {
               AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
               builder.setMessage("Are you sure you want to delete this item?");
               builder.setTitle("Alert");
               builder.setIcon(R.drawable.ic_alert_red_24dp);
               builder.setCancelable(false);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                       if (Utilities.isInternetAvailable(context)) {
                           new CustomerAdapter.DeleteCustomerDetails1().execute(finalCustomerPojo.getId());
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
   }else if(finalCustomerPojo.getImportR().equals("ImportDealer")){
               AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
               builder.setMessage("Are you sure you want to delete this item?");
               builder.setTitle("Alert");
               builder.setIcon(R.drawable.ic_alert_red_24dp);
               builder.setCancelable(false);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                       if (Utilities.isInternetAvailable(context)) {
                           new CustomerAdapter.DeleteCustomerDetails().execute(finalCustomerPojo.getId());
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
       }else if(finalCustomerPojo.getImportR().equals("ImportOther")){
               AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
               builder.setMessage("Are you sure you want to delete this item?");
               builder.setTitle("Alert");
               builder.setIcon(R.drawable.ic_alert_red_24dp);
               builder.setCancelable(false);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                       if (Utilities.isInternetAvailable(context)) {
                           new CustomerAdapter.DeleteCustomerDetails3().execute(finalCustomerPojo.getId());
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
           }else if(finalCustomerPojo.getImportR().equals("ImportBanker")){
               AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
               builder.setMessage("Are you sure you want to delete this item?");
               builder.setTitle("Alert");
               builder.setIcon(R.drawable.ic_alert_red_24dp);
               builder.setCancelable(false);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                       if (Utilities.isInternetAvailable(context)) {
                           new CustomerAdapter.DeleteBankerDetails().execute(finalCustomerPojo.getId());
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
           }else{
               AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
               builder.setMessage("Are you sure you want to delete this item?");
               builder.setTitle("Alert");
               builder.setIcon(R.drawable.ic_alert_red_24dp);
               builder.setCancelable(false);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                       if (Utilities.isInternetAvailable(context)) {
                           new CustomerAdapter.DeleteCustomerDetails2().execute(finalCustomerPojo.getId());
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

       }
   });

        holder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalCustomerPojo.getImportR().equals("ImportAgent")) {
                    if(finalCustomerPojo.getIsimport().equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("This record is already imported. Do you really want to import this record?");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, ImportRTOCustomer_Activity.class);
                                intent.putExtra("customerDetails", finalCustomerPojo);
                                context.startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    }else{
                        Intent intent = new Intent(context, ImportRTOCustomer_Activity.class);
                        intent.putExtra("customerDetails", finalCustomerPojo);
                        context.startActivity(intent);
                    }
                }else if(finalCustomerPojo.getImportR().equals("ImportDealer")){
                    if(finalCustomerPojo.getIsimport().equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("This record is already imported. Do you realy want to import this record?");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, ImportVehicleCustomer_Activity.class);
                                intent.putExtra("customerDetails", finalCustomerPojo);
                                context.startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    }else{
                        Intent intent = new Intent(context, ImportVehicleCustomer_Activity.class);
                        intent.putExtra("customerDetails", finalCustomerPojo);
                        context.startActivity(intent);
                    }
                }else if(finalCustomerPojo.getImportR().equals("ImportBanker")){
                    if(finalCustomerPojo.getIsimport().equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("This record is already imported. Do you realy want to import this record?");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, Import_Banker_Customer_Activity.class);
                                intent.putExtra("customerDetails", finalCustomerPojo);
                                context.startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    }else{
                        Intent intent = new Intent(context, Import_Banker_Customer_Activity.class);
                        intent.putExtra("customerDetails", finalCustomerPojo);
                        context.startActivity(intent);
                    }
                }else if(finalCustomerPojo.getImportR().equals("ImportOther")){
                  if(finalCustomerPojo.getIsimport().equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("This record is already imported. Do you realy want to import this record?");
                        builder.setIcon(R.drawable.ic_success_24dp);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, ImportVehicleOtherCustomer_Activity.class);
                                intent.putExtra("customerDetails", finalCustomerPojo);
                                context.startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog alertD = builder.create();
                        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
                        alertD.show();
                    }else{
                        Intent intent = new Intent(context, ImportVehicleOtherCustomer_Activity.class);
                        intent.putExtra("customerDetails", finalCustomerPojo);
                        context.startActivity(intent);
                    }
                }
                else{
                    Intent intent = new Intent(context, EditCustomer_Activity.class);
                    intent.putExtra("customerDetails", finalCustomerPojo);
                    context.startActivity(intent);
                }
            }
        });
        holder.img_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalCustomerPojo.getImportR().equals("ImportDealer")){
                    Intent intent = new Intent(context, LinkCustomerDealer_Activity.class);
                    intent.putExtra("customerDetails", finalCustomerPojo);
                    context.startActivity(intent);
                }else if(finalCustomerPojo.getImportR().equals("ImportAgent")){
                    Intent intent = new Intent(context, LinkCustomerRto_Activity.class);
                    intent.putExtra("customerDetails", finalCustomerPojo);
                    context.startActivity(intent);
                }else if(finalCustomerPojo.getImportR().equals("ImportBanker")){
                    Intent intent = new Intent(context, LinkCustomerBanker_Activity.class);
                    intent.putExtra("customerDetails", finalCustomerPojo);
                    context.startActivity(intent);
                }else if(finalCustomerPojo.getImportR().equals("ImportOther")){
                    Intent intent = new Intent(context, LinkCustomerOther_Activity.class);
                    intent.putExtra("customerDetails", finalCustomerPojo);
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
                if (!finalCustomerPojo.getVehicle_image().equals("")) {
                    Picasso.with(context)
                            .load(finalCustomerPojo.getVehicle_image_url())
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
            img_link =view.findViewById(R.id.img_link);
            img_vehicle =view.findViewById(R.id.img_vehicle);
            tv_identify = view.findViewById(R.id.tv_identify);
            img_show = view.findViewById(R.id.img_show);
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
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

                        new Self_Fragment.GetCustomerList().execute(user_id);

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

    public class DeleteCustomerDetails extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isdeleteDealer");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
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

                        new Vehicle_Customer_Fragment.GetCustomerList().execute(user_id);
                        new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role_id);
                        new Vehicle_Customer_Fragment.GetBankerList().execute(user_id,role_id);
                        new Vehicle_Customer_Fragment.GetOtherVehicleList().execute(user_id);

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
    public class DeleteCustomerDetails1 extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isdeleteRTO");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
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

                        new Vehicle_Customer_Fragment.GetCustomerList().execute(user_id);
                        new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role_id);
                        new Vehicle_Customer_Fragment.GetBankerList().execute(user_id,role_id);
                        new Vehicle_Customer_Fragment.GetOtherVehicleList().execute(user_id);

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

    public class DeleteCustomerDetails2 extends AsyncTask<String, Void, String> {
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
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
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

                      new Self_Fragment.GetCustomerList().execute(user_id);

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
    public class DeleteCustomerDetails3 extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "isdeleteOther");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
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

                        new Vehicle_Customer_Fragment.GetCustomerList().execute(user_id);
                        new Vehicle_Customer_Fragment.GetCustomerList1().execute(user_id,role_id);
                        new Vehicle_Customer_Fragment.GetBankerList().execute(user_id,role_id);
                        new Vehicle_Customer_Fragment.GetOtherVehicleList().execute(user_id);
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

}

