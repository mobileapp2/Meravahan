package in.rto.collections.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.rto.collections.R;
import in.rto.collections.activities.RtoAgent_Activity;
import in.rto.collections.models.RTOAgentPojo;
import in.rto.collections.utilities.ApplicationConstants;
import in.rto.collections.utilities.UserSessionManager;
import in.rto.collections.utilities.Utilities;
import in.rto.collections.utilities.WebServiceCalls;

public class getRtoAgentListAdapter extends RecyclerView.Adapter<getRtoAgentListAdapter.MyViewHolder> {
    @NonNull

    private List<RTOAgentPojo> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String user_id;


    public getRtoAgentListAdapter(Context context, List<RTOAgentPojo> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            user_id = json.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_rtoagent, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RTOAgentPojo rtoagentListDetails = new RTOAgentPojo();
        rtoagentListDetails = resultArrayList.get(position);
        final RTOAgentPojo finalrtoagentListDetails = rtoagentListDetails;

        holder.tv_name.setText(rtoagentListDetails.getName());
        holder.tv_alias.setText(rtoagentListDetails.getMobile());

        holder.ll_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edt_name = new EditText(context);
                edt_name.setHint("Name");
                final EditText edt_alias = new EditText(context);
                edt_alias.setHint("Alias");
                final EditText edt_mobile = new EditText(context);
                edt_mobile.setHint("Mobile");
                edt_mobile.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(10);
                edt_mobile.setFilters(filterArray);
                edt_name.setText(finalrtoagentListDetails.getName());
                edt_name.setSelection(finalrtoagentListDetails.getName().length());
                edt_alias.setText(finalrtoagentListDetails.getAlias());
                edt_alias.setSelection(finalrtoagentListDetails.getAlias().length());
                edt_mobile.setText(finalrtoagentListDetails.getMobile());
                edt_mobile.setSelection(finalrtoagentListDetails.getMobile().length());
                float dpi = context.getResources().getDisplayMetrics().density;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setTitle("Edit Rto Agent");

                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isInternetAvailable(context)) {
                            new getRtoAgentListAdapter.EditRtoAgent().execute(edt_name.getText().toString().trim(), edt_alias.getText().toString().trim(),
                                    user_id,
                                    finalrtoagentListDetails.getId(), edt_mobile.getText().toString().trim());
                        } else {
                            Utilities.showMessageString(context, "Please Check Internet Connection");
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Utilities.isInternetAvailable(context)) {
                            new getRtoAgentListAdapter.DeleteRtoAgent().execute(finalrtoagentListDetails.getId());
                        } else {
                            Utilities.showMessageString(context, "Please Check Internet Connection");
                        }
                    }
                });

                final AlertDialog alertD = builder.create();

                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(edt_name);
                ll.addView(edt_alias);
                ll.addView(edt_mobile);
                //alertD.setView(ll);

                alertD.setView(ll, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                //alertD.setView(edt_alias, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;

                alertD.show();

                edt_name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }

                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public TextView tv_alias;
        CardView ll_mainlayout;
        View view1;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_alias = view.findViewById(R.id.tv_alias);
            view1 = view.findViewById(R.id.view);
            ll_mainlayout = view.findViewById(R.id.ll_mainlayout);
        }
    }

    public class EditRtoAgent extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "updateRTOAgent");
            obj.addProperty("name", params[0]);
            obj.addProperty("alias", params[1]);
            obj.addProperty("user_id", params[2]);
            obj.addProperty("id", params[3]);
            obj.addProperty("mobile", params[4]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.MASTERAPI, obj.toString());
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
                        // new Clients_Fragment.GetClientList().execute(user_id);
                        new RtoAgent_Activity.GetRtoAgentList().execute(user_id);
                    } else {
                        Utilities.showAlertDialog(context, "Alert", message, false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DeleteRtoAgent extends AsyncTask<String, Void, String> {
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
            obj.addProperty("type", "deleteRTOAgent");
            obj.addProperty("id", params[0]);
            res = WebServiceCalls.JSONAPICall(ApplicationConstants.MASTERAPI, obj.toString());
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
                        new RtoAgent_Activity.GetRtoAgentList().execute(user_id);
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
